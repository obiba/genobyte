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

import org.obiba.bitwise.dao.BaseBdbDaoTestCase;
import org.obiba.bitwise.schema.DictionaryMetaData;
import org.obiba.bitwise.schema.FieldMetaData;
import org.obiba.bitwise.schema.StoreSchema;


public class BitwiseStoreTest extends BaseBdbDaoTestCase {

  StoreSchema testSchema = new StoreSchema();

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    DictionaryMetaData dmd = new DictionaryMetaData();
    dmd.setName("d");
    dmd.setClass("org.obiba.bitwise.dictionary.BooleanDictionary");
    testSchema.addDictionary(dmd);
    
    FieldMetaData fmd = new FieldMetaData();
    fmd.setName("f");
    fmd.setDictionary("d");
    fmd.setTemplate(false);
    testSchema.addField(fmd);
  }

  public void testRollback() {
    BitwiseStore bs = BitwiseStoreUtil.getInstance().create("rollback", testSchema, 10);
    assertEquals(10, bs.getCapacity());
    assertEquals(0, bs.getSize());
    BitwiseStoreUtil.getInstance().close(bs);

    bs = BitwiseStoreUtil.getInstance().open("rollback");
    assertEquals(10, bs.getCapacity());
    assertEquals(0, bs.getSize());

    int recordIndex;
    try {
      bs.startTransaction();
      bs.ensureCapacity(1000);
      recordIndex = bs.nextIndex();
      BitVector value = bs.getField("f").getDictionary().lookup(true);
      bs.getField("f").setValue(recordIndex, value);
    } finally {
      bs.endTransaction();
      BitwiseStoreUtil.getInstance().close(bs);
    }

    bs = BitwiseStoreUtil.getInstance().open("rollback");
    assertEquals(10, bs.getCapacity());
    assertEquals(0, bs.getSize());
    assertNull(bs.getField("f").getValue(recordIndex));
  }
 
  public void testZeroCapacity() {
    BitwiseStore bs = BitwiseStoreUtil.getInstance().create("zero", testSchema, 0);
    bs.close();
    bs = BitwiseStoreUtil.getInstance().open("zero");
    assertNotNull(bs);
    bs.close();
  }
  
  public void testDeleted() {
    BitwiseStore bs = BitwiseStoreUtil.getInstance().create("test", testSchema, 3);
    int index;
    assertTrue((index = bs.nextIndex()) >= 0) ;
    bs.close();
    bs = BitwiseStoreUtil.getInstance().open("test");
    assertNotNull(bs);
    assertFalse(bs.getDeleted().get(index));
    bs.delete(index);
    bs.close();
    bs = BitwiseStoreUtil.getInstance().open("test");
    assertNotNull(bs);
    assertTrue(bs.getDeleted().get(index));
  }
  

  public void testFieldPersistence() {
    BitwiseStore bs = BitwiseStoreUtil.getInstance().create("fieldPersistence", testSchema, 10);
    Field f = bs.getField("f");
    assertNotNull(f);
    int record = bs.nextIndex();
    f.setValue(record, f.getDictionary().lookup(Boolean.TRUE));
    bs.close();

    bs = BitwiseStoreUtil.getInstance().open("fieldPersistence");
    assertNotNull(bs);
    f = bs.getField("f");
    assertEquals(Boolean.TRUE, f.getDictionary().reverseLookup(f.getValue(record)));
    bs.close();
  }
  
}
