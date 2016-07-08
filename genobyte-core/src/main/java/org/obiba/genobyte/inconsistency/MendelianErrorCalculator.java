/*******************************************************************************
 * Copyright 2007(c) Genome Quebec. All rights reserved.
 * <p>
 * This file is part of GenoByte.
 * <p>
 * GenoByte is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * <p>
 * GenoByte is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
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
   * Constructs a calculator using the specified store (from which {@link DefaultGenotypingField#CALLS} will be obtained.)
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

    //For each sample which is a child, fetch parents
    for(int childIndex = children.next(0); childIndex != -1; childIndex = children.next(childIndex + 1)) {
      K childKey = sampleManager.getKey(childIndex);

      //Fetch all parent records. There might be many father/mother records for a given child, because of replicates.
      QueryResult motherRecords = provider_.getMotherRecords(childIndex);
      QueryResult fatherRecords = provider_.getFatherRecords(childIndex);

      // If child has no parents, skip to next child
      if(motherRecords == null && fatherRecords == null) {
        continue;
      }

      // Vectors of tested parents. Used to filter out already tested individuals in trios
      BitVector countedMothers = new BitVector(samples_.getStore().getCapacity());
      BitVector countedFathers = new BitVector(samples_.getStore().getCapacity());

      //Tests will be done in this order:
      //  For each trio with a genotype call field for father, mother, child:
      //    Check for "trio" mendelian errors for all assays where genotypes exist for everybody
      //    Check for "duo" mendelian errors between father and child for assays where mother's call was null
      //    Check for "duo" mendelian errors between mother and child for assays where father's call was null
      for(int motherIndex = motherRecords.next(0);
          motherIndex != -1; motherIndex = motherRecords.next(motherIndex + 1)) {
        K motherKey = sampleManager.getKey(motherIndex);
        for(int fatherIndex = fatherRecords.next(0);
            fatherIndex != -1; fatherIndex = fatherRecords.next(fatherIndex + 1)) {
          K fatherKey = sampleManager.getKey(fatherIndex);
          MendelErrorsResult errorsStructure = mendelErrors(childKey, motherKey, fatherKey, null);

          if(errorsStructure != null) {
            MendelianErrors<K> errors = errorsStructure.getMendelianErrors();

            //Some assays were already tested for theses parents. Remove from the list of parents to recheck completely as duos.
            countedMothers.set(motherIndex);
            countedFathers.set(fatherIndex);

            errors.setChildIndex(childIndex);
            errors.setChildKey(childKey);
            errors.setMotherIndex(motherIndex);
            errors.setMotherKey(motherKey);
            errors.setFatherIndex(fatherIndex);
            errors.setFatherKey(fatherKey);
            errorCountingStrategy_.countInconsistencies(errors);

            //If there are remaining untested genotypes, test them here.
            QueryResult fatherChildAsDuo = errorsStructure.getUnapplicableMotherGenotypes().copy()
                .andNot(errorsStructure.getUnapplicableFatherGenotypes());
            QueryResult motherChildAsDuo = errorsStructure.getUnapplicableFatherGenotypes().copy()
                .andNot(errorsStructure.getUnapplicableMotherGenotypes());
            //Test the null mother genotype as a duo between child and father          .
            if(fatherChildAsDuo.count() > 0) {
              MendelErrorsResult errorsStructureFatherDuo = mendelErrors(childKey, fatherKey, null, fatherChildAsDuo);

              if(errorsStructureFatherDuo != null) {
                MendelianErrors<K> errorsFatherDuo = errorsStructureFatherDuo.getMendelianErrors();
                errorsFatherDuo.setChildIndex(childIndex);
                errorsFatherDuo.setChildKey(childKey);
                errorsFatherDuo.setFatherIndex(fatherIndex);
                errorsFatherDuo.setFatherKey(fatherKey);
                errorCountingStrategy_.countInconsistencies(errorsFatherDuo);
              }
            }
            //Test the null father genotype as a duo between child and mother.
            if(motherChildAsDuo.count() > 0) {
              MendelErrorsResult errorsStructureMotherDuo = mendelErrors(childKey, motherKey, null, motherChildAsDuo);

              if(errorsStructureMotherDuo != null) {
                MendelianErrors<K> errorsMotherDuo = errorsStructureMotherDuo.getMendelianErrors();
                errorsMotherDuo.setChildIndex(childIndex);
                errorsMotherDuo.setChildKey(childKey);
                errorsMotherDuo.setMotherIndex(motherIndex);
                errorsMotherDuo.setMotherKey(motherKey);
                errorCountingStrategy_.countInconsistencies(errorsMotherDuo);
              }
            }
          }
        }
      }

      //Remove from the list of mother records the ones for which we found trio errors already
      BitVectorQueryResult filter = new BitVectorQueryResult(countedMothers);
      motherRecords.andNot(filter);

      // Create mendelian errors on remaining child-mother relationships
      for(int motherIndex = motherRecords.next(0);
          motherIndex != -1; motherIndex = motherRecords.next(motherIndex + 1)) {
        K motherKey = sampleManager.getKey(motherIndex);
        MendelErrorsResult errorsStructure = mendelErrors(childKey, motherKey, null, null);
        if(errorsStructure != null) {
          MendelianErrors<K> errors = errorsStructure.getMendelianErrors();
          errors.setChildIndex(childIndex);
          errors.setChildKey(childKey);
          errors.setMotherIndex(motherIndex);
          errors.setMotherKey(motherKey);
          errorCountingStrategy_.countInconsistencies(errors);
        }
      }

      //Remove from the list of father records the ones for which we found trio errors already
      filter = new BitVectorQueryResult(countedFathers);
      fatherRecords.andNot(filter);

      // Create mendelian errors on remaining child-father relationships
      for(int fatherIndex = fatherRecords.next(0);
          fatherIndex != -1; fatherIndex = fatherRecords.next(fatherIndex + 1)) {
        K fatherKey = sampleManager.getKey(fatherIndex);
        MendelErrorsResult errorsStructure = mendelErrors(childKey, fatherKey, null, null);
        if(errorsStructure != null) {
          MendelianErrors<K> errors = errorsStructure.getMendelianErrors();
          errors.setChildIndex(childIndex);
          errors.setChildKey(childKey);
          errors.setFatherIndex(fatherIndex);
          errors.setFatherKey(fatherKey);
          errorCountingStrategy_.countInconsistencies(errors);
        }
      }

    } //End of "for each record that is a child"
  }

  protected class MendelErrorsResult {
    MendelianErrors<K> errors = null;

    QueryResult unapplicableFatherGenotypes = null;

    QueryResult unapplicableMotherGenotypes = null;

    public MendelErrorsResult(MendelianErrors<K> pErrors, QueryResult pUnapplicableFatherGenotypes,
        QueryResult pUnapplicableMotherGenotypes) {
      errors = pErrors;
      unapplicableFatherGenotypes = pUnapplicableFatherGenotypes;
      unapplicableMotherGenotypes = pUnapplicableMotherGenotypes;
    }

    public MendelianErrors<K> getMendelianErrors() {
      return errors;
    }

    public QueryResult getUnapplicableFatherGenotypes() {
      return unapplicableFatherGenotypes;
    }

    public QueryResult getUnapplicableMotherGenotypes() {
      return unapplicableMotherGenotypes;
    }
  }

  /**
   * Identifies all mendelian errors in the specified trio.
   *
   * @param childId the child record's key
   * @param motherId the mother record's key
   * @param fatherId the father record's key
   * @param mask the genotypes to be ignored in this error check
   * @return an instance of {@link MendelianErrors} that holds all identified errors. Null is returned if either the child, the mother or the father has no calls.
   */
  protected MendelErrorsResult mendelErrors(K childId, K motherId, K fatherId, QueryResult mask) {
    Field child = samples_.getGenotypingField(DefaultGenotypingField.COMPARABLE_CALLS.getName(), childId);
    if(child == null) child = samples_.getGenotypingField(DefaultGenotypingField.CALLS.getName(), childId);
    if(child == null) {
      return null;
    }

    Field mother = null;
    QueryResult motherNullCalls = null;
    if(motherId != null) {
      mother = samples_.getGenotypingField(DefaultGenotypingField.COMPARABLE_CALLS.getName(), motherId);
      if(mother == null) mother = samples_.getGenotypingField(DefaultGenotypingField.CALLS.getName(), motherId);
      if(mother == null) {
        return null;
      }
      motherNullCalls = new BitVectorQueryResult(mother.getNulls());
    }

    Field father = null;
    QueryResult fatherNullCalls = null;
    if(fatherId != null) {
      father = samples_.getGenotypingField(DefaultGenotypingField.COMPARABLE_CALLS.getName(), fatherId);
      if(father == null) father = samples_.getGenotypingField(DefaultGenotypingField.CALLS.getName(), fatherId);
      if(father == null) {
        return null;
      }
      fatherNullCalls = new BitVectorQueryResult(father.getNulls());
    }

    //Error accumulator from all tests
    QueryResult mendelErrors = null;
    QueryResult mendelTests = null;

    QueryResult motherErrors = mendel(child, mother);
    QueryResult motherTests = tests(child, mother);
    //If errors were found between mother and child
    if(motherErrors != null) {
      mendelErrors = motherErrors;
      mendelTests = motherTests;
    }

    QueryResult fatherErrors = mendel(child, father);
    QueryResult fatherTests = tests(child, father);
    //If errors were found between father and child
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
      //Testing specifically for Child=H, Parents=A and Child=H, Parents=B errors
      Dictionary<SnpCall> d = child.getDictionary();
      BitVector a = d.lookup(SnpCall.A);
      BitVector b = d.lookup(SnpCall.B);
      BitVector h = d.lookup(SnpCall.H);

      // Child H Mother A Father A
      QueryResult trioErrors = null;
      trioErrors = child.query(h).and(mother.query(a).and(father.query(a)));

      // Child H Mother B Father B
      trioErrors.or(child.query(h).and(mother.query(b).and(father.query(b))));

      if(mendelErrors != null) {
        mendelErrors.or(trioErrors);
      } else {
        mendelErrors = trioErrors;
      }

      //We should remove the nulls from both parents to test later as duos
      mendelErrors.andNot(fatherNullCalls);
      mendelErrors.andNot(motherNullCalls);
      mendelTests.andNot(fatherNullCalls);
      mendelTests.andNot(motherNullCalls);
    }

    if(mask != null) {
      mendelErrors.and(mask);
      mendelTests.and(mask);
    }

    if(mendelErrors == null) return null;

    //All tests are finished. Prepare error structure.
    MendelianErrors<K> errors = new MendelianErrors<K>();
    errors.setInconsistencies(mendelErrors);
    errors.setTests(mendelTests);
//    return errors;

    MendelErrorsResult errorStructure = new MendelErrorsResult(errors, fatherNullCalls, motherNullCalls);
    return errorStructure;
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
