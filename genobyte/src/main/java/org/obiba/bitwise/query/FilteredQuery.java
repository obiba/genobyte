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

/**
 * Query whose result is filtered. The filter is provided in parameter and the records for which the
 * filter has a value set to "one" will be excluded.
 */
public class FilteredQuery extends Query {

  private Filter filter_ = null;
  private Query query_ = null;


  public FilteredQuery(Filter filter, Query query) {
    super();
    filter_ = filter;
    query_ = query;
  }


  public QueryResult execute(BitwiseStore store) throws QueryExecutionException {
    return query_.execute(store).and(filter_.filter(store));
  }


  public String explain(BitwiseStore store) throws QueryExecutionException {
    StringBuffer sb = new StringBuffer();
    sb.append(filter_).append("(").append(query_.explain(store)).append("){").append(execute(store).count()).append("}");
    return sb.toString();
  }


  /**
   * Creates a <tt>String</tt> representation of this query.
   */
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(filter_).append("(").append(query_).append(")");
    return sb.toString();
  }
}
