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
package org.obiba.bitwise;

import java.util.List;

/**
 * Provides a set of methods to manage records within a store.
 * @param <K> the type of the unique field in this store.
 * @param <T> the class that can hold the data for records in the store.
 */
public interface BitwiseRecordManager<K, T> {
  
  /**
   * Gets an {@link FieldValueIterator} over the key value of all records in the store.
   * @return a {@link FieldValueIterator} instance on the field holding the keys.
   */
  public FieldValueIterator<K> keys();


  /**
   * Gets the key (unique field) value for the provided record object.
   * @param record the record from which we need the key value.
   * @return the key value.
   */
  public K getKey(T record);


  /**
   * Gets the key (unique field) value for the record at the provided index. Returns null if no record exists at the specified index.
   * @param index the record's index in the store.
   * @return the record's key or null if no record exists at the specified index.
   */
  public K getKey(int index);


  /**
   * Finds the index of the provided record.
   * @param record the object which position must be found in the store.
   * @return the index of the given record, or -1 if the record doesn't exist in the store.
   */
  public int getIndex(T record);


  /**
   * Finds the index of the record with the provided key.
   * @param recordKey the key to find in the store.
   * @return the index of the record with the given key, or -1 if there is no such record.
   */
  public int getIndexFromKey(K recordKey);


  /**
   * Saves the data contained in an object, at the desired index in the store.
   * @param index the index where to save the new record.
   * @param record the record object to be transfered in the store.
   * @return <tt>true</tt> if the object was saved, <tt>false</tt> otherwise.
   */
  public boolean save(int index, T record);


  /**
   * Insert a new record in the store.
   * @param record the object containing the new record data.
   * @return the id of the newly inserted record.
   */
  public int insert(T record);


  /**
   * Modifies in the store the columns of a record. Only the unique field must remain the same, as it
   * is the key to retrieve the record in the store.
   * @param record the object containing the update record data.
   * @return <tt>true</tt> if the record could be updated, <tt>false</tt> otherwise.
   */
  public boolean update(T record);


  /**
   * Transfers the content of a record into a new instance of the store's defining class <tt>T</tt>.
   * @param index the index of the record to be transfered in the new class instance.
   * @return an instance of the store defining class, filled with data from the required record.
   */
  public T load(int index);


  /**
   * Deletes the record at the given index.
   * @param index the zero-based index of the record to delete.
   */
  public void delete(int index);


  /**
   * Deletes all records in the store.
   */
  public void deleteAll();
  

  /**
   * Gets a list of names for all <tt>Fields</tt>.
   * @return the <tt>String</tt> list of <tt>Fields</tt> names.
   */
  public List<String> listFields();


  /**
   * Creates a new instance of the class used to represent a record.
   * @return the newly created instance of this class.
   */
  public T createInstance();

}
