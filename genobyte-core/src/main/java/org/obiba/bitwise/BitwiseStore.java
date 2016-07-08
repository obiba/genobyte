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
package org.obiba.bitwise;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.obiba.bitwise.dao.BitwiseStoreDtoDao;
import org.obiba.bitwise.dao.DaoKey;
import org.obiba.bitwise.dao.KeyedDaoManager;
import org.obiba.bitwise.dto.BitwiseStoreDto;
import org.obiba.bitwise.schema.DictionaryMetaData;
import org.obiba.bitwise.schema.FieldMetaData;
import org.obiba.bitwise.schema.StoreSchema;
import org.obiba.bitwise.util.StringUtil;

import com.ibatis.dao.client.DaoManager;

/**
 * Structure used to hold groups of fields belonging to a type of record. It is roughly the equivalent of a
 * table in SQL. 
 */
public class BitwiseStore {

  private int cacheSize_ = 1000;

  private FieldCache lruCache_ = new FieldCache();

  private Map<String, Field> writeCache_ = new HashMap<String, Field>();

  private DaoKey key = null;

  private BitwiseStoreDto data_ = null;

  private Map<String, Dictionary<?>> dictionaries_ = new HashMap<String, Dictionary<?>>();

  private FieldUtil fieldUtil_ = null;

  private DictionaryUtil dictUtil_ = null;

  private boolean dirty_ = false;

  private Properties configProperties_ = null;

  protected BitwiseStore(BitwiseStoreDto data) {
    super();
    data_ = data;
    key = new DaoKey(data_.getName());
    fieldUtil_ = new FieldUtil(this);
    dictUtil_ = new DictionaryUtil(this);
  }

  /**
   * Marks the beginning of a transaction in the store. It is defined by the traditional definition of a
   * transaction in the database world. If this method isn't called, all DAO methods will use the "autocommit" semantics.
   */
  public void startTransaction() {
    KeyedDaoManager.getInstance(key).startTransaction();
  }

  /**
   * Finishes the transaction and saves in the store all actions done within that transaction. 
   */
  public void commitTransaction() {
    synchronized(data_) {
      flush();
      writeCache_.clear();
      KeyedDaoManager.getInstance(key).commitTransaction();
    }
  }

  /**
   * Clears the transaction cache, and rollbacks uncommited transactions.
   */
  public void endTransaction() {
    synchronized(data_) {
      // Clear the dirty flag: if we are rolling back a transaction, this prevents writing modifications after the transaction ends.
      dirty_ = false;
      // We can simply clear the write cache here since the commitTransaction method should have flushed it.
      // At this point, if it contains data, it probably means that the transaction is being rolledback.
      writeCache_.clear();
      KeyedDaoManager.getInstance(key).endTransaction();
    }
  }

  /**
   * Returns the name of this <code>BitwiseStore</code>.
   * @return the name that was given to this <code>BitwiseStore</code> instance.
   */
  public String getName() {
    return data_.getName();
  }

  /**
   * Returns the maximum number of records that this <code>BitwiseStore</code> can currently hold.
   * @return the number of records that was set for this store.
   */
  public int getCapacity() {
    synchronized(data_) {
      return data_.getCapacity();
    }
  }

  /**
   * Returns the current number of existing records within this store.
   * @return the number of records found.
   */
  public int getSize() {
    synchronized(data_) {
      return data_.getCapacity() - data_.getDeleted().count();
    }
  }

  public StoreSchema getSchema() {
    return data_.getSchema();
  }

  /**
   * Removes a record from the store.
   * @param record the zero-based index of the record to be erased.
   */
  public void delete(int record) {
    synchronized(data_) {
      dirty_ = true;
      data_.getDeleted().set(record);
    }
  }

  /**
   * Removes a vector of records from the store.
   * @param records a vector of records to delete
   */
  public void delete(BitVector records) {
    synchronized(data_) {
      dirty_ = true;
      data_.getDeleted().or(records);
    }
  }

  /**
   * Sets all records in the store to an existing, undeleted state. All available record slots that
   * haven't previously been assigned to a value will become existing null records. Therefore, using this
   * method ensure that there will be the same amount of records in the store than its capacity allows.
   */
  public void undeleteAll() {
    synchronized(data_) {
      dirty_ = true;
      data_.getDeleted().clearAll();
      data_.getCleared().clearAll();
    }
  }

  /**
   * Deletes all records in the store, making them available to hold new data. 
   */
  public void deleteAll() {
    synchronized(data_) {
      dirty_ = true;
      data_.getDeleted().setAll();
    }
  }

  /**
   * Returns a vector identifying all recods that have been deleted. The deleted records will be identified
   * by a "one" in the <code>BitVector</code>.
   * @return a <code>BitVector</code> showing all deleted records in the store.
   */
  public BitVector getDeleted() {
    return new BitVector(data_.getDeleted());
  }

  /**
   * Returns a vector identifying all records (that are not deleted). Valid records' index is set in the 
   * returned vector. 
   * @return a vector of valid records.
   */
  public BitVector all() {
    return getDeleted().not();
  }

  /**
   * Returns the index of the next used record, starting from the provided index (inclusive).
   * @param index the index from where to start looking for an existing record.
   * @return the index of the next record or <code>index</code> if it is itself a valid record.
   */
  public int nextRecord(int index) {
    synchronized(data_) {
      return data_.getDeleted().nextClearBit(index);
    }
  }

  /**
   * Returns the index of the next available space to store a record or -1 if no more space is available. Use
   * ensureCapacity() to make sure the store can accomodate enough records.
   * @return the index of the next available space or -1 if none are available.
   */
  public int nextIndex() {
    synchronized(data_) {
      int id = data_.getDeleted().nextSetBit(0);
      if(id < 0) {
        return -1;
      }
      if(data_.getCleared().get(id) == false) {
        clear();
      }
      // Mark record as not deleted
      data_.getDeleted().clear(id);
      // Mark record as not cleared
      data_.getCleared().clear(id);
      dirty_ = true;
      return id;
    }
  }

  /**
   * Returns the <code>Dictionary</code> in this store identified by the provided name. 
   * @param name the name of the <code>Dictionary</code> to look for.
   * @return the <code>Dictionary</code> object, or <tt>null</tt> if there is no <code>Dictionary</code> with the provided name in this store.
   */
  public Dictionary<?> getDictionary(String name) {
    return dictionaries_.get(name);
  }

  /**
   * Makes sure that there is at least a certain number of record slots in the <tt>BitwiseStore</tt>. If there is less record slots
   * available than the number provided in parameter, the capacity of the store will be increased to match that number.
   * @param capacity the number of record slots that <b>must</b> available in the store.
   */
  public void ensureCapacity(int capacity) {
    synchronized(data_) {
      int c = data_.getCapacity();
      if(c < capacity) {
        dirty_ = true;
        data_.setCapacity(capacity);
        BitVector deleted = data_.getDeleted();
        BitVector cleared = data_.getCleared();
        int oldSize = deleted.size();
        deleted.grow(capacity);
        cleared.grow(capacity);
        for(int i = oldSize; i < capacity; i++) {
          deleted.set(i);
          cleared.set(i);
        }
        for(Field f : lruCache_.values()) {
          if(f.getSize() < getCapacity()) {
            f.grow(getCapacity());
          }
        }
        for(Field f : writeCache_.values()) {
          if(f.getSize() < getCapacity()) {
            f.grow(getCapacity());
          }
        }
      }
    }
  }

  /**
   * Creates a new <tt>Field</tt> object as described in the <tt>StoreSchema</tt>.
   * @param name the name of the field to be created. This name must correspond to a field entry
   * specified in the <tt>StoreSchema</tt> for this store.
   * @return the newly created <tt>Field</tt> object, or <tt>null</tt> if there was no field definition
   * with this name in the <tt>StoreSchema</tt>. 
   */
  public Field createField(String name) {
    synchronized(data_) {
      FieldMetaData meta = data_.getSchema().getField(name);
      if(meta == null) {
        return null;
      }
      Field f = fieldUtil_.create(name, meta.getDictionary(), getCapacity());
      lruCache_.put(name, f);
      writeCache_.put(name, f);
      return f;
    }
  }

  /**
   * Removes a <tt>Field</tt> object completely from this store.
   * @param name the name of the field to be deleted.
   */
  public void deleteField(String name) {
    synchronized(data_) {
      lruCache_.remove(name);
      writeCache_.remove(name);
      fieldUtil_.delete(name);
    }
  }

  /**
   * Returns the <tt>Field</tt> object with the name provided in parameter.
   * @param name the name of the <tt>Field</tt> to return.
   * @return the field, or <tt>null</tt> if there is no field with the required name.
   */
  public Field getField(String name) {
    synchronized(data_) {
      Field f = lruCache_.get(name);
      if(f == null) {
        f = writeCache_.get(name);
        if(f == null) {
          // Cache miss
          f = fieldUtil_.open(name);
          if(f == null) {
            return null;
          }
        }
        lruCache_.put(name, f);
      }
      if(f.getSize() < getCapacity()) {
        f.grow(getCapacity());
      }
      return f;
    }
  }

  /**
   * Removes a field from the read and the write cache.
   * @param field the <tt>Field</tt> to detach.
   */
  public void detach(Field field) {
    synchronized(data_) {
      lruCache_.remove(field.getName());
      writeCache_.remove(field.getName());
    }
  }

  /**
   * Closes the <tt>BitwiseStore</tt>. A <tt>BitwiseStore</tt> must be closed after use to gracefully finish operations in internal data
   * structure.
   */
  public void close() {
    flush();
    BitwiseStoreUtil.getInstance().close(this);
  }

  /**
   * Transfers field new content from the cache to the persistance mechanism (such as a database), and saves
   * dictionaries and bitwise store information to that mechanism.
   */
  public void flush() {
    synchronized(data_) {
      // Flush the dictionaries
      for(Dictionary<?> d : getDictionaries().values()) {
        getDictUtil().saveDictionary(d);
      }

      if(writeCache_ != null) {
        // Flush the Field write cache
        for(Field field : writeCache_.values()) {
          fieldUtil_.save(field);
        }
        writeCache_.clear();
      }

      // Save the bitwise store info
      save();
    }
  }

  /**
   * Returns the size of the field cache for this store.
   * @return the field cache size.
   */
  public int getCacheSize() {
    return cacheSize_;
  }

  /**
   * Get a list of all field names in the bitwise store.
   * @return List of field names as a String.
   */
  public List<String> getFieldList() {
    return fieldUtil_.list();
  }

  /**
   * Returns a String object representing this BitwiseStore. More exactly, what will be produced
   * is a string following this model: <code>BitwiseStore[_name_of_store_]</code>.
   */
  @Override
  public String toString() {
    return "BitwiseStore[" + getName() + "]";
  }

  public Properties getConfigurationProperties() {
    return this.configProperties_;
  }

  void flushField(Field field) {
    synchronized(data_) {
      writeCache_.put(field.getName(), field);
    }
  }

  protected DaoKey getDaoKey() {
    return key;
  }

  DictionaryUtil getDictUtil() {
    return dictUtil_;
  }

  FieldUtil getFieldUtil() {
    return fieldUtil_;
  }

  Map<String, Dictionary<?>> getDictionaries() {
    return dictionaries_;
  }

  void open(Properties config) {
    for(String dictName : getDictUtil().list()) {
      Dictionary<?> d = getDictUtil().openDictionary(dictName);
      if(d == null) {
        throw new IllegalStateException("Cannot open dictionary name=[" + dictName + "]");
      }
      getDictionaries().put(d.getName(), d);
    }
    configure(config);
  }

  void create(Properties config) {
    for(DictionaryMetaData meta : data_.getSchema().getDictionaries()) {
      Dictionary<?> d = getDictUtil().createDictionary(meta.getName(), meta.getClazz(), meta.getProperties());
      getDictionaries().put(d.getName(), d);
    }

    for(FieldMetaData meta : data_.getSchema().getFields()) {
      if(meta.isTemplate() == false) {
        getFieldUtil().create(meta.getName(), meta.getDictionary(), data_.getCapacity());
      }
    }
    configure(config);
  }

  /**
   * Returns the Data Transfert Object in used for this store.
   * @return the <tt>BitwiseStoreDto</tt> used by this <tt>BitwiseStore</tt>.
   */
  protected BitwiseStoreDto getDto() {
    return data_;
  }

  /**
   * Clear values from every field for all records that are marked deleted but not marked cleared 
   */
  private void clear() {
    synchronized(data_) {
      flush();

      BitVector clear = new BitVector(data_.getCleared());
      clear.not(); // not marked cleared
      clear.and(data_.getDeleted()); // and marked deleted

      if(clear.nextSetBit(0) == -1) {
        // Nothing to clear
        return;
      }
      fieldUtil_.clear(clear);
      data_.getCleared().or(clear);
      dirty_ = true;
    }
  }

  private void save() {
    if(dirty_) {
      synchronized(data_) {
        dirty_ = false;
        DaoManager daoManager = KeyedDaoManager.getInstance(key);
        BitwiseStoreDtoDao dao = (BitwiseStoreDtoDao) daoManager.getDao(BitwiseStoreDtoDao.class);
        dao.save(data_);
      }
    }
  }

  private void configure(Properties config) {
    configProperties_ = config;
    String cacheSize = config.getProperty("bitwise.store.fieldCacheSize");
    if(StringUtil.isEmptyString(cacheSize) == false) {
      try {
        cacheSize_ = Integer.parseInt(cacheSize);
      } catch(NumberFormatException e) {
        throw new IllegalArgumentException("Value for property \"bitwise.store.fieldCacheSize\" is invalid.");
      }
    }
  }

  /**
   * An LRU cache (Least Recently Used) to speed up access to recently used elements in the store.
   */
  private class FieldCache extends LinkedHashMap<String, Field> {
    private static final long serialVersionUID = -2108100556369387698L;

    private FieldCache() {
      super(cacheSize_, 0.75f, true);
    }

    @Override
    protected boolean removeEldestEntry(Entry<String, Field> e) {
      if(size() > cacheSize_) {
        return true;
      }
      return false;
    }
  }
}
