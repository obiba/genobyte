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
package org.obiba.bitwise.mock;

import java.util.List;

import org.obiba.bitwise.BitwiseRecordManager;
import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.FieldValueIterator;


public class MockBitwiseRecordManager implements BitwiseRecordManager<Integer,Object> {

  private BitwiseStore store = null;
  
  public MockBitwiseRecordManager(BitwiseStore bs) {
    super();
    store = bs;
  }

  public Object createInstance() {
    return null;
  }

  public void delete(int index) {
  }

  public void deleteAll() {
  }

  public int getIndex(Object record) {
    return 0;
  }

  public int getIndexFromKey(Integer recordKey) {
    //In this mock store, the record key is also the record index.
    return recordKey;
  }

  public Integer getKey(Object record) {
    return null;
  }

  public Integer getKey(int index) {
    //In this mock store, the record key is also the record index.
    return index;
  }

  public int insert(Object record) {
    return -1;
  }

  public FieldValueIterator keys() {
    // Should work if store is a MockBitwiseStore
    return new FieldValueIterator(store.getField("id"));
  }

  public List listFields() {
    return store.getFieldList();
  }

  public Object load(int index) {
    return null;
  }

  public boolean save(int index, Object record) {
    return false;
  }

  public boolean update(Object record) {
    return false;
  }

}
