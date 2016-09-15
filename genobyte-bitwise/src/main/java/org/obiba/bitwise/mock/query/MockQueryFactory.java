/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
