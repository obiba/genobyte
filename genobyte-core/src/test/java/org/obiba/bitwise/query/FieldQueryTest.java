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
package org.obiba.bitwise.query;

import org.obiba.bitwise.BitwiseStoreTestingHelper;
import org.obiba.bitwise.Dictionary;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.dao.BaseBdbDaoTestCase;
import org.obiba.bitwise.dictionary.IntegerDictionary;


public class FieldQueryTest extends BaseBdbDaoTestCase {

  BitwiseStoreTestingHelper store_ = null;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    store_ = super.createMockStore("TEST", 9);
    
    IntegerDictionary dict = new IntegerDictionary("d");
    dict.setLower("0");
    dict.setUpper(Integer.toString(Integer.MAX_VALUE));
    dict.setStep("1");
    store_.addDictionary(dict);
    store_.setFieldDict("f1", "d");
    store_.setFieldDict("f2", "d");
    
    int record1 = store_.nextIndex();
    int record2 = store_.nextIndex();
    int record3 = store_.nextIndex();
    int record4 = store_.nextIndex();
    int record5 = store_.nextIndex();
    int record6 = store_.nextIndex();
    int record7 = store_.nextIndex();
    int record8 = store_.nextIndex();
    int record9 = store_.nextIndex();

    Field f1 = store_.createField("f1");
    Field f2 = store_.createField("f2");
    store_.flush();

    Dictionary<Integer> d = f1.getDictionary();
    f1.setValue(record1, d.lookup(1));
    f1.setValue(record2, d.lookup(1));
    f1.setValue(record3, d.lookup(1));
    f1.setValue(record4, d.lookup(2));
    f1.setValue(record5, d.lookup(2));
    f1.setValue(record6, d.lookup(2));
    f1.setValue(record7, null);
    f1.setValue(record8, null);
    f1.setValue(record9, null);

    f2.setValue(record1, d.lookup(1));
    f2.setValue(record2, d.lookup(2));
    f2.setValue(record3, null);
    f2.setValue(record4, d.lookup(1));
    f2.setValue(record5, d.lookup(2));
    f2.setValue(record6, null);
    f2.setValue(record7, d.lookup(1));
    f2.setValue(record8, d.lookup(2));
    f2.setValue(record9, null);
  }


  /**
   * Testing OR
   */
  public void testOrWithNullRecords() {
    QueryParser qp = new QueryParser();
    String query1 = "f1:1 OR f2:1";
    String query2 = "NOT(f1:1 OR f2:1)";
    
    Query q1;
    Query q2;
    try {
      q1 = qp.parse(query1);
      q2 = qp.parse(query2);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    QueryResult qr1 = q1.execute(store_);
    QueryResult qr2 = q2.execute(store_);
    
    //First test - Five results found:
    //true or true
    //true or false
    //true or *filtered*
    //false or true
    //*filtered* or true
    assertEquals(5, qr1.count());
    
    //Second test: Negate this first test to obtain the amount of "false" bits while ignoring the
    //filtered ones. One result found: NOT(false or false)
    assertEquals(1, qr2.count());
  }
  
  
  /***
   * Testing AND
   */
  public void testAndWithNullRecords() {
    QueryParser qp = new QueryParser();
    String query1 = "f1:1 AND f2:1";
    String query2 = "NOT(f1:1 AND f2:1)";
    
    Query q1;
    Query q2;
    try {
      q1 = qp.parse(query1);
      q2 = qp.parse(query2);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    QueryResult qr1 = q1.execute(store_);
    QueryResult qr2 = q2.execute(store_);
    
    //First test - One found: true and true
    assertEquals(1, qr1.count());
    
    //Second test: Negate this first test to obtain the amount of "false" bits while ignoring the
    //filtered ones. Five results found:
    //  NOT(true and false)
    //  NOT(false and true)
    //  NOT(false and false)
    //  NOT(false and *filtered*)
    //  NOT(*filtered* and false)
    assertEquals(5, qr2.count());
  }
  
  
  /**
   * Testing XOR
   */
  public void testXorWithNullRecords() {
    QueryParser qp = new QueryParser();
    String query1 = "f1:1 XOR f2:1";
    String query2 = "NOT(f1:1 XOR f2:1)";
    
    Query q1;
    Query q2;
    try {
      q1 = qp.parse(query1);
      q2 = qp.parse(query2);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    QueryResult qr1 = q1.execute(store_);
    QueryResult qr2 = q2.execute(store_);
    
    //First test - Two results found:
    //true xor false
    //false xor true
    assertEquals(2, qr1.count());
    
    //Second test: Negate this first test to obtain the amount of "false" bits while ignoring the
    //filtered ones. Two results found:
    //  NOT(true xor true)
    //  NOT(false xor false)
    assertEquals(2, qr2.count());
  }
  

  /**
   * Testing AND NOT
   * 
   * andNot operator is not currently used in the query grammar, so let's do it manually with two
   * ResultQuery stems.
   */
  public void testAndNotWithNullRecords() {
    QueryParser qp = new QueryParser();
    String query1 = "f1:1";
    String query2 = "f2:1";
    
    Query q1;
    Query q2;
    try {
      q1 = qp.parse(query1);
      q2 = qp.parse(query2);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    QueryResult qr1 = q1.execute(store_);
    QueryResult qr2 = q2.execute(store_);
    
    //First test: Only one result found: true andNot false
    qr1.andNot(qr2);
    assertEquals(1, qr1.count());
    
    //Second test: Negate this first test to obtain the amount of "false" bits while ignoring the
    //filtered ones. Five bits from the first test should now be true:
    //  NOT(true andNot true)
    //  NOT(false andNot true)
    //  NOT(false andNot false)
    //  NOT(false andNot *filtered*)
    //  NOT(*filtered andNot true)
    qr1.not();
    assertEquals(5, qr1.count());
  }
  
  public void testAndWithIsNullQuery() {
    QueryParser qp = new QueryParser();
    String query1 = "f1:1 and f2:null";
    String query2 = "not(f1:1 and f2:null)";
    
    Query q1;
    Query q2;
    try {
      q1 = qp.parse(query1);
      q2 = qp.parse(query2);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    QueryResult qr1 = q1.execute(store_);
    QueryResult qr2 = q2.execute(store_);
    
    assertEquals(1, qr1.count());
    assertEquals(7, qr2.count());
  }
  
  public void testAndNotWithIsNullQuery() {
    QueryParser qp = new QueryParser();
    String query1 = "f1:1";
    String query2 = "f2:null";
    
    Query q1;
    Query q2;
    try {
      q1 = qp.parse(query1);
      q2 = qp.parse(query2);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    QueryResult qr1 = q1.execute(store_);
    QueryResult qr2 = q2.execute(store_);
    
    qr1.andNot(qr2);
    assertEquals(2, qr1.count());
    qr1.not();
    assertEquals(5, qr1.count());
  }
  
  public void testOrWithIsNullQuery() {
    QueryParser qp = new QueryParser();
    String query1 = "f1:1 or f2:null";
    String query2 = "not(f1:1 or f2:null)";
    
    Query q1;
    Query q2;
    try {
      q1 = qp.parse(query1);
      q2 = qp.parse(query2);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    QueryResult qr1 = q1.execute(store_);
    QueryResult qr2 = q2.execute(store_);
    
    assertEquals(5, qr1.count());
    assertEquals(2, qr2.count());
  }
  
  public void testXorWithIsNullQuery() {
    QueryParser qp = new QueryParser();
    String query1 = "f1:1 xor f2:null";
    String query2 = "not(f1:1 xor f2:null)";
    
    Query q1;
    Query q2;
    try {
      q1 = qp.parse(query1);
      q2 = qp.parse(query2);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    QueryResult qr1 = q1.execute(store_);
    QueryResult qr2 = q2.execute(store_);
    
    assertEquals(3, qr1.count());
    assertEquals(3, qr2.count());
  }

}
