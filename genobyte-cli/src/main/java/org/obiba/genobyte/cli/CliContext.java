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
package org.obiba.genobyte.cli;

import java.io.PrintStream;
import java.util.ArrayList;

import org.obiba.bitwise.query.QueryResult;
import org.obiba.genobyte.GenotypingRecordStore;
import org.obiba.genobyte.GenotypingStore;

/**
 * Each instance of {@link BitwiseCli} has a <tt>CliContext</tt> associated that describes the
 * current execution state. A <tt>CliContext</tt> instance should be used to pass values between commands instead of linking two commands together.
 */
public class CliContext {

  /** The stream to which any user output should be printed to */
  private PrintStream output_ = null;

  /** The current opened store (may be null) */
  private GenotypingStore<?, ?, ?, ?> store_ = null;

  /** The record store being queried (may be null) */
  private GenotypingRecordStore<?, ?, ?> activeRecordStore_ = null;

  private QueryHistory history = new QueryHistory();

  public CliContext(PrintStream ps) {
    output_ = ps;
  }

  public void clear() {
    store_ = null;
    activeRecordStore_ = null;
    history.clear();
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

  public String addQuery(String query, QueryResult result) {
    return history.pushQuery(query, result);
  }

  public QueryHistory getHistory() {
    return history;
  }

  public class QueryHistory {

    private ArrayList<QueryExecution> queries = new ArrayList<QueryExecution>(100);

    /** The QueryResult with the most results in the history. Used for padding the count column */
    private int maxCount = -1;

    public String pushQuery(String query, QueryResult result) {
      queries.add(new QueryExecution(query, result));
      if(result.count() > maxCount) {
        maxCount = result.count();
      }
      return "q" + queries.size();
    }

    public QueryExecution getLast() {
      return queries.get(queries.size() - 1);
    }

    public QueryExecution getLast(GenotypingRecordStore<?, ?, ?> store) {
      String storeName = store.getStore().getName();
      for(int i = queries.size() - 1; i >= 0; i--) {
        QueryExecution qe = queries.get(i);
        if(qe.store.getStore().getName().equals(storeName)) {
          return qe;
        }
      }
      return null;
    }

    public boolean isQueryReference(String reference) {
      if(reference.length() < 2) return false;
      String id = reference.substring(1);
      try {
        int index = Integer.parseInt(id) - 1;
        // Matches a query reference format
        return true;
      } catch(NumberFormatException e) {
        return false;
      }
    }

    /**
     * Resolve a query reference into the QueryExecution that resulted.
     * @param reference the reference to a QueryExecution. Format is q# where # is the index (1-based) of the query in the history.
     * @return the QueryExecution associated with the reference.
     * @throws IllegalArgumentException when reference is not a valid query reference.
     */
    public QueryExecution resolveQuery(String reference) {
      if(queries.size() == 0) throw new IllegalArgumentException(reference +
          " is not a valid query reference. No queries in history: execute at least one query to be able to reference them later.");
      if(reference.length() < 2)
        throw new IllegalArgumentException(reference + " is not a valid query reference. Expected format is \"q#\".");
      String id = reference.substring(1);
      try {
        int index = Integer.parseInt(id) - 1;
        if(index < 0 || index >= queries.size()) {
          throw new IllegalArgumentException(
              reference + " is not a valid query reference. The index should be between 1 and " + queries.size() + ".");
        }
        return get(index);
      } catch(NumberFormatException e) {
        throw new IllegalArgumentException(reference + " is not a valid query reference. Expected format is \"q#\".");
      }
    }

    public QueryExecution get(int index) {
      return queries.get(index);
    }

    public void clear() {
      queries.clear();
      maxCount = -1;
    }

    public int size() {
      return queries.size();
    }

    public int getMaxCount() {
      return maxCount;
    }

  }

  public class QueryExecution {

    private GenotypingRecordStore<?, ?, ?> store;

    private String query;

    private QueryResult result;

    QueryExecution(String q, QueryResult qr) {
      this(activeRecordStore_, q, qr);
    }

    QueryExecution(GenotypingRecordStore<?, ?, ?> store, String q, QueryResult qr) {
      this.store = store;
      this.query = q;
      this.result = qr;
    }

    public String getQuery() {
      return query;
    }

    public int count() {
      return result.count();
    }

    public QueryResult getResult() {
      return result;
    }

    public GenotypingRecordStore<?, ?, ?> getStore() {
      return store;
    }
  }
}
