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
import org.obiba.bitwise.query.Query;
import org.obiba.bitwise.query.QueryExecutionException;
import org.obiba.bitwise.query.QueryResult;

public class MockDiffQuery extends Query implements MockQuery {

  private String field1_ = null;

  private String field2_ = null;

  public MockDiffQuery(String field1, String field2) {
    super();
    field1_ = field1;
    field2_ = field2;
  }

  @Override
  public QueryResult execute(BitwiseStore store) throws QueryExecutionException {
    return null;
  }

  public String parsable() {
    return field1_ + "~" + field2_;
  }

  public String toString() {
    return parsable();
  }

  @Override
  public boolean equals(Object o) {
    if(o instanceof MockDiffQuery) {
      MockDiffQuery rhs = (MockDiffQuery) o;
      return field1_.equals(rhs.field1_) && field2_.equals(rhs.field2_);
    }
    return super.equals(o);
  }
}
