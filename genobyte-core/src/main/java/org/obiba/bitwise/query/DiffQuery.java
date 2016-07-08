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
import org.obiba.bitwise.Field;

/**
 * Finds all records for which the value of two fields is different.
 */
public class DiffQuery extends Query {

  private String field1_ = null;

  private String field2_ = null;

  public DiffQuery(String field1, String field2) {
    super();
    field1_ = field1;
    field2_ = field2;
  }

  @Override
  public QueryResult execute(BitwiseStore store) throws QueryExecutionException {
    Field field1 = store.getField(field1_);
    if(field1 == null) {
      throw invalidField(store, field1_);
    }
    Field field2 = store.getField(field2_);
    if(field2 == null) {
      throw invalidField(store, field2_);
    }

    try {
      return field1.diff(field2);
    } catch(IllegalArgumentException e) {
      throw new QueryExecutionException(e);
    }
  }

  /**
   * Creates a <tt>String</tt> representation of this query.
   */
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(field1_).append("~").append(field2_);
    return sb.toString();
  }

}
