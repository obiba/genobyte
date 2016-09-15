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

import org.obiba.bitwise.dao.BaseBdbDaoTestCase;
import org.obiba.bitwise.dictionary.IntegerDictionary;
import org.obiba.bitwise.util.BitVectorQueryResult;
import org.obiba.bitwise.util.BitwiseStoreTestingHelper;

public class FieldValueIteratorTest extends BaseBdbDaoTestCase {

  private static final String DICT_NAME = "testIntegerDict";

  private static final String FIELD_NAME = "testField";

  IntegerDictionary id = null;

  BitwiseStoreTestingHelper store_ = null;

  public FieldValueIteratorTest() {
    super();
  }

  public FieldValueIteratorTest(String t) {
    super(t);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    store_ = createMockStore("MOCK_STORE", 10000);
    id = new IntegerDictionary(DICT_NAME);
    id.setLower("0");
    id.setUpper("100000");
    id.setStep("1");
    store_.addDictionary(id);
    store_.setFieldDict(FIELD_NAME, DICT_NAME);
  }

  public void testIterate() {
    int record1 = store_.nextIndex();
    int record2 = store_.nextIndex();
    int record3 = store_.nextIndex();
    store_.delete(record2);

    Field d = store_.createField(FIELD_NAME);
    assertNotNull(d);
    d.setValue(record1, d.getDictionary().lookup(4500));
    d.setValue(record2, d.getDictionary().lookup(5000));
    d.setValue(record3, d.getDictionary().lookup(4500));

    store_.flush();

    d = store_.getField(FIELD_NAME);
    assertNotNull(d);
    FieldValueIterator iter = new FieldValueIterator(d);
    for (int i = 0; i < 2; i++) {
      assertTrue(iter.hasNext());
      FieldValueIterator.FieldValue value = iter.next();
      if (i == 1) assertFalse(iter.hasNext());
      else assertTrue(iter.hasNext());

      assertNotNull(value);
      assertEquals(d.getDictionary().lookup(4500), value.getBitValue());
      assertEquals(4500, value.getValue());
      assertTrue(value.getIndex() == record1 || value.getIndex() == record3);
    }
  }

  public void testMask() {
    int record1 = store_.nextIndex();
    int record2 = store_.nextIndex();
    int record3 = store_.nextIndex();

    Field d = store_.createField(FIELD_NAME);
    assertNotNull(d);
    d.setValue(record1, d.getDictionary().lookup(4500));
    d.setValue(record2, d.getDictionary().lookup(5000));
    d.setValue(record3, d.getDictionary().lookup(4500));

    store_.flush();

    // Create a mask for first and last records only
    BitVector mask = new BitVector(store_.getCapacity());
    mask.set(record1);
    mask.set(record3);

    d = store_.getField(FIELD_NAME);
    assertNotNull(d);
    FieldValueIterator iter = new FieldValueIterator(d, new BitVectorQueryResult(mask));
    for (int i = 0; i < 2; i++) {
      assertTrue(iter.hasNext());
      FieldValueIterator.FieldValue value = iter.next();
      if (i == 1) assertFalse(iter.hasNext());
      else assertTrue(iter.hasNext());

      assertNotNull(value);
      assertEquals(d.getDictionary().lookup(4500), value.getBitValue());
      assertEquals(4500, value.getValue());
      assertTrue(value.getIndex() == record1 || value.getIndex() == record3);
    }

  }

  public void testNull() {
    Field f = store_.createField(FIELD_NAME);
    assertNotNull(f);

    Integer[] values = {null, 5000, null};

    for (int i = 0; i < values.length; i++) {
      int r = store_.nextIndex();
      f.setValue(r, f.getDictionary().lookup(values[i]));
    }

    store_.flush();

    f = store_.getField(FIELD_NAME);

    assertNotNull(f);
    FieldValueIterator iter = new FieldValueIterator(f);
    for (int i = 0; i < values.length; i++) {
      assertTrue(iter.hasNext());
      FieldValueIterator.FieldValue value = iter.next();
      if (i == values.length - 1) assertFalse(iter.hasNext());
      else assertTrue(iter.hasNext());
      assertNotNull(value);
      assertEquals(f.getDictionary().lookup(values[i]), value.getBitValue());
      assertEquals(values[i], value.getValue());
    }

  }
}
