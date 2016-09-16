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
import org.obiba.bitwise.query.BooleanQuery.Operator;
import org.obiba.bitwise.query.Query;
import org.obiba.bitwise.query.QueryExecutionException;
import org.obiba.bitwise.query.QueryResult;

public class MockBooleanQuery extends Query implements MockQuery {

  private Operator op_ = null;

  private Query left_ = null;

  private Query right_ = null;

  public MockBooleanQuery(Operator op, Query left, Query right) {
    super();
    op_ = op;
    left_ = left;
    right_ = right;
  }

  @Override
  public QueryResult execute(BitwiseStore store) throws QueryExecutionException {
    switch (op_) {
      case AND:
        return left_.execute(store).and(right_.execute(store));
      case OR:
        return left_.execute(store).or(right_.execute(store));
    }
    throw new QueryExecutionException("Invalid operator=[" + op_ + "]");
  }

  public String parsable() {
    return "(" + left_ + " " + op_ + " " + right_ + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof MockBooleanQuery) {
      MockBooleanQuery rhs = (MockBooleanQuery) o;
      return op_.equals(rhs.op_) && left_.equals(rhs.left_) && right_.equals(rhs.right_);
    }
    return super.equals(o);
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("(").append(left_).append(" ").append(op_).append(" ").append(right_).append(")");
    return sb.toString();
  }
}
