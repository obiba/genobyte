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
package org.obiba.bitwise.util;

import org.obiba.bitwise.BitwiseRecordManager;
import org.obiba.bitwise.FieldValueIterator;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link BitwiseRecordManager} for stores using an <tt>Integer</tt> unique field.
 * @param <T> the class holding data for a record in the store.
 */
abstract public class IntegerKeyCache<T> implements BitwiseRecordManager<Integer, T> {

  BitwiseRecordManager<Integer, T> impl_ = null;

  /**
   * Index of array is the record unique key, the value is its bitwise record index.
   */
  ArrayList<Integer> keyCache_ = new ArrayList<Integer>();

  /**
   * Index of array is the record index, the value is its unique key.
   */
  ArrayList<Integer> keyCache2_ = new ArrayList<Integer>();

  public IntegerKeyCache(BitwiseRecordManager<Integer, T> impl) {
    super();
    impl_ = impl;

    FieldValueIterator<Integer> keyIter = impl_.keys();
    while(keyIter.hasNext()) {
      FieldValueIterator<Integer>.FieldValue value = keyIter.next();
      Integer key = value.getValue();
      if(key == null) {
        throw new IllegalStateException("Key cannot be null for index=[" + value.getIndex() + "]");
      }
      while(keyCache_.size() <= key) {
        keyCache_.add(null);
      }
      keyCache_.set(key, value.getIndex());

      while(keyCache2_.size() <= value.getIndex()) keyCache2_.add(null);
      keyCache2_.set(value.getIndex(), key);
    }
  }

  /**
   * Returns a String object representing various information about this <tt>IntegerKeyCache</tt>.
   * More exactly, what will be produced is a string following this model: IntegerCache{BitwiseRecordManager.toString()}.
   */
  @Override
  public String toString() {
    return "IntegerCache{" + impl_.toString() + "}";
  }

  /**
   * Should be overriden by extending classes to provide the key for the specified record.
   * @return the key of the specified record.
   */
  abstract public Integer getKey(T record);

  public Integer getKey(int index) {
    if(index < 0 || index >= keyCache2_.size()) return null;
    return keyCache2_.get(index);
  }

  synchronized public void delete(int index) {
    Integer key = keyCache2_.get(index);
    keyCache2_.set(index, null);
    if(key != null) {
      keyCache_.set(key, null);
    }
    impl_.delete(index);
  }

  synchronized public void deleteAll() {
    keyCache_.clear();
    impl_.deleteAll();
  }

  public int getIndex(T record) {
    return getIndexFromKey(getKey(record));
  }

  public int getIndexFromKey(Integer key) {
    if(key == null || key >= keyCache_.size()) {
      return -1;
    }
    Integer index = keyCache_.get(key);
    return index != null ? index : -1;
  }

  public int insert(T record) {
    int index = impl_.insert(record);
    int key = getKey(record);

    while(keyCache_.size() <= key) keyCache_.add(null);
    keyCache_.set(key, index);

    while(keyCache2_.size() <= index) keyCache2_.add(null);
    keyCache2_.set(index, key);

    return index;
  }

  public T load(int index) {
    return impl_.load(index);
  }

  public boolean save(int index, T record) {
    return impl_.save(index, record);
  }

  public boolean update(T record) {
    return impl_.save(getIndex(record), record);
  }

  public FieldValueIterator<Integer> keys() {
    return impl_.keys();
  }

  public List<String> listFields() {
    return impl_.listFields();
  }

  public T createInstance() {
    return impl_.createInstance();
  }
}
