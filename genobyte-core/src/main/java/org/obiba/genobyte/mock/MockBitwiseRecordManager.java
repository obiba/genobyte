/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.obiba.genobyte.mock;

import org.obiba.bitwise.BitwiseRecordManager;
import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.FieldValueIterator;

import java.util.List;

public class MockBitwiseRecordManager implements BitwiseRecordManager<Integer, Object> {

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
