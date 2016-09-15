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

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.Field;

/**
 * Query on a store's field exact value.
 */
public class FieldValueQuery extends Query {

  private String field_ = null;

  private String value_ = null;

  public FieldValueQuery(String field, String value) {
    super();
    field_ = field;
    value_ = value;
  }

  @Override
  public QueryResult execute(BitwiseStore store) throws QueryExecutionException {
    Field f = store.getField(field_);
    if (f == null) {
      throw invalidField(store, field_);
    }
    Object value = null;
    if (value_ != null && value_.equals("null") == false) {
      value = f.getDictionary().convert(value_);
    }
    BitVector v = f.getDictionary().lookup(value);
    return f.query(v);
  }

  /**
   * Creates a <tt>String</tt> representation of this query.
   */
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(field_).append("=[").append(value_).append("]");
    return sb.toString();
  }

}
