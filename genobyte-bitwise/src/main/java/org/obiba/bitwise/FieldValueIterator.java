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
package org.obiba.bitwise;

import org.obiba.bitwise.query.QueryResult;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Can be used to iterate on <code>Field</code> values. This class will effectively
 * skip any deleted record and provide values for each valid record in the field's store.
 */
public class FieldValueIterator<T> implements Iterator<FieldValueIterator<T>.FieldValue> {

  Field field_ = null;

  BitwiseStore store_ = null;

  int nextIndex_ = -1;

  Dictionary<T> dict_ = null;

  QueryResult mask_ = null;

  /**
   * Initializes an iterator on a <tt>Field</tt> instance.
   *
   * @param f the <tt>Field</tt> on which the iterator will be based.
   */
  public FieldValueIterator(Field f) {
    this(f, null);
  }

  public FieldValueIterator(Field f, QueryResult mask) {
    super();
    if (f == null) {
      throw new NullPointerException("field cannot be null");
    }
    field_ = f;
    mask_ = mask;

    store_ = field_.store_;
    dict_ = f.getDictionary();
    nextIndex_ = getNextIndex(0);
  }

  /**
   * Returns the next valid index that is a record and part of the mask (if any);
   *
   * @param current
   * @return
   */
  protected int getNextIndex(int current) {
    int index = store_.nextRecord(current);
    while (this.mask_ != null && index != -1 && this.mask_.get(index) == false) {
      index = store_.nextRecord(index + 1);
    }
    return index;
  }

  /**
   * Initializes a empty <tt>Field</tt> iterator.
   */
  protected FieldValueIterator() {
  }

  /**
   * Returns true if the <tt>Field</tt> iteration has more elements.
   *
   * @return <tt>true</tt> if the iterator has more elements.
   */
  public boolean hasNext() {
    return nextIndex_ != -1;
  }

  /**
   * Returns the next element in the iteration.
   *
   * @return the next element in the iteration.
   */
  public FieldValue next() {
    if (nextIndex_ == -1) {
      throw new NoSuchElementException("no more elements while iterating values from field [" + field_.getName() + "]");
    }
    int currentIndex = nextIndex_;
    nextIndex_ = getNextIndex(nextIndex_ + 1);
    return new FieldValue(currentIndex);
  }

  /**
   * This method is not supported, as the record removal for a single <tt>Field</tt> of a bitwise store is not allowed. Do not use.
   */
  @Deprecated
  public void remove() {
    throw new UnsupportedOperationException();
  }

  /**
   * Provides access to a field's value at a given record index. This is used by <tt>FieldValueIterator</tt> to return
   * the value at the current position for the field on which we are iterating.
   */
  public class FieldValue {
    int index_ = -1;

    BitVector value_ = null;

    boolean valueFetched_ = false;

    T realValue_ = null;

    boolean realValueFetched_ = false;

    FieldValue(int index) {
      index_ = index;
    }

    /**
     * Returns the current record iteration index.
     *
     * @return the index.
     */
    public int getIndex() {
      return index_;
    }

    /**
     * Returns the current record iteration <tt>Field</tt> value, expressed in the bitwise store native format, <tt>BitVector</tt>.
     *
     * @return the <tt>BitVector</tt> value at <tt>index</tt>.
     */
    public BitVector getBitValue() {
      return valueFetched_ == false ? value_ = field_.getValue(index_) : value_;
    }

    /**
     * Returns the current record iteration <tt>Field</tt> value, in its original type.
     *
     * @return the value.
     */
    public T getValue() {
      return valueFetched_ == false ? realValue_ = dict_.reverseLookup(getBitValue()) : realValue_;
    }
  }
}
