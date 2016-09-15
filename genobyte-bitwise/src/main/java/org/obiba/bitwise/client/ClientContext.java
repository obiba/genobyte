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
package org.obiba.bitwise.client;

import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.query.QueryResult;

/**
 * This bean contains the contextual information for a client session.
 */
public class ClientContext {

  private BitwiseStore store_ = null;

  private QueryResult lastResult_ = null;

  /**
   * Returns the result of the last query ran in a session for a store.
   *
   * @return The QueryResult obtained from running the last query.
   */
  public QueryResult getLastResult() {
    return lastResult_;
  }

  /**
   * Gets the currently opened store.
   *
   * @return The BitwiseStore
   */
  public BitwiseStore getStore() {
    return store_;
  }

  /**
   * Saves the result of the last query ran in a session for a store.
   *
   * @param lastResult is the QueryResult of the last query that has been ran.
   */
  public void setLastResult(QueryResult lastResult) {
    lastResult_ = lastResult;
  }

  /**
   * Swtiches the context of the client session to a new store. All following queries will be ran on
   * this new store. The result of the last query ran on the previous store will be lost.
   *
   * @param store is the newly opened store that will be used in the client to run queries.
   */
  public void setStore(BitwiseStore store) {
    store_ = store;
    lastResult_ = null;   //Now irrelevant because we just switched store
  }
}
