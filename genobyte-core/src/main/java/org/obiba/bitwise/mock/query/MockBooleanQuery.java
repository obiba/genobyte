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
    switch(op_) {
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
    if(o instanceof MockBooleanQuery) {
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
