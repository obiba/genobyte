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
 * Finds records that do not match the given query.
 */
public class NotQuery extends Query {

  private Query query_ = null;

  public NotQuery(Query q) {
    super();
    query_ = q;
  }

  public QueryResult execute(BitwiseStore store) throws QueryExecutionException {
    return query_.execute(store).not();
  }

  /**
   * Creates a <tt>String</tt> representation of this query.
   */
  @Override
  public String toString() {
    return "!" + query_;
  }
}
