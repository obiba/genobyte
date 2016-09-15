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
package org.obiba.bitwise.query.sort;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.bitwise.query.UnknownFieldException;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Query resultset that is sorted following provided criteria.
 */
public class SortedQueryResult implements QueryResult {

  BitwiseStore store_ = null;

  Sort sort_ = null;

  QueryResult result_ = null;

  int[] sorted_ = null;

  private SortField[] sortFields_ = null;

  public SortedQueryResult(BitwiseStore store, Sort sort, QueryResult result) {
    store_ = store;
    sort_ = sort;
    result_ = result;
    sortFields_ = sort_.getSort();
  }

  /*
   * @see org.obiba.bitwise.query.QueryResult#and(org.obiba.bitwise.query.QueryResult)
   */
  public QueryResult and(QueryResult r) {
    return result_.and(r);
  }

  /*
   * @see org.obiba.bitwise.query.QueryResult#andNot(org.obiba.bitwise.query.QueryResult)
   */
  public QueryResult andNot(QueryResult r) {
    return result_.andNot(r);
  }

  /*
   * @see org.obiba.bitwise.query.QueryResult#bits()
   */
  public BitVector bits() {
    return result_.bits();
  }

  /*
   * @see org.obiba.bitwise.query.QueryResult#getFilter()
   */
  public BitVector getFilter() {
    return result_.getFilter();
  }

  /*
   * @see org.obiba.bitwise.query.QueryResult#copy()
   */
  public QueryResult copy() {
    return result_.copy();
  }

  /*
   * @see org.obiba.bitwise.query.QueryResult#count()
   */
  public int count() {
    return result_.count();
  }

  /*
   * @see org.obiba.bitwise.query.QueryResult#get(int)
   */
  public boolean get(int index) {
    return result_.get(index);
  }

  /*
   * @see org.obiba.bitwise.query.QueryResult#next(int)
   */
  public int hit(int index) {
    if (sorted_ == null) {
      sort();
    }
    if (index < sorted_.length) {
      return sorted_[index];
    }
    return -1;
  }

  public int next(int index) {
    return result_.next(index);
  }

  /*
   * @see org.obiba.bitwise.query.QueryResult#not()
   */
  public QueryResult not() {
    return result_.not();
  }

  /*
   * @see org.obiba.bitwise.query.QueryResult#or(org.obiba.bitwise.query.QueryResult)
   */
  public QueryResult or(QueryResult r) {
    return result_.or(r);
  }

  /*
   * @see org.obiba.bitwise.query.QueryResult#xor(org.obiba.bitwise.query.QueryResult)
   */
  public QueryResult xor(QueryResult r) {
    return result_.xor(r);
  }

  /**
   * Sorts the <link>QueryResult</link> according to the contents of <code>sortFields_</code>.
   * This method will initialze the <code>sorted_</code> field.
   */
  private void sort() {
    PriorityQueue<SortNode> sorted = new PriorityQueue<SortNode>(result_.count());
    for (int i = result_.next(0); i != -1; i = result_.next(i + 1)) {
      SortNode node = new SortNode(i);
      // PriorityQueue sorts on insert
      sorted.add(node);
    }

    int index = 0;
    sorted_ = new int[result_.count()];
    while (sorted.size() > 0) {
      // use the remove() method to iterate on sort order
      sorted_[index++] = sorted.remove().recordIndex_;
    }
  }

  /**
   * Used to obtain the <link>Comparable</link> instance used to compare a record's field value
   *
   * @param index     the index of the record for which we need the <link>Comparable</link> instance.
   * @param sortField the <link>SortField</link> instance the <link>Comparable</link> should represent.
   * @return a <link>Comparable</link> instance for the specified record on the specified field
   * @throws UnknownFieldException when the specified sort field does not exist.
   */
  @SuppressWarnings("unchecked")
  private Comparable<Object> getOrder(int index, SortField sortField) {
    String fieldName = sortField.getField();
    Field field = store_.getField(fieldName);
    if (field == null) {
      throw new UnknownFieldException(store_.getName(), fieldName, "Cannot sort on unknown field.");
    }
    BitVector value = field.getValue(index);
    if (field.getDictionary().isOrdered()) {
      return value;
    }
    return (Comparable<Object>) field.getDictionary().reverseLookup(value);
  }

  /**
   * Returns the reverse flag of the <code>i</code>th sort clause.
   *
   * @param index the index of the sort clause to test
   * @return the reverse flag of the <code>i</code>th sort clause.
   */
  private boolean isReverse(int index) {
    return sortFields_[index].isReverse();
  }

  /**
   * A <link>Comparable</link> implementation
   *
   * @author plaflamm
   */
  private class SortNode implements Comparable {
    int recordIndex_ = -1;

    // We can't use an Array of Comparables because we can't mix Object Classes in an Array of interfaces
    List<Comparable<Object>> comparables_ = null;

    boolean[] initialized_ = null;

    SortNode(int index) {
      recordIndex_ = index;
      comparables_ = new ArrayList<Comparable<Object>>(sortFields_.length);
      while (comparables_.size() < sortFields_.length) comparables_.add(null);
      initialized_ = new boolean[sortFields_.length];
    }

    @Override
    public boolean equals(Object o) {
      return recordIndex_ == ((SortNode) o).recordIndex_;
    }

    @Override
    public int hashCode() {
      return recordIndex_;
    }

    public int compareTo(Object o) {
      SortNode other = (SortNode) o;
      for (int i = 0; i < comparables_.size(); i++) {
        Comparable<Object> l = comparable(i);
        Comparable<Object> r = other.comparable(i);
        if (l == null) return -1;
        if (r == null) return 1;
        int c = l.compareTo(r);
        if (c != 0) {
          return isReverse(i) ? c * -1 : c;
        }
      }
      // Everything else is identical. Use the record index as a last resort.
      return recordIndex_ - other.recordIndex_;
    }

    private Comparable<Object> comparable(int i) {
      Comparable<Object> c = comparables_.get(i);
      if (initialized_[i] == false) {
        c = getOrder(recordIndex_, sortFields_[i]);
        comparables_.set(i, c);
        initialized_[i] = true;
      }
      return c;
    }
  }
}
