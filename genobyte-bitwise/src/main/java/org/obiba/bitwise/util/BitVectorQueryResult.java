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
package org.obiba.bitwise.util;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.query.QueryResult;

/**
 * {@link QueryResult} implementation using a {@link BitVector} instance. This
 * class is useful to decorate the {@link BitVector} class in order to use
 * it as a {@link QueryResult}.
 */
public class BitVectorQueryResult implements QueryResult {

  /**
   * The result vector
   */
  BitVector result_ = null;

  /**
   * Constructs a {@link QueryResult} using the specified {@link BitVector}
   *
   * @param result the vector to wrap
   */
  public BitVectorQueryResult(BitVector result) {
    super();
    result_ = result;
  }

  public BitVector bits() {
    return result_;
  }

  public BitVector getFilter() {
    // Return an empty vector
    return new BitVector(result_.size());
  }

  public QueryResult copy() {
    return new BitVectorQueryResult(new BitVector(result_));
  }

  public int hit(int index) {
    int h = -1;
    while (index-- >= 0) {
      h = next(h + 1);
    }
    return h;
  }

  public int next(int index) {
    return result_.nextSetBit(index);
  }

  public boolean get(int index) {
    return result_.get(index);
  }

  public int count() {
    return result_.count();
  }

  public QueryResult not() {
    result_.not();
    return this;
  }

  public QueryResult and(QueryResult r) {
    result_.and(r.bits());
    return this;
  }

  public QueryResult andNot(QueryResult r) {
    result_.andNot(r.bits());
    return this;
  }

  public QueryResult or(QueryResult r) {
    result_.or(r.bits());
    return this;
  }

  public QueryResult xor(QueryResult r) {
    result_.xor(r.bits());
    return this;
  }

}
