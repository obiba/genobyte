/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
    if (o instanceof MockDiffQuery) {
      MockDiffQuery rhs = (MockDiffQuery) o;
      return field1_.equals(rhs.field1_) && field2_.equals(rhs.field2_);
    }
    return super.equals(o);
  }
}
