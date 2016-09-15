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
package org.obiba.bitwise.schema.defaultDict;

import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.BitwiseStoreUtil;
import org.obiba.bitwise.Dictionary;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.annotation.AnnotationStoreSchemaBuilder;
import org.obiba.bitwise.dao.BaseBdbDaoTestCase;
import org.obiba.bitwise.schema.StoreSchema;

import java.util.Properties;

/**
 * Test the encoding and decoding methods of various Java basic types. If the orginal value can be encoded and decoded
 * back to its original value, it seems that the dictionary is working properly.
 */
public class EncodeDecodeWithDefaultDictionariesTest extends BaseBdbDaoTestCase {

  /**
   * Build a valid store from a schema built with annotation, insert data in that store, and try to
   * run a query.
   */
  public void testDefaultDictionaries() {
    String annotationStoreName = "myStore";
    int storeSize = 10;
    BitwiseStore myStore = null;
    try {
      //Create store from Annotations schema
      myStore = createStore(annotationStoreName, storeSize);
      myStore.startTransaction();
      addRecords(myStore);
      myStore.commitTransaction();
      myStore.endTransaction();

      //Make sure the new store has been created
      assertNotNull(myStore);
    } finally {
      if (myStore != null) {
        myStore.endTransaction();
        myStore.close();
      }
    }
  }

  /**
   * Build a new store (using class annotations)
   *
   * @param pStoreName is the name of the store to be created.
   * @param pStoreSize is the starting size of the repository (number of records already set to null)
   * @return The newly created store as a BitwiseStore object.
   */
  public BitwiseStore createStore(String pStoreName, int pStoreSize) {
    AnnotationStoreSchemaBuilder sca = new AnnotationStoreSchemaBuilder();
    StoreSchema mySchema = sca.createSchema(FakeStoreForDefaultDictionaries.class);

    //Creating the store from the information in StoreSchemaTODO: Test non-primitive types
    Properties p = new Properties();
    p.put("je.maxMemory", "200000000");
    BitwiseStore newStore = BitwiseStoreUtil.getInstance().create(pStoreName, mySchema, pStoreSize, p);
    return newStore;
  }

  /**
   * Create new rows in the store.
   *
   * @param pStore the store in which to add new rows.
   */
  private void addRecords(BitwiseStore pStore) {
    int recordIndex = pStore.nextIndex();

    addFieldData(pStore, "a", (byte) 1, recordIndex);
    addFieldData(pStore, "b", (short) 1, recordIndex);
    addFieldData(pStore, "c", 1, recordIndex);
    addFieldData(pStore, "d", 1l, recordIndex);
    addFieldData(pStore, "e", 1.01f, recordIndex);
    addFieldData(pStore, "f", -999.9d, recordIndex);
    addFieldData(pStore, "g", (boolean) true, recordIndex);
    addFieldData(pStore, "h", (char) 'e', recordIndex);

    addFieldData(pStore, "i", new Integer(1000), recordIndex);
    addFieldData(pStore, "j", new Double(1000.0d), recordIndex);
    addFieldData(pStore, "k", new String("wow"), recordIndex);
  }

  private void addFieldData(BitwiseStore pStore, String pFieldName, Object pValue, int pIndex) {
    Field f = pStore.getField(pFieldName);
    Dictionary idDict = f.getDictionary();
    f.setValue(pIndex, idDict.lookup(pValue));
    Object storedValue = idDict.reverseLookup(f.getValue(pIndex));
    assertEquals(pValue, storedValue);
  }

}
