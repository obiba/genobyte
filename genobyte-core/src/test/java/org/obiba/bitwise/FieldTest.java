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

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.Dictionary;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.dao.BaseBdbDaoTestCase;
import org.obiba.bitwise.dictionary.IntegerDictionary;
import org.obiba.bitwise.mock.MockBitwiseStore;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.bitwise.util.BitVectorQueryResult;


/**
 * Tests AbstractField methods by using a persisted field on a dummy bitwise store.
 */
public class FieldTest extends BaseBdbDaoTestCase {

  private static final String DICT_1_NAME = "integerDict1";
  private static final String DICT_2_NAME = "integerDict2";
  private static final String FIELD_1 = "testField1";
  private static final String FIELD_2 = "testField2";
  private static final String FIELD_3 = "testBoundedField";

  BitwiseStoreTestingHelper store_ = null;
  
  public FieldTest() {
    super();
  }

  public FieldTest(String t) {
    super(t);
  }
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    store_ = createMockStore("MOCK_STORE", 10000);
    
    //Create the two dictionaries used in all the tests
    IntegerDictionary dict1 = new IntegerDictionary(DICT_1_NAME);
    dict1.setLower("0");
    dict1.setUpper("100000");
    dict1.setStep("1");
    store_.addDictionary(dict1);
    
    IntegerDictionary dict2 = new IntegerDictionary(DICT_2_NAME);
    dict2.setLower("1900");
    dict2.setUpper("1970");
    dict2.setStep("1");
    store_.addDictionary(dict2);

    //Prepare the three fields used in the tests
    store_.setFieldDict(FIELD_1, DICT_1_NAME);
    store_.setFieldDict(FIELD_2, DICT_1_NAME);
    store_.setFieldDict(FIELD_3, dict2.getName());
  }


  /**
   * Does a field creation method really create a field?
   */
  public void testCreate() {
    Field d = store_.createField(FIELD_1);
    assertNotNull(d);
  }


  /**
   * Can a value be set for a given field record, and fetched back as the same value?
   */
  public void testSetValue() {
    Field d = store_.createField(FIELD_1);
    assertNotNull(d);
    d.setValue(9090, d.getDictionary().lookup(4500));
//    store_.closeField(d);
    store_.flush();
    d = store_.getField(FIELD_1);
    assertNotNull(d);
    Integer value = (Integer)d.getDictionary().reverseLookup(d.getValue(9090));
    assertNotNull(value);
    assertEquals(4500, value.intValue());
  }


  /**
   * Is it possible to increase the size of the store (the amount of records), and does it keep
   * the record values consistant?
   */
  public void testGrow() {
    Field d = store_.createField(FIELD_1);
    assertNotNull(d);
    d.setValue(9090, d.getDictionary().lookup(4500));
//    store_.closeField(d);
    store_.flush();
    d = store_.getField(FIELD_1);
    assertNotNull(d);
    d.grow(100000);
    d.setValue(90900, d.getDictionary().lookup(8686));
//    store_.closeField(d);
    store_.flush();

    d = store_.getField(FIELD_1);
    assertEquals(100000, d.getSize());

    Integer value = (Integer)d.getDictionary().reverseLookup(d.getValue(9090));
    assertNotNull(value);
    assertEquals(4500, value.intValue());
    value = (Integer)d.getDictionary().reverseLookup(d.getValue(90900));
    assertNotNull(value);
    assertEquals(8686, value.intValue());
  }


  /**
   * Does the value querying of a field values work properly, i.e. is the resultset valid?
   */
  public void testQuery() {
    Field f = store_.createField(FIELD_1);
    assertNotNull(f);
    f.setValue(store_.nextIndex(), f.getDictionary().lookup(4500));
    f.setValue(store_.nextIndex(), f.getDictionary().lookup(4500));
    f.setValue(store_.nextIndex(), f.getDictionary().lookup(4501));
    f.setValue(store_.nextIndex(), f.getDictionary().lookup(4501));

    QueryResult v = f.query(f.getDictionary().lookup(4500));
    assertNotNull(v);
    assertEquals(2, v.count());
  }


  /**
   * Does the value range querying of a field values work properly, i.e. is the resultset valid?
   */
  public void testRangeQuery() {
    Field f = store_.createField(FIELD_3);
    assertNotNull(f);

    f.setValue(store_.nextIndex(), f.getDictionary().lookup(new Integer(1900)));
    f.setValue(store_.nextIndex(), f.getDictionary().lookup(new Integer(1925)));
    f.setValue(store_.nextIndex(), f.getDictionary().lookup(new Integer(1970)));    

    //Range queries are including the bounds
    QueryResult v1 = f.rangeQuery(f.getDictionary().lookup(new Integer(1925)), f.getDictionary().lookup(new Integer(1925)));
    assertEquals(1, v1.count());
    
    //Fetch all values (the dictionary lower and upper bounds are the range of this query)
    QueryResult v2 = f.rangeQuery(f.getDictionary().lookup(new Integer(1900)), f.getDictionary().lookup(new Integer(1970)));
    assertEquals(store_.getSize(), v2.count());
  }

  
  /**
   * Is the range query resultset always consistent by testing various ranges for the same field?
   */
  public void testRangeQuery2() {
    Field d = store_.createField(FIELD_3);
    assertNotNull(d);

    for(int i = 1900; i <= 1970; i++) {
      d.setValue(store_.nextIndex(), d.getDictionary().lookup(new Integer(i)));
    }

    int lower = 1900;
    int upper = 1970;
    while(lower < upper) {
      QueryResult v = d.rangeQuery(d.getDictionary().lookup(new Integer(lower)), d.getDictionary().lookup(new Integer(upper)));
      assertEquals(upper - lower + 1, v.count());
      lower++;
    }
  }


  /**
   * Does the method <code>Field.diff</tt> work properly?
   */
  public void testDiff() {
    Field f1 = store_.createField(FIELD_1);
    assertNotNull(f1);
    Field f2 = store_.createField(FIELD_2);
    assertNotNull(f2);

    int id1 = store_.nextIndex();
    f1.setValue(id1, f1.getDictionary().lookup(4500));
    f2.setValue(id1, f2.getDictionary().lookup(4500));

    int id2 = store_.nextIndex();
    f1.setValue(id2, f1.getDictionary().lookup(8686));
    f2.setValue(id2, f2.getDictionary().lookup(4500));

    assertEquals(0, f1.diff(f1).count());
    assertEquals(0, f2.diff(f2).count());
    
    QueryResult v = f1.diff(f2);
    assertFalse(v.get(id1));
    assertTrue(v.get(id2));
  }


  /**
   * Tests the copyValues() method of AbstractField.
   */
  public void testCopyValues() {
    // Create the source of values to be copied
    Field sourceField = store_.createField(FIELD_1);
    Dictionary<Integer> d = sourceField.getDictionary();
    assertNotNull(sourceField);
    // Create 6 records (so they are not deleted)
    while(store_.nextIndex() < 5);
    sourceField.setValue(0, d.lookup(1));
    sourceField.setValue(1, d.lookup(1));
    sourceField.setValue(2, d.lookup(2));
    sourceField.setValue(3, d.lookup(2));
    sourceField.setValue(4, d.lookup(3));
    sourceField.setValue(5, d.lookup(3));

    assertEquals(2, sourceField.query(d.lookup(1)).count());
    assertEquals(2, sourceField.query(d.lookup(2)).count());
    assertEquals(2, sourceField.query(d.lookup(3)).count());

    // Create the destination Field
    Field destinationField = store_.createField(FIELD_2);
    destinationField.setValue(0, d.lookup(4));
    destinationField.setValue(1, d.lookup(4));
    destinationField.setValue(2, d.lookup(5));
    destinationField.setValue(3, d.lookup(5));
    destinationField.setValue(4, d.lookup(6));
    destinationField.setValue(5, d.lookup(6));

    assertEquals(2, destinationField.query(d.lookup(4)).count());
    assertEquals(2, destinationField.query(d.lookup(5)).count());
    assertEquals(2, destinationField.query(d.lookup(6)).count());
    
    BitVector filter = new BitVector(sourceField.getSize());
    filter.setAll();
    QueryResult qr = new BitVectorQueryResult(filter);
    // Unfiltered copy (copies all values from f to f2)
    destinationField.copyValues(sourceField, qr);

    assertEquals(2, destinationField.query(d.lookup(1)).count());
    assertEquals(2, destinationField.query(d.lookup(2)).count());
    assertEquals(2, destinationField.query(d.lookup(3)).count());
  }

  public void testCopyValuesWithNulls() {
    // Create the source of values to be copied
    Field sourceField = store_.createField(FIELD_1);
    Dictionary<Integer> d = sourceField.getDictionary();
    assertNotNull(sourceField);
    // Create 6 records (so they are not deleted)
    while(store_.nextIndex() < 5);
    sourceField.setValue(0, d.lookup(1));
    sourceField.setValue(1, d.lookup(1));
    sourceField.setValue(2, d.lookup(2));
    sourceField.setValue(3, d.lookup(2));
    sourceField.setValue(4, d.lookup(3));
    sourceField.setValue(5, d.lookup(3));

    assertEquals(2, sourceField.query(d.lookup(1)).count());
    assertEquals(2, sourceField.query(d.lookup(2)).count());
    assertEquals(2, sourceField.query(d.lookup(3)).count());

    // Create the destination Field
    Field destinationField = store_.createField(FIELD_2);
    destinationField.setValue(0, d.lookup(4));
    destinationField.setValue(1, null);
    destinationField.setValue(2, d.lookup(5));
    destinationField.setValue(3, null);
    destinationField.setValue(4, d.lookup(6));
    destinationField.setValue(5, null);

    assertEquals(1, destinationField.query(d.lookup(4)).count());
    assertEquals(1, destinationField.query(d.lookup(5)).count());
    assertEquals(1, destinationField.query(d.lookup(6)).count());
    
    BitVector filter = new BitVector(sourceField.getSize());
    filter.setAll();
    QueryResult qr = new BitVectorQueryResult(filter);
    // Unfiltered copy (copies all values from f to f2)
    destinationField.copyValues(sourceField, qr);

    assertEquals(2, destinationField.query(d.lookup(1)).count());
    assertEquals(2, destinationField.query(d.lookup(2)).count());
    assertEquals(2, destinationField.query(d.lookup(3)).count());
  }

  public void testFilteredCopyValues() {
    // Create the source of values to be copied
    Field sourceField = store_.createField(FIELD_1);
    Dictionary<Integer> d = sourceField.getDictionary();
    assertNotNull(sourceField);
    // Create 6 records (so they are not deleted)
    while(store_.nextIndex() < 5);
    sourceField.setValue(0, d.lookup(1));
    sourceField.setValue(1, d.lookup(1));
    sourceField.setValue(2, d.lookup(2));
    sourceField.setValue(3, d.lookup(2));
    sourceField.setValue(4, d.lookup(3));
    sourceField.setValue(5, d.lookup(3));

    assertEquals(2, sourceField.query(d.lookup(1)).count());
    assertEquals(2, sourceField.query(d.lookup(2)).count());
    assertEquals(2, sourceField.query(d.lookup(3)).count());

    // Create the destination Field
    Field destinationField = store_.createField(FIELD_2);
    destinationField.setValue(0, d.lookup(4));
    destinationField.setValue(1, null);
    destinationField.setValue(2, d.lookup(5));
    destinationField.setValue(3, null);
    destinationField.setValue(4, d.lookup(6));
    destinationField.setValue(5, null);

    assertEquals(1, destinationField.query(d.lookup(4)).count());
    assertEquals(1, destinationField.query(d.lookup(5)).count());
    assertEquals(1, destinationField.query(d.lookup(6)).count());

    BitVector mask = new BitVector(sourceField.getSize());
    mask.set(1);
    mask.set(3);
    mask.set(5);
    QueryResult qr = new BitVectorQueryResult(mask);

    destinationField.copyValues(sourceField, qr);

    assertEquals(1, destinationField.query(d.lookup(1)).count());
    assertEquals(1, destinationField.query(d.lookup(2)).count());
    assertEquals(1, destinationField.query(d.lookup(3)).count());
    assertEquals(1, destinationField.query(d.lookup(4)).count());
    assertEquals(1, destinationField.query(d.lookup(5)).count());
    assertEquals(1, destinationField.query(d.lookup(6)).count());
  }
}
