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
package org.obiba.genobyte.statistic;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.VolatileField;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.bitwise.schema.defaultDict.DefaultDictionaryFactory;
import org.obiba.bitwise.util.BitVectorQueryResult;
import org.obiba.genobyte.GenotypingRecordStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Calculates statistical runs and holds the pool of calculated results.
 *
 * @param <K>  the key for the store using this <tt>StatsPool</tt>.
 * @param <TK> the key for the transposed store.
 */
public class StatsPool<K, TK> {

  private final Logger log = LoggerFactory.getLogger(StatsPool.class);

  private Map<String, Object> pool_ = new HashMap<String, Object>();

  private StatsRunDefinition run_ = null;

  private QueryResult recordMask_ = null;

  private QueryResult transposedMask_ = null;

  private GenotypingRecordStore<K, ?, ?> rs_;

  private DefaultDictionaryFactory ddf_ = null;

  public StatsPool(GenotypingRecordStore<K, ?, ?> pRs, StatsRunDefinition pRun) {
    rs_ = pRs;
    run_ = pRun;
    ddf_ = new DefaultDictionaryFactory();
    resetMasks();
  }

  /**
   * Sets the masks to consider all records.
   */
  public void resetMasks() {
    recordMask_ = new BitVectorQueryResult(rs_.getStore().all());
    transposedMask_ = new BitVectorQueryResult(rs_.getTransposedStore().getStore().all());
  }

  /**
   * Empties the content of the parameters pool.
   */
  public void empty() {
    pool_.clear();
  }

  /**
   * Set values for the initial set of parameters used in statistic calculation.
   *
   * @param pValues the map of values identified by an unique name.
   */
  public void setPredefinedValues(Map<String, Object> pValues) {
    pool_.putAll(pValues);
  }

  /**
   * Gives to the pool the records on which the <tt>Statistic</tt> implementations calculate method will be ran.
   *
   * @param pQr the <tt>QueryResult</tt> containing all records to be considered in calculation.
   */
  public void setRecordMask(QueryResult pQr) {
    recordMask_ = pQr;
  }

  /**
   * Gives to the pool the records on which the <tt>Statistic</tt> implementations calculate method will be ran.
   *
   * @param pSet the <tt>Set</tt> of unique keys for all records to be considered in calculation.
   */
  public void setRecordMask(Set<K> pSet) {
    BitVector recordsBv = new BitVector(rs_.getStore().getCapacity());
    for (K key : pSet) {
      int index = rs_.getRecordManager().getIndexFromKey(key);
      recordsBv.set(index);
    }
    recordMask_ = new BitVectorQueryResult(recordsBv);
  }

  /**
   * Returns the mask used in this pool to select the records on which statistics will be calculated.
   *
   * @return a <tt>BitVector</tt> of the selected records mask.
   */
  public QueryResult getRecordMask() {
    return recordMask_;
  }

  /**
   * Sets a mask on the transposed store to select the records used in statistics calculation.
   *
   * @param pQr the record mask in a <tt>QueryResult</tt> format.
   */
  public void setTransposedMask(QueryResult pQr) {
    transposedMask_ = pQr;
  }

  /**
   * Returns the record mask applied to the transposed store.
   *
   * @return the record mask for the transposed store.
   */
  public QueryResult getTransposedMask() {
    return transposedMask_;
  }

  /**
   * Returns the pool containing all data processed inputed into and output from statistical calculation.
   *
   * @return the pool of data.
   */
  public Map<String, Object> getPool() {
    return pool_;
  }

  /**
   * Returns the <tt>GenotypingRecordStore</tt> in use by the <tt>StatsPool</tt> instance.
   *
   * @return the record store.
   */
  public GenotypingRecordStore<K, ?, ?> getGenotypingRecordStore() {
    return rs_;
  }

  /**
   * Calculate all statistics in the correct order, as defined by the <tt>StatsRunDefinition</tt> object.
   */
  public void calculate() {
    log.debug("Starting calculation for run {}", this.run_);
    log.debug("Records included [{}]", this.recordMask_.count());
    log.debug("Transposed records included [{}]", this.transposedMask_.count());

    log.debug("Run requires {} iterations", (this.run_.getMaxIteration() + 1));
    //Run through all iterations
    for (int i = 0; i <= run_.getMaxIteration(); i++) {
      log.debug("Iteration {}", (i + 1));

      //If there are stats that need transposed fields.
      List<RecordStatistic> tStats = run_.getRecordStats(i);
      if (tStats.size() > 0) {
        log.debug("Computing {} RecordStatistics", tStats.size());
        calculateRecordStats(tStats);
      }

      //Calculate all stats that do not need transposed fields.
      List<FieldStatistic> ntStats = run_.getFieldStats(i);
      if (ntStats.size() > 0) {
        log.debug("Computing {} FieldStatistics", ntStats.size());
        Map<String, Object> params = new HashMap<String, Object>();
        for (FieldStatistic s : ntStats) {
          prepareStatisticFields(s, null, params);
          s.calculate(this, params, recordMask_);
          params.clear();
        }
      }
    }
  }

  /**
   * Calculates all the <tt>RecordStatistic</tt> instances provided in parameter, by iterating on all store records.
   *
   * @param pStats the list of statistics to be computed.
   */
  private void calculateRecordStats(List<RecordStatistic> pStats) {
    //Iterate on records that are not filtered by keysFilter_.
    for (int i = recordMask_.next(0); i != -1; i = recordMask_.next(i + 1)) {
      K key = rs_.getRecordManager().getKey(i);
      if (key == null) {
        continue;
      }

      Map<String, Object> params = new HashMap<String, Object>();
      for (RecordStatistic s : pStats) {
        prepareStatisticFields(s, key, params);

        //Results for this statistic have been obtained. Put them in the proper VolatileField.
        s.calculate(this, params, transposedMask_, i);
        params.clear();
      }
    }
  }

  /**
   * Prepares a <tt>Map</tt> structure that contains fields from the <tt>GenotypingStore</tt> stores.
   *
   * @param pStat the statistic for which we prepare the field map.
   * @param key   the key of the record for which we extract fields.
   * @return the map containing the required fields.
   */
  private void prepareStatisticFields(Statistic pStat, K key, Map<String, Object> params) {
    //Prepare the parameter map
    for (String p : pStat.getInputFields()) {
      params.put(p, rs_.getGenotypingField(p, key));
    }
  }

  /**
   * Gets a <tt>VolatileField</tt> from the pool. If it doesn't exist yet, either create it or return null.
   *
   * @param pName       the name under which the field can be found in the pool.
   * @param pClass      the class of the field content, in case we need to create a new field.
   * @param pAutoCreate whether or not to create the field if it doesn't exist.
   * @return the <tt>VolatileField</tt> that was requested.
   */
  public VolatileField getPooledField(String pName, Class<?> pClass, boolean pAutoCreate) {
    VolatileField field;

    //Verify if the field already exists in the pool
    if (!(pool_.containsKey(pName))) {
      //The field doesn't exist. Either create it or return null.
      if (pAutoCreate) {
        field = new VolatileField(pName, rs_.getStore(), ddf_.getInstance(pClass, pName));
        log.debug("Created volatile field {} of dimension {} and size {}",
            new Object[]{pName, field.getDictionary().dimension(), field.getSize()});
        pool_.put(pName, field);
      } else {
        return null;
      }
    } else {
      Object pooledData = pool_.get(pName);

      //TODO: Make sure the provided name is a kind of field
//      if (!(pooledData.getClass().isAssignableFrom(AbstractField.class))) {
//        throw new RuntimeException("The given pool member is not a type of field.");
//      }

      field = (VolatileField) pooledData;
    }
    return field;
  }

  /**
   * Gets a <tt>VolatileField</tt> from the pool, and creates it if it doesn't exist yet, using the class
   * provided in parameter to determine the dictionary.
   *
   * @param pName  the name under which the field can be found in the pool.
   * @param pClass the class of the field content, in case we need to create a new field.
   * @return the <tt>VolatileField</tt> that was requested.
   */
  public VolatileField getPooledField(String pName, Class<?> pClass) {
    return getPooledField(pName, pClass, true);
  }

  /**
   * Gets a <tt>VolatileField</tt> from the pool.
   *
   * @param pName the name under which the field can be found in the pool.
   * @return the <tt>VolatileField</tt> that was requested, or <tt>null</tt> if it doesn't exist in the pool.
   */
  public VolatileField getPooledField(String pName) {
    return getPooledField(pName, null, false);
  }

  /**
   * Sets a statistic result in a <tt>VolatileField</tt> located in the pool. If the <tt>VolatileField</tt>
   * doesn't exist, it will first be created.
   *
   * @param <T>    the type of the value to be stored in a <tt>VolatileField</tt>.
   * @param pName  the name of the field.
   * @param pIndex the record index.
   * @param pValue the value to store.
   */
  public <T> void setPoolResult(String pName, int pIndex, T pValue) {
    VolatileField resultField = getPooledField(pName, pValue.getClass());
    resultField.setValue(pIndex, resultField.getDictionary().lookup(pValue));
  }

  /**
   * Gets a value in a <tt>VolatileField</tt> at the given index.
   *
   * @param <T>    the type in which the value will be casted.
   * @param pName  the name of the field.
   * @param pIndex the record index.
   * @return the fetched value.
   */
  @SuppressWarnings("unchecked")
  public <T> T getPoolResult(String pName, int pIndex) {
    VolatileField resultField = getPooledField(pName);
    T result = (T) resultField.getDictionary().reverseLookup(resultField.getValue(pIndex));
    return result;
  }

}
