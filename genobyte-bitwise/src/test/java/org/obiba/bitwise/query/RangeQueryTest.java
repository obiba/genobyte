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
package org.obiba.bitwise.query;

import org.obiba.bitwise.Dictionary;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.dao.BaseBdbDaoTestCase;
import org.obiba.bitwise.dictionary.HuffmanDictionary;
import org.obiba.bitwise.dictionary.IntegerDictionary;
import org.obiba.bitwise.util.BitwiseStoreTestingHelper;

public class RangeQueryTest extends BaseBdbDaoTestCase {

  BitwiseStoreTestingHelper store_ = null;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    store_ = super.createMockStore("TEST", 9);

    //Prepare Integer dictionary
    IntegerDictionary intDict = new IntegerDictionary("id");
    intDict.setLower("0");
    intDict.setUpper(Integer.toString(Integer.MAX_VALUE));
    intDict.setStep("1");
    store_.addDictionary(intDict);

    //Prepare String dictionary
    HuffmanDictionary stringDict = new HuffmanDictionary("sd");
    stringDict.setSeedString("abcd");
    store_.addDictionary(stringDict);

    //Append dictionaries to store
    store_.setFieldDict("f1", "sd");
    store_.setFieldDict("f2", "id");

    int record1 = store_.nextIndex();
    int record2 = store_.nextIndex();
    int record3 = store_.nextIndex();

    Field f1 = store_.createField("f1");
    Field f2 = store_.createField("f2");
    store_.flush();

    Dictionary<String> sd = f1.getDictionary();
    f1.setValue(record1, sd.lookup("a"));
    f1.setValue(record2, sd.lookup("a"));
    f1.setValue(record3, sd.lookup("b"));

    Dictionary<Integer> id = f2.getDictionary();
    f2.setValue(record1, id.lookup(1));
    f2.setValue(record2, id.lookup(2));
    f2.setValue(record3, id.lookup(5));
  }

  /**
   * Testing OR
   */
  public void testRangeQueryOnUnorderedField() {
    QueryParser qp = new QueryParser();
    String query1 = "f1:[a,b]";
    String query2 = "f1:[1,2]";

    Query q1;
    Query q2;
    try {
      q1 = qp.parse(query1);
      q2 = qp.parse(query2);
    } catch(ParseException e) {
      throw new RuntimeException(e);
    }

    //Launch a range query on a String (therefore unordered) field, using string values
    boolean success = false;
    try {
      q1.execute(store_);
    } catch(InvalidQueryTypeException iqte) {
      success = true;
    }
    assertTrue(success);

    //Launch a range query on a String (therefore unordered) field, using integer values
    success = false;
    try {
      q2.execute(store_);
    } catch(InvalidQueryTypeException iqte) {
      success = true;
    }
    assertTrue(success);
  }

}
