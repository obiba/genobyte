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
package org.obiba.genobyte.cli;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.obiba.bitwise.query.QueryResult;
import org.obiba.genobyte.GenotypingRecordStore;
import org.obiba.genobyte.GenotypingStore;


/**
 * Each instance of {@link BitwiseCli} has a <tt>CliContext</tt> associated that describes the
 * current execution state. A context instance should be used to pass values between commands.
 */
public class CliContext {

  /** The stream to which any user output should be printed to */
  private PrintStream output_ = null;
  /** The current opened store (may be null) */
  private GenotypingStore<?, ?, ?, ?> store_ = null;
  /** The record store being queried (may be null) */
  private GenotypingRecordStore<?, ?, ?> activeRecordStore_ = null;
  /** Each record store may have up to one query result associated */
  private Map<String, QueryResult> lastResults_ = new HashMap<String, QueryResult>();

  public CliContext(PrintStream ps) {
    output_ = ps;
  }

  public void clear() {
    store_ = null;
    activeRecordStore_ = null;
    lastResults_.clear();
  }

  public PrintStream getOutput() {
    return output_;
  }

  public GenotypingStore<?, ?, ?, ?> getStore() {
    return store_;
  }

  public void setStore(GenotypingStore<?, ?, ?, ?> store) {
    store_ = store;
  }

  public GenotypingRecordStore<?, ?, ?> getActiveRecordStore() {
    return activeRecordStore_;
  }

  public void setActiveRecordStore(GenotypingRecordStore<?, ?, ?> activeRecordStore) {
    activeRecordStore_ = activeRecordStore;
  }

  public QueryResult getLastResult() {
    return lastResults_.get(activeRecordStore_.getStore().getName());
  }

  public void setLastResult(QueryResult lastResult) {
    lastResults_.put(activeRecordStore_.getStore().getName(), lastResult);
  }
  
  public QueryResult getStoreLastResult(GenotypingRecordStore<?, ?, ?> store) {
    return lastResults_.get(store.getStore().getName());
  }

}
