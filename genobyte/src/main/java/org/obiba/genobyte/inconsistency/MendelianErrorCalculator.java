/*******************************************************************************
 * Copyright 2007(c) Génome Québec. All rights reserved.
 * 
 * This file is part of GenoByte.
 * 
 * GenoByte is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * GenoByte is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 *******************************************************************************/
package org.obiba.genobyte.inconsistency;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.BitwiseRecordManager;
import org.obiba.bitwise.Dictionary;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.bitwise.util.BitVectorQueryResult;
import org.obiba.genobyte.GenotypingRecordStore;
import org.obiba.genobyte.model.DefaultGenotypingField;
import org.obiba.genobyte.model.SnpCall;


/**
 * Default implementation of a mendelian error calculator.
 * <p/>
 * The implementation iterates on all possible trios and duos. First, all possible child-mother-father combinations,
 * are computed for mendelian errors. For each trio computed, the mother and father records are removed from 
 * the remaining duos. Then, errors are computed for each remaining child-parent duo.
 */
public class MendelianErrorCalculator<K> {

  /** The store used to obtain the genotypes */
  private GenotypingRecordStore<K, ?, ?> samples_ = null;
  /** The error handler */
  private MendelianErrorCountingStrategy<K> errorCountingStrategy_ = null;
  /** Provides the record combinations to be analysed */
  private MendelianRecordTrioProvider provider_ = null;
  
  /**
   * Constructs a calculator using the specified store (from which {@link DefaultGenotypingField#CALLS} will be obtained.
   * @param store the store used to obtain the calls
   */
  public MendelianErrorCalculator(GenotypingRecordStore<K, ?, ?> store) {
    samples_ = store;
  }
  
  /**
   * The instance of {@link MendelianErrorCountingStrategy} that will handle computed errors.
   * @param strategy the counting strategy instance.
   */
  public void setCountingStrategy(MendelianErrorCountingStrategy<K> strategy) {
    errorCountingStrategy_ = strategy;
  }

  /**
   * The instance of {@link MendelianRecordTrioProvider} that provides the records to be compared.
   * @param provider the instance used to determine which records to compare
   */
  public void setRecordProvider(MendelianRecordTrioProvider provider) {
    provider_ = provider;
  }

  /**
   * Starts the mendelian error computation process.
   */
  public void calculate() {
    if(provider_ == null) {
      return;
    }
    QueryResult children = provider_.getChildRecords();
    if(children == null) {
      // No children records: nothing to do.
      return;
    }

    BitwiseRecordManager<K, ?> sampleManager = samples_.getRecordManager();
    for(int childIndex = children.next(0); childIndex != -1; childIndex = children.next(childIndex + 1)) {
      K childKey = sampleManager.getKey(childIndex);

      QueryResult motherRecords = provider_.getMotherRecords(childIndex);
      QueryResult fatherRecords = provider_.getFatherRecords(childIndex);

      if(motherRecords == null && fatherRecords == null) {
        // No comparable records
        continue;
      }

      // Vectors of tested parents. Used to filter out already tested individuals in trios
      BitVector countedMothers = new BitVector(samples_.getStore().getCapacity());
      BitVector countedFathers = new BitVector(samples_.getStore().getCapacity());

      for(int motherIndex = motherRecords.next(0); motherIndex != -1; motherIndex = motherRecords.next(motherIndex+1)) {
        K motherKey = sampleManager.getKey(motherIndex);
        for(int fatherIndex = fatherRecords.next(0); fatherIndex != -1; fatherIndex = fatherRecords.next(fatherIndex+1)) {
          K fatherKey = sampleManager.getKey(fatherIndex);
          MendelianErrors<K> errors = mendelErrors(childKey, motherKey, fatherKey);
          if(errors != null) {
            countedMothers.set(motherIndex);
            countedFathers.set(fatherIndex);
            errors.setChildIndex(childIndex);
            errors.setChildKey(childKey);
            errors.setMotherIndex(motherIndex);
            errors.setMotherKey(motherKey);
            errors.setFatherIndex(fatherIndex);
            errors.setFatherKey(fatherKey);
            errorCountingStrategy_.countInconsistencies(errors);
          }
        }
      }

      BitVectorQueryResult filter = new BitVectorQueryResult(countedMothers);
      motherRecords.andNot(filter);

      // Create mendelian errors on remaining child-mother relationships
      for(int motherIndex = motherRecords.next(0); motherIndex != -1; motherIndex = motherRecords.next(motherIndex+1)) {
        K motherKey = sampleManager.getKey(motherIndex);
        MendelianErrors<K> errors = mendelErrors(childKey, motherKey, null);
        if(errors != null) {
          errors.setChildIndex(childIndex);
          errors.setChildKey(childKey);
          errors.setMotherIndex(motherIndex);
          errors.setMotherKey(motherKey);
          errorCountingStrategy_.countInconsistencies(errors);
        }
      }

      filter = new BitVectorQueryResult(countedFathers);
      fatherRecords.andNot(filter);

      // Create mendelian errors on remaining child-father relationships
      for(int fatherIndex = fatherRecords.next(0); fatherIndex != -1; fatherIndex = fatherRecords.next(fatherIndex+1)) {
        K fatherKey = sampleManager.getKey(fatherIndex);
        MendelianErrors<K> errors = mendelErrors(childKey, fatherKey, null);
        if(errors != null) {
          errors.setChildIndex(childIndex);
          errors.setChildKey(childKey);
          errors.setFatherIndex(fatherIndex);
          errors.setFatherKey(fatherKey);
          errorCountingStrategy_.countInconsistencies(errors);
        }
      }
    }
  }

  /**
   * Identifies all mendelian errors in the specified trio.
   * 
   * @param childId the child record's key
   * @param motherId the mother record's key
   * @param fatherId the father record's key
   * @return an instance of {@link MendelianErrors} that holds all identified errors. Null is returned if either the child, the mother or the father has no calls.
   */
  protected MendelianErrors<K> mendelErrors(K childId, K motherId, K fatherId) {
    Field child = samples_.getGenotypingField(DefaultGenotypingField.COMPARABLE_CALLS.getName(), childId);
    if(child == null) child = samples_.getGenotypingField(DefaultGenotypingField.CALLS.getName(), childId);
    if(child == null) {
      return null;
    }

    Field mother = null;
    if(motherId != null) {
      mother = samples_.getGenotypingField(DefaultGenotypingField.COMPARABLE_CALLS.getName(), motherId);
      if(mother == null) mother = samples_.getGenotypingField(DefaultGenotypingField.CALLS.getName(), motherId);
      if(mother == null) {
        return null;
      }
    }

    Field father = null;
    if(fatherId != null) {
      father = samples_.getGenotypingField(DefaultGenotypingField.COMPARABLE_CALLS.getName(), fatherId);
      if(father == null) father = samples_.getGenotypingField(DefaultGenotypingField.CALLS.getName(), fatherId);
      if(father == null) {
        return null;
      }
    }

    QueryResult mendelErrors = null;
    QueryResult mendelTests = null;

    QueryResult motherErrors = mendel(child, mother);
    QueryResult motherTests = tests(child, mother);
    if(motherErrors != null) {
      mendelErrors = motherErrors;
      mendelTests = motherTests;
    }

    QueryResult fatherErrors = mendel(child, father);
    QueryResult fatherTests = tests(child, father);
    if(fatherErrors != null) {
      if(mendelErrors != null) {
        mendelErrors.or(fatherErrors);
        mendelTests.or(fatherTests);
      } else {
        mendelErrors = fatherErrors;
        mendelTests = fatherTests;
      }
    }

    if(mother != null && father != null) {
      Dictionary<SnpCall> d = child.getDictionary();
      BitVector a = d.lookup(SnpCall.A);
      BitVector b = d.lookup(SnpCall.B);
      BitVector h = d.lookup(SnpCall.H);

      // Child H Mother A Father A
      QueryResult trioErrors = null;
      trioErrors = child.query(h)
                    .and(mother.query(a)
                    .and(father.query(a)));

      // Child H Mother B Father B
      trioErrors.or(child.query(h)
                     .and(mother.query(b)
                     .and(father.query(b))));

      if(mendelErrors != null) {
        mendelErrors.or(trioErrors);
      } else {
        mendelErrors = trioErrors;
      }
    }

    if(mendelErrors == null) return null;

    MendelianErrors<K> errors = new MendelianErrors<K>();
    errors.setInconsistencies(mendelErrors);
    errors.setTests(mendelTests);
    return errors;
  }

  /**
   * Identifies all mendelian errors in the specified duo.
   * 
   * @param child the child record's key
   * @param parent the parent record's key
   * @return an instance of {@link MendelianErrors} that holds all identified errors. Null is returned if either the child or the parent has no calls.
   */
  protected QueryResult mendel(Field child, Field parent) {
    if(child == null || parent == null) {
      return null;
    }
    Dictionary<SnpCall> d = child.getDictionary();
    BitVector a = d.lookup(SnpCall.A);
    BitVector b = d.lookup(SnpCall.B);

    // Child A Parent B
    QueryResult errors = child.query(a).and(parent.query(b));
    // Child B Parent A
    errors.or(child.query(b).and(parent.query(a)));

    return errors;
  }

  /**
   * Returns the number of compared pairs of genotypes between a child and its parent
   * @param child the calls field of the child
   * @param parent the calls field of the parent
   * @return a vector with ones for every call compared
   */
  protected QueryResult tests(Field child, Field parent) {
    if(child == null || parent == null) {
      return null;
    }

    Dictionary<SnpCall> d = child.getDictionary();
    BitVector u = d.lookup(SnpCall.U);

    // All calls that are not U
    QueryResult childTestable = child.query(u).not();
    QueryResult parentTestable = parent.query(u).not();

    // All calls that are not U in both the child and the parent
    return childTestable.and(parentTestable);
  }
}
