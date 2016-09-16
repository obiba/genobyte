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

import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.query.Query;
import org.obiba.bitwise.query.QueryExecutionException;
import org.obiba.bitwise.query.QueryResult;

public class MockRangeQuery extends Query implements MockQuery {

  private String field_ = null;

  private String from_ = null;

  private String to_ = null;

  public MockRangeQuery(String field, String from, String to) {
    super();
    field_ = field;
    from_ = from;
    to_ = to;
  }

  @Override
  public QueryResult execute(BitwiseStore store) throws QueryExecutionException {
    return null;
  }

  public String parsable() {
    return field_ + ":[" + from_ + " , " + to_ + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof MockRangeQuery) {
      MockRangeQuery rhs = (MockRangeQuery) o;
      return field_.equals(rhs.field_) && from_.equals(rhs.from_) && to_.equals(rhs.to_);
    }
    return super.equals(o);
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(field_).append("=[").append(from_).append(" TO ").append(to_).append("]");
    return sb.toString();
  }
}
