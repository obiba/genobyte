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
package org.obiba.bitwise.annotation;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.BitwiseRecordManager;
import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.Dictionary;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.FieldValueIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Provides a set of methods to manage records within a store that has been created using Bitwise Annotations.
 * @param <K> the type of the unique field in this store.
 * @param <T> the class that can hold the data for a record in the store. This class is usually the one that was used to create the store,
 * using Java Annotations.
 */
public class AnnotationBasedRecord<K, T> implements BitwiseRecordManager<K, T> {

  private final Logger log = LoggerFactory.getLogger(AnnotationBasedRecord.class);

  BitwiseStore store_  = null;
  BitwiseAnnotationManager ba_ = null;

  private AnnotationBasedRecord(BitwiseStore bs, BitwiseAnnotationManager ba) {
    super();
    store_ = bs;
    ba_ = ba;
  }


  /**
   * Returns a String object representing this AnnotationBasedRecord. More exactly, what will be produced
   * is a string following this model: <code>AnnotationBasedRecord{_name_of_store_,_simple_name_of_record_class_}</code>.
   */
  @Override
  public String toString() {
    return "AnnotationBasedRecord{"+store_.getName()+","+ba_.getRecordClass().getSimpleName()+"}";
  }


  /**
   * Creates a new instance of <tt>AnnotationBasedRecord</tt> for a given store and a given class used to represent the store.
   * @param <K> the type of the unique field in this store.
   * @param <T> the class that can hold the data for a record in the store. This class is usually the one that was used to create the store,
   * using Java Annotations.
   * @param store the store related to this new instance.
   * @param recordClass the class that can hold the data for a record in the store.
   * @return the newly created instance.
   */
  static public <K, T> AnnotationBasedRecord<K, T> createInstance(BitwiseStore store, Class<? extends T> recordClass) {
    BitwiseAnnotationManager ba = new BitwiseAnnotationManager(recordClass);
    return new AnnotationBasedRecord<K, T>(store, ba);
  }


  @SuppressWarnings("unchecked")
  public K getKey(T record) {
    try {
      return (K)ba_.getUniqueFieldDescriptor().getReadMethod().invoke(record, (Object[])null);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  public K getKey(int index) {
    return getKeyFromIndex(index);
  }


  public T createInstance() {
    try {
      Class<T> c = ba_.getRecordClass();
      return c.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  public FieldValueIterator<K> keys() {
    if(ba_.getUniqueFieldDescriptor() == null) {
      throw new IllegalStateException("No unique field defined for annotation based record=["+ba_.getRecordClass()+"]");
    }
    String bitwiseField = ba_.getStoredFields().get(ba_.getUniqueFieldDescriptor().getName());
    Field keyField = store_.getField(bitwiseField);
    if(keyField == null) {
      // This may happen when store is being created inside a transaction (the store exists, but not its fields)
      return new EmptyFieldValueIterator<K>();
    }
    return new FieldValueIterator<K>(keyField);
  }


  public int getIndex(T record) {
    K uniqueKey = getKey(record);
    return getKeyIndex(uniqueKey);
  }


  public int getIndexFromKey(K recordKey) {
    return getKeyIndex(recordKey);
  }


  public boolean save(int index, T record) {
    return saveRecord(index, record);
  }


  public int insert(T record) {
    int id = store_.nextIndex();
    if(id < 0) {
      throw new IllegalStateException("store_.nextIndex() returned an illegal index: make sure the store's capacity is large enough.");
    }
    saveRecord(id, record);
    return id;
  }


  public boolean update(T record) {
    int id = getIndex(record);
    return save(id, record);
  }


  public T load(int index) {
    T e = createInstance();
    loadRecord(index, e);
    return e;
  }


  public void delete(int index) {
    store_.delete(index);
  }


  public void deleteAll() {
    store_.deleteAll();
  }


  public List<String> listFields() {
    return Collections.unmodifiableList(new ArrayList<String>(ba_.getStoredFields().values()));
  }


  private void loadRecord(int index, T o) {
    for (PropertyDescriptor property : ba_.getStoredDescriptors()) {
      String propertyName = property.getName();
      String bitwiseField = ba_.getStoredFields().get(propertyName);
      try {
        Field f = store_.getField(bitwiseField);
        if(f == null) {
          continue;
        }
        BitVector vector = f.getValue(index);
        Object value = f.getDictionary().reverseLookup(vector);
        if(property.getWriteMethod() != null) {
          property.getWriteMethod().invoke(o, value);
        }
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }


  @SuppressWarnings("unchecked")
  private boolean saveRecord(int id, T record) {
    if(id == -1) {
      throw new IllegalArgumentException("The record index is invalid.");
    }
    boolean modified = false;
    for (PropertyDescriptor property : ba_.getStoredDescriptors()) {
      String propertyName = property.getName();
      String bitwiseField = ba_.getStoredFields().get(propertyName);
      Object value = null;
      try {
        value = property.getReadMethod().invoke(record, (Object[])null);
        Field f = store_.getField(bitwiseField);
        if(f == null) {
          f = store_.createField(bitwiseField);
          if(f == null) {
            throw new RuntimeException("Cannot save property ["+propertyName+"] for record class ["+ba_.getRecordClass().getName()+"]: field name ["+bitwiseField+"] does not exist in store ["+store_.getName()+"].");
          }
        }
        Dictionary d = f.getDictionary();
        BitVector v = d.lookup(value);
        if(value != null && v == null) {
          throw new RuntimeException("Dictionary ["+d.getName()+"] cannot encode the value ["+value+"].");
        }

        if(modified == false) {
          BitVector currentValue = f.getValue(id);
          if(currentValue == null && v != null) {
            modified = true;
          } else if(currentValue != null && v == null) {
            modified = true;
          } else if(currentValue != null && v != null && currentValue.equals(v) == false) {
            modified = true;
          }
        }
        f.setValue(id, v);
      } catch (RuntimeException e) {
        log.error("Cannot set value [{}] in bitwise field [{}]: ", new Object[]{value, propertyName, e.getMessage()});
        throw e;
      } catch (Exception e) {
        log.error("Cannot set value [{}] in bitwise field [{}]: ", new Object[]{value, propertyName, e.getMessage()});
        throw new RuntimeException(e);
      }
    }
    return modified;
  }


  @SuppressWarnings("unchecked")
  private int getKeyIndex(K key) {
    String bitwiseField = ba_.getStoredFields().get(ba_.getUniqueFieldDescriptor().getName());
    Field f = store_.getField(bitwiseField);
    Dictionary<K> d = f.getDictionary();
    BitVector v = d.lookup(key);
    if(v == null) {
      v = new BitVector(d.dimension());
    }
    return f.query(v).next(0);
  }


  @SuppressWarnings("unchecked")
  private K getKeyFromIndex(int index) {
    String bitwiseField = ba_.getStoredFields().get(ba_.getUniqueFieldDescriptor().getName());
    Field f = store_.getField(bitwiseField);
    Dictionary<K> d = f.getDictionary();
    return d.reverseLookup(f.getValue(index));
  }
}
