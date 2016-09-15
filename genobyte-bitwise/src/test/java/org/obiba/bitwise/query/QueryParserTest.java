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

import junit.framework.TestCase;
import org.obiba.bitwise.mock.query.*;

public class QueryParserTest extends TestCase {

  QueryParser qp = new QueryParser(new MockQueryFactory());

  public QueryParserTest() {
    super();
  }

  public QueryParserTest(String arg0) {
    super(arg0);
  }

  public void testSingleFieldQuery() {
    try {
      Query q = qp.parse("field1:value");
      assertEquals(new MockFieldValueQuery("field1", "value"), q);

      q = qp.parse("field1:100");
      assertEquals(new MockFieldValueQuery("field1", "100"), q);

      q = qp.parse("field1:0.1");
      assertEquals(new MockFieldValueQuery("field1", "0.1"), q);

      q = qp.parse("field1:value123");
      assertEquals(new MockFieldValueQuery("field1", "value123"), q);

      q = qp.parse("(field1:100)");
      assertEquals(new MockFieldValueQuery("field1", "100"), q);

      q = qp.parse("field1:\"quoted string with numbers 1234\"");
      assertEquals(new MockFieldValueQuery("field1", "quoted string with numbers 1234"), q);

      q = qp.parse("NOT field1:value123");
      assertEquals(new MockNotQuery(new MockFieldValueQuery("field1", "value123")), q);

      q = qp.parse("! field1:value123");
      assertEquals(new MockNotQuery(new MockFieldValueQuery("field1", "value123")), q);

      q = qp.parse("field1 ~ field2");
      assertEquals(new MockDiffQuery("field1", "field2"), q);

      q = qp.parse("field1 DIFF field2");
      assertEquals(new MockDiffQuery("field1", "field2"), q);

      q = qp.parse("field1:\"quoted string with colon :\"");
      assertEquals(new MockFieldValueQuery("field1", "quoted string with colon :"), q);

    } catch(ParseException e) {
      assertTrue(e.getMessage(), false);
    }
  }

  public void testWildcardQuery() {
    try {
      Query q;

      //Testing wildcard at right of query
      q = qp.parse("field1:value*");
      assertEquals(new MockWildcardQuery("field1", "value", null), q);  //String

      q = qp.parse("field1:1A*");
      assertEquals(new MockWildcardQuery("field1", "1A", null), q);     //String starting with integer 

      q = qp.parse("field1:1*");
      assertEquals(new MockWildcardQuery("field1", "1", null), q);      //Integer

      q = qp.parse("field1:1.01*");
      assertEquals(new MockWildcardQuery("field1", "1.01", null), q);   //Float

      //Testing wildcard at right of query
      q = qp.parse("field1:*value");
      assertEquals(new MockWildcardQuery("field1", null, "value"), q);  //String

      q = qp.parse("field1:*1A");
      assertEquals(new MockWildcardQuery("field1", null, "1A"), q);     //String starting with integer 

      q = qp.parse("field1:*1");
      assertEquals(new MockWildcardQuery("field1", null, "1"), q);      //Integer

      q = qp.parse("field1:*1.01");
      assertEquals(new MockWildcardQuery("field1", null, "1.01"), q);   //Float

      //Testing wildcard in middle of query
      q = qp.parse("field1:abc*def");
      assertEquals(new MockWildcardQuery("field1", "abc", "def"), q);  //String

      q = qp.parse("field1:1a*2b");
      assertEquals(new MockWildcardQuery("field1", "1a", "2b"), q);     //String starting with integer 

      q = qp.parse("field1:1*2");
      assertEquals(new MockWildcardQuery("field1", "1", "2"), q);      //Integer

      q = qp.parse("field1:1.11*2.22");
      assertEquals(new MockWildcardQuery("field1", "1.11", "2.22"), q);   //Float

    } catch(ParseException e) {
      assertTrue(e.getMessage(), false);
    }
  }

  public void testInvalidQuery() {
    try {
      Query q = qp.parse("field1:");
      assertTrue("Expected exception not thrown", false);
    } catch(ParseException e) {
      assertTrue(true);
    }

    try {
      Query q = qp.parse("field1:value allo");
      assertTrue("Expected exception not thrown", false);
    } catch(ParseException e) {
      assertTrue(true);
    }

    try {
      Query q = qp.parse(":");
      assertTrue("Expected exception not thrown", false);
    } catch(ParseException e) {
      assertTrue(true);
    }
  }

  public void testMultipleFieldQuery() {
    try {
      Query q1 = new MockFieldValueQuery("field1", "value");
      Query q2 = new MockFieldValueQuery("field2", "value");

      Query q = qp.parse("field1:value AND field2:value");
      assertEquals(new MockBooleanQuery(BooleanQuery.Operator.AND, q1, q2), q);

      q = qp.parse("field1:value OR field2:value");
      assertEquals(new MockBooleanQuery(BooleanQuery.Operator.OR, q1, q2), q);

      q = qp.parse("field1:value XOR field2:value");
      assertEquals(new MockBooleanQuery(BooleanQuery.Operator.XOR, q1, q2), q);

      q = qp.parse("field1:value OR NOT field2:value");
      assertEquals(new MockBooleanQuery(BooleanQuery.Operator.OR, q1, new MockNotQuery(q2)), q);

      Query q3 = new MockFieldValueQuery("field3", "0.99");
      q = qp.parse("field1:value OR NOT field2:value AND field3:0.99");
      Query result = new MockBooleanQuery(BooleanQuery.Operator.OR, q1,
          new MockBooleanQuery(BooleanQuery.Operator.AND, new MockNotQuery(q2), q3));
      assertEquals(result, q);

      q = qp.parse("field1:value OR NOT (field2:value AND field3:0.99)");
      result = new MockBooleanQuery(BooleanQuery.Operator.OR, q1,
          new MockNotQuery(new MockBooleanQuery(BooleanQuery.Operator.AND, q2, q3)));
      assertEquals(result, q);

      q = qp.parse("NOT field1:value AND NOT field2:value AND field1 DIFF field2");
      result = new MockBooleanQuery(BooleanQuery.Operator.AND, new MockNotQuery(q1),
          new MockBooleanQuery(BooleanQuery.Operator.AND, new MockNotQuery(q2), new MockDiffQuery("field1", "field2")));
      assertEquals(result, q);

    } catch(ParseException e) {
      assertTrue(e.getMessage(), false);
    }
  }

}
