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


public class MockNotQuery extends Query implements MockQuery {

  private Query query_ = null;

  public MockNotQuery(Query q) {
    super();
    query_ = q;
  }
  
  public QueryResult execute(BitwiseStore store) throws QueryExecutionException {
    return query_.execute(store).not();
  }

  public String parsable() {
    return "(NOT " + query_ + ")";
  }

  @Override
  public String toString() {
    return "!" + query_;
  }
  
  @Override
  public boolean equals(Object o) {
    if(o instanceof MockNotQuery) {
      MockNotQuery rhs = (MockNotQuery)o;
      return query_.equals(rhs.query_);
    }
    return super.equals(o);
  }
}
