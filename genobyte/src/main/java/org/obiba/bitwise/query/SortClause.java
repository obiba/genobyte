/*******************************************************************************
 * Copyright 2007(c) Génome Québec. All rights reserved.
 * 
 * This file is part of GenoByte.
 * 
 * GenoByte is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * GenoByte is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 *******************************************************************************/
package org.obiba.bitwise.query;

import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.query.sort.Sort;
import org.obiba.bitwise.query.sort.SortedQueryResult;


/**
 * Runs a query and sorts its results.
 */
public class SortClause extends Query {

  Query query_ = null;
  Sort sort_ = null;


  public SortClause(Query q, Sort sort) {
    super();
    query_ = q;
    sort_ = sort;
  }


  @Override
  public QueryResult execute(BitwiseStore store) throws QueryExecutionException {
    return new SortedQueryResult(store, sort_, query_.execute(store));
  }


  /**
   * Creates a <tt>String</tt> representation of this query.
   */
  @Override
  public String explain(BitwiseStore store) throws QueryExecutionException {
    return "{"+query_.explain(store)+"} ordered by {"+sort_+"}";
  }
}
