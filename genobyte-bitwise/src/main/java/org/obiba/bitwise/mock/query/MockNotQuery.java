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
    if (o instanceof MockNotQuery) {
      MockNotQuery rhs = (MockNotQuery) o;
      return query_.equals(rhs.query_);
    }
    return super.equals(o);
  }
}
