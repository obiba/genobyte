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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A {@link BitwiseRecordManager} for stores using a <tt>String</tt> unique field.
 *
 * @param <T> the class holding data for a record in the store.
 */
abstract public class StringKeyCache<T> implements BitwiseRecordManager<String, T> {

  BitwiseRecordManager<String, T> impl_ = null;

  Map<String, Integer> keyCache_ = new HashMap<String, Integer>();

  public StringKeyCache(BitwiseRecordManager<String, T> impl) {
    super();
    impl_ = impl;

    FieldValueIterator keyIter = impl_.keys();
    while (keyIter.hasNext()) {
      FieldValueIterator.FieldValue value = keyIter.next();
      String key = (String) value.getValue();
      keyCache_.put(key, value.getIndex());
    }
  }

  /**
   * Returns a String object representing various information about this <tt>StringKeyCache</tt>.
   * More exactly, what will be produced is a string following this model: StringCache{BitwiseRecordManager.toString()}.
   */
  @Override
  public String toString() {
    return "StringCache{" + impl_.toString() + "}";
  }

  abstract public String getKey(T record);

  public String getKey(int index) {
    return impl_.getKey(index);
  }

  synchronized public void delete(int index) {
    Integer i = new Integer(index);
    for (Iterator iter = keyCache_.values().iterator(); iter.hasNext(); ) {
      Integer value = (Integer) iter.next();
      if (value.equals(i)) {
        iter.remove();
        break;
      }
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

  public int getIndexFromKey(String key) {
    Integer index = keyCache_.get(key);
    return index != null ? index : -1;
  }

  public int insert(T record) {
    int index = impl_.insert(record);
    String key = getKey(record);
    keyCache_.put(key, index);
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

  public FieldValueIterator<String> keys() {
    return impl_.keys();
  }

  public List<String> listFields() {
    return impl_.listFields();
  }

  public T createInstance() {
    return impl_.createInstance();
  }
}
