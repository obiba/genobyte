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
package org.obiba.bitwise.mock.query;

import org.obiba.bitwise.query.BooleanQuery.Operator;
import org.obiba.bitwise.query.Query;
import org.obiba.bitwise.query.QueryFactory;
import org.obiba.bitwise.query.sort.Sort;

public class MockQueryFactory implements QueryFactory {

  public MockQueryFactory() {
    super();
  }

  public Query getFieldValueQuery(String field, String value) {
    return new MockFieldValueQuery(field, value);
  }

  public Query getRangeQuery(String field, String from, String to) {
    return null;
  }

  public Query getBooleanQuery(Operator op, Query left, Query right) {
    return new MockBooleanQuery(op, left, right);
  }

  public Query getNotQuery(Query q) {
    return new MockNotQuery(q);
  }

  public Query getDiffQuery(String field1, String field2) {
    return new MockDiffQuery(field1, field2);
  }

  public Query getSortClause(Query q, Sort sort) {
    return null;
  }

  public Query getWildcardQuery(String field, String valuePre, String valuePost) {
    return new MockWildcardQuery(field, valuePre, valuePost);
  }
}
