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
    return field_ + ":[" + from_ +" , "+to_+"]";
  }

  @Override
  public boolean equals(Object o) {
    if(o instanceof MockRangeQuery) {
      MockRangeQuery rhs = (MockRangeQuery)o;
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
