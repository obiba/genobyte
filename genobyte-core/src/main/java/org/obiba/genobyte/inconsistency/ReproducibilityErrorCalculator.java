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

import org.obiba.bitwise.BitwiseRecordManager;
import org.obiba.bitwise.Dictionary;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.genobyte.GenotypingRecordStore;
import org.obiba.genobyte.model.DefaultGenotypingField;
import org.obiba.genobyte.model.SnpCall;

/**
 * Computes reproducibility errors.
 * <p/>
 * Using a {@link ComparableRecordProvider} instance, this class is able to compare
 * genotypes and produce {@link ReproducibilityErrors} instances.
 *
 * @param <K> the type of the key of a record in the {@link GenotypingRecordStore}
 */
public class ReproducibilityErrorCalculator<K> {

  /** The store used to obtain the genotypes */
  private GenotypingRecordStore<K, ?, ?> store_ = null;

  /** The strategy used to handle reported errors */
  private ReproducibilityErrorCountingStrategy<K> errorCountingStrategy_ = null;

  /** The provider of records that should be compared */
  private ComparableRecordProvider provider_ = null;

  /**
   * Constructs a calculator using the specified store (from which {@link DefaultGenotypingField#CALLS} will be obtained).
   * @param store the store used to obtain the calls
   */
  public ReproducibilityErrorCalculator(GenotypingRecordStore<K, ?, ?> store) {
    store_ = store;
  }

  /**
   * The instance of {@link ReproducibilityErrorCountingStrategy} that will handle computed errors.
   * @param strategy the counting strategy instance.
   */
  public void setCountingStrategy(ReproducibilityErrorCountingStrategy<K> strategy) {
    errorCountingStrategy_ = strategy;
  }

  /**
   * The instance of {@link ComparableRecordProvider} that provides the records to be compared.
   * @param provider the instance used to determine which records to compare
   */
  public void setComparableRecordProvider(ComparableRecordProvider provider) {
    provider_ = provider;
  }

  /**
   * Starts the reproducibility error computation process.
   */
  public void calculate() {
    if(provider_ == null) {
      provider_ = store_.getComparableRecordProvider();
    }

    if(provider_ == null) {
      return;
    }
    QueryResult references = provider_.getComparableReferenceRecords();
    if(references == null) {
      // No reference records: nothing to do.
      return;
    }

    BitwiseRecordManager<K, ?> sampleManager = store_.getRecordManager();
    for(int referenceIndex = references.next(0);
        referenceIndex != -1; referenceIndex = references.next(referenceIndex + 1)) {
      K referenceKey = sampleManager.getKey(referenceIndex);
      QueryResult rep = provider_.getComparableRecords(referenceIndex);
      if(rep == null) {
        // No comparable records
        continue;
      }
      for(int replicateIndex = rep.next(0); replicateIndex != -1; replicateIndex = rep.next(replicateIndex + 1)) {
        K replicateKey = sampleManager.getKey(replicateIndex);
        ReproducibilityErrors<K> errors = reproErrors(referenceKey, replicateKey);
        if(errors == null) {
          // Either reference of replicate has no calls
          continue;
        }
        errors.setReferenceIndex(referenceIndex);
        errors.setReferenceKey(referenceKey);
        errors.setReplicateIndex(replicateIndex);
        errors.setReplicateKey(replicateKey);
        errorCountingStrategy_.countInconsistencies(errors);
      }
    }
  }

  /**
   * Identifies all reproducibility errors between the two specified records.
   *
   * @param reference the reference record's key
   * @param replicate the replicate record's key
   * @return an instance of {@link ReproducibilityErrors} that holds all identified errors. Null is returned if either the reference or the replicate has no calls to compare.
   */
  protected ReproducibilityErrors<K> reproErrors(K reference, K replicate) {
    Field refCalls = store_.getGenotypingField(DefaultGenotypingField.COMPARABLE_CALLS.getName(), reference);
    if(refCalls == null) {
      refCalls = store_.getGenotypingField(DefaultGenotypingField.CALLS.getName(), reference);
    }
    Field repCalls = store_.getGenotypingField(DefaultGenotypingField.COMPARABLE_CALLS.getName(), replicate);
    if(repCalls == null) {
      repCalls = store_.getGenotypingField(DefaultGenotypingField.CALLS.getName(), replicate);
    }
    if(refCalls == null || repCalls == null) {
      return null;
    }

    Dictionary<SnpCall> d = refCalls.getDictionary();

    // Filter failed calls and nulls
    QueryResult refCallsFilter = refCalls.query(d.lookup(SnpCall.U)).or(refCalls.query(null));
    QueryResult repCallsFilter = repCalls.query(d.lookup(SnpCall.U)).or(repCalls.query(null));

    QueryResult tests = refCallsFilter.not().and(repCallsFilter.not());
    QueryResult errors = refCalls.diff(repCalls).and(tests);
    ReproducibilityErrors<K> inconsistencies = new ReproducibilityErrors<K>();
    inconsistencies.setInconsistencies(errors);
    inconsistencies.setTests(tests);
    return inconsistencies;
  }

}
