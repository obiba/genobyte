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

import org.obiba.bitwise.BitwiseStore;

/**
 * Boolean query on a store, which is a pair of two queries joined by a logical operator.
 */
public class BooleanQuery extends Query {

  /**
   * Enumeration of boolean operators supported by this type of query. 
   */
  public enum Operator {
    AND, OR, XOR
  }

  private Operator op_ = null;

  private Query left_ = null;

  private Query right_ = null;

  public BooleanQuery(Operator op, Query left, Query right) {
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
      case XOR:
        return left_.execute(store).xor(right_.execute(store));
    }
    throw new QueryExecutionException("Invalid operator=[" + op_ + "]");
  }

  @Override
  public String explain(BitwiseStore store) throws QueryExecutionException {
    StringBuffer sb = new StringBuffer();
    sb.append("(").append(left_.explain(store)).append(" ").append(op_).append(" ").append(right_.explain(store))
        .append(")").append("{").append(execute(store).count()).append("}");
    return sb.toString();
  }

  /**
   * Creates a <tt>String</tt> representation of this query.
   */
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("(").append(left_).append(" ").append(op_).append(" ").append(right_).append(")");
    return sb.toString();
  }

}
