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
package org.obiba.genobyte;

import org.obiba.bitwise.BitwiseRecordManager;
import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.Dictionary;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.genobyte.inconsistency.ComparableRecordProvider;
import org.obiba.genobyte.inconsistency.MendelianRecordTrioProvider;
import org.obiba.genobyte.model.TransposedValue;
import org.obiba.genobyte.statistic.StatsDigester;
import org.obiba.genobyte.statistic.StatsPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Wrapper to one of the Bitwise stores that make a <tt>GenotypingStore</tt>. There are two stores inside
 * a <tt>GenotypingStore</tt>: a sample store and an assay store.
 *
 * @param <K>  Key Type, the type (class) of the unique field for the <tt>BitwiseStore</tt> wrapped here.
 * @param <T>  Type, the class stored in the bitwise.
 * @param <TK> Transposed Key Type, the type (class) of the unique field for the other <tt>BitwiseStore</tt> kept in relationship with this one.
 */
abstract public class GenotypingRecordStore<K, T, TK> {

  private final Logger log = LoggerFactory.getLogger(GenotypingRecordStore.class);

  private static final String TRANSPOSE_MEM_SIZE = "bitwise.transpose.mem.size";

  /**
   * <tt>BitwiseStore</tt> being wrapped by this object.
   */
  protected BitwiseStore store_ = null;

  /**
   * The <tt>GenotypingRecordStore</tt> that is connected to this one, in the relationship defined by a <tt>GenotypingStore</tt>.
   */
  protected GenotypingRecordStore<TK, ?, K> transposedStore_ = null;

  /**
   * Map of all genotyping fields for this <tt>GenotypingRecordStore</tt>, organized by field name.
   */
  protected Map<String, GenotypingField> genotypingFields_ = new HashMap<String, GenotypingField>();

  protected BitwiseRecordManager<K, T> manager_ = null;

  private long transposeMemSize_ = 100 * 1024 * 1024;

  /**
   * Default StatsPool for this record store (calculates all default stats for this <tt>GenotypingRecordStore</tt>).
   */
  private StatsPool<K, TK> mainStatsPool_ = null;

  private StatsDigester mainStatsDigester_ = null;

  public GenotypingRecordStore(BitwiseStore store) {
    store_ = store;
    String memSize = this.getStore().getConfigurationProperties().getProperty(TRANSPOSE_MEM_SIZE);
    if (memSize != null) {
      try {
        transposeMemSize_ = Long.parseLong(memSize);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Configuration property [" + TRANSPOSE_MEM_SIZE + "] must be a number.");
      }
    }

    log.debug("Store [{}] transpose mem size is [{}]", this.getStore().getName(), transposeMemSize_);
  }

  public void setStatsPool(StatsPool<K, TK> pPool) {
    mainStatsPool_ = pPool;
  }

  public void setStatsDigester(StatsDigester pDigester) {
    mainStatsDigester_ = pDigester;
  }

  /**
   * Gets the <tt>BitwiseStore</tt> wrapped in this class, used to store records.
   *
   * @return the <tt>BitwiseStore</tt> used here.
   */
  public BitwiseStore getStore() {
    return store_;
  }

  /**
   * Sets the <tt>GenotypingRecordStore</tt> related to this one by a <tt>GenotypingStore</tt>.
   * If this instance is used to contain samples, the transposed store should be an assay store.
   * Reversely, if this instance is containing assays, the transposed store should be a sample store.
   *
   * @param transposed the related store into which genotype data can be transposed.
   */
  public void setTransposedStore(GenotypingRecordStore<TK, ?, K> transposed) {
    transposedStore_ = transposed;
  }

  /**
   * Returns the record store related to this store by a <tt>GenotypingStore</tt>.
   * If this is a sample store, the related assay store will be returned, and vice versa.
   *
   * @return the transposed store connected to this store.
   */
  public GenotypingRecordStore<TK, ?, K> getTransposedStore() {
    return transposedStore_;
  }

  public long getTransposeMemSize() {
    return transposeMemSize_;
  }

  public void setTransposeMemSize(long transposeMemSize) {
    transposeMemSize_ = transposeMemSize;
  }

  public void registerGenotypingField(GenotypingField field) {
    genotypingFields_.put(field.getName(), field);
  }

  /**
   * Returns the genotyping <tt>Field</tt> matching the given name and record, without creating fields if they do not yet
   * exist in the stores.
   *
   * @param fieldName the name of the field to get.
   * @param key       the unique key of the record.
   * @return the genotyping <tt>Field</tt> for the given name and record key.
   * @see getGenotypingField(String, K, boolean)
   */
  public Field getGenotypingField(String fieldName, K key) {
    return getGenotypingField(fieldName, key, false);
  }

  /**
   * Returns the genotyping <tt>Field</tt> matching the given name and record key.
   * That <tt>Field</tt> will be fecthed from the proper store (either this store or the related transposed store), depending
   * if the genotyping field was defined as transposable. If the field doesn't exist in the store for the given key, it is
   * possible to create it, depending if it is set as creatable or not.
   *
   * @param field  the name of the field to get.
   * @param key    the unique key of the record.
   * @param create if the field should be created in this <tt>BitwiseStore</tt>, when it doesn't exist.
   * @return the genotyping <tt>Field</tt> for the given name and record key.
   */
  public Field getGenotypingField(String fieldName, K key, boolean create) {
    GenotypingField gf = genotypingFields_.get(fieldName);

    //There is no defined genotyping field, try extracting a regular field from the store.
    if (gf == null) {
      return store_.getField(fieldName);
    }

    //Fetch the genotyping <tt>Field</tt> for the given record unique key.
    //It might be in this store, or in the transposed store
    Field f = null;
    fieldName = getFieldName(gf, key);
    BitwiseStore s = store_;
    if (gf.isTransposed()) {
      s = transposedStore_.getStore();
    }
    f = s.getField(fieldName);

    //If the field could not be found and it creatable (meaning we can create it if it doesn't exist),
    //and if the calling method wants to create such a field in the store.
    if (create && f == null && gf.isCreatable()) {
      f = s.createField(fieldName);
      if (f == null) {
        throw new IllegalStateException("Cannot create genotyping field [" + fieldName + "] in store [" + s + "]");
      }
    }
    return f;
  }

  /**
   * Set a list of tranposed values for a specific record
   *
   * @param key    the unique key of the record to populate
   * @param values the list of tranposed values to set
   * @return the number of values overwriten (number of times a value was written over a non-null preivous value)
   */
  public int setTransposedValues(String field, K key, List<? extends TransposedValue<TK, ?>> values) {
    if (values == null || values.size() == 0) {
      return 0;
    }

    log.debug("Store [{}]: setting [{}] transposed values in field [{}] for record key [{}]",
        new Object[]{this.getStore().getName(), values.size(), field, key});

    int overwriten = 0;
    BitwiseRecordManager<TK, ?> transposedManager_ = transposedStore_.getRecordManager();
    Field bitwiseField = getGenotypingField(field, key, true);
    if (bitwiseField == null) {
      log.error(
          "GenotypingField [{}] for key [{}] does not exist and cannot be created. Make sure the store schema defines a template field named [{}].",
          new Object[]{field, key, field});
      return 0;
    }

    Dictionary dict = bitwiseField.getDictionary();
    for (TransposedValue<TK, ?> value : values) {
      TK transposedKey = value.getTransposedKey();
      int transposedIndex = transposedManager_.getIndexFromKey(transposedKey);
      if (transposedIndex == -1) {
        throw new IllegalStateException(
            "No index for key [" + transposedKey + "] was found in [" + transposedStore_ + "] using manager [" +
                transposedManager_ + "]");
      }

      if (bitwiseField.isNull(transposedIndex) == false) overwriten++;
      bitwiseField.setValue(transposedIndex, dict.lookup(value.getValue()));
    }
    return overwriten;
  }

  /**
   * Computes genotype-related statistics for a store.
   */
  public void updateStats() {
    mainStatsPool_.resetMasks();
    internalUpdateStats();
  }

  /**
   * Computes genotype-related statistics for one store record.
   */
  public void updateStats(K pKey) {
    updateStats(Collections.singleton(pKey));
  }

  /**
   * Starts the default statistics calculation on a collection of record keys.
   *
   * @param keys the record keys to include in the stats calculation.
   */
  public void updateStats(Collection<K> keys) {
    // Reset the masks. If we don't do this here, the transposedMask may be erronous
    // because the store size may have grown since the last run... see GrowingStoreTest.testCalculateStatsAfterStoreGrows
    mainStatsPool_.resetMasks();

    Set<K> keySet = new TreeSet<K>();
    keySet.addAll(keys);
    mainStatsPool_.setRecordMask(keySet);

    internalUpdateStats();
  }

  /**
   * Starts the default statistics calculation on a set of record indexes.
   *
   * @param recordMask the record indexes to include in the stats calculation.
   */
  public void updateStats(QueryResult recordMask) {
    // Reset the masks. If we don't do this here, the transposedMask may be erronous
    // because the store size may have grown since the last run... see GrowingStoreTest.testCalculateStatsAfterStoreGrows
    mainStatsPool_.resetMasks();
    mainStatsPool_.setRecordMask(recordMask);

    internalUpdateStats();
  }

  /**
   * Utility method to start the main stats calculation after the record/transposed record masks have been set.
   */
  private void internalUpdateStats() {
    log.debug("Store {}: updating stats for [{}] records on [{}] transposedRecords",
        new Object[]{getStore().getName(), mainStatsPool_.getRecordMask().count(),
            mainStatsPool_.getTransposedMask().count()});
    mainStatsPool_.calculate();
    log.debug("Store {}: digesting stats pool", getStore().getName());
    mainStatsDigester_.digest(mainStatsPool_);
    // Reset pool's masks
    mainStatsPool_.resetMasks();
    mainStatsPool_.empty();
  }

  /**
   * Returns the <tt>BitwiseRecordManager</tt> mapped to the bitwise store in use in this object.
   *
   * @return the <tt>BitwiseRecordManager</tt>.
   */
  final public BitwiseRecordManager<K, T> getRecordManager() {
    if (manager_ == null) {
      manager_ = createRecordManager(store_);
    }
    return manager_;
  }

  abstract protected BitwiseRecordManager<K, T> createRecordManager(BitwiseStore store);

  abstract public ComparableRecordProvider getComparableRecordProvider();

  abstract public MendelianRecordTrioProvider getMendelianRecordTrioProvider();

  abstract public ReversableCallProvider getReversableCallProvider();

  /**
   * Gets the name of the current field in the store, from the template field name and the given record key.
   *
   * @param field the template name for the genotyping field.
   * @param key   the unique key to the record.
   * @return the complete field name.
   */
  protected String getFieldName(GenotypingField field, K key) {
    return GenotypingFieldNameHelper.generateFieldName(field, key);
  }
}
