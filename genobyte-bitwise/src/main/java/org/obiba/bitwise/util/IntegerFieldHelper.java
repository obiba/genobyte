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
package org.obiba.bitwise.util;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.Dictionary;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.FieldValueIterator;
import org.obiba.bitwise.query.QueryResult;

/**
 * Offers utility methods to manipulate fields that store integers.
 */
public class IntegerFieldHelper {

  /**
   * Sets a value in all record for a given field.
   * @param f the field on which all records must be set to a value.
   * @param value the value to assign to the records field.
   */
  static public void setAll(Field f, int value) {
    if(f == null) return;
    BitVector bitValue = f.getDictionary().lookup(value);
    FieldValueIterator<Integer> fvi = new FieldValueIterator<Integer>(f);
    while(fvi.hasNext()) {
      FieldValueIterator<Integer>.FieldValue fv = fvi.next();
      f.setValue(fv.getIndex(), bitValue);
    }
  }

  /**
   * Adds 1 to a field value for all records in a {@link QueryResult}.
   *
   * @param f the integer field that must be incremented.
   * @param indexes the records which field must be incremented.
   */
  static public void increment(Field f, QueryResult indexes) {
    add(f, indexes, 1);
  }

  /**
   * Adds an integer value to a field value for all records in a {@link QueryResult}.
   * @param f the integer field that must be incremented.
   * @param indexes the records which field must be incremented.
   * @param value the value that must be added.
   */
  static public void add(Field f, QueryResult indexes, int value) {
    if(f == null) return;
    Dictionary<Integer> d = f.getDictionary();
    for(int i = indexes.next(0); i != -1; i = indexes.next(i + 1)) {
      Integer v = d.reverseLookup(f.getValue(i));
      if(v == null) {
        v = 0;
      }
      f.setValue(i, d.lookup(v + value));
    }
  }

  /**
   * Adds an integer value to a field for one record.
   * @param f the integer field that must be incremented.
   * @param index the index of the record which must be incremented.
   * @param value the value that must be added.
   */
  static public void add(Field f, int index, int value) {
    if(f == null) return;
    Dictionary<Integer> d = f.getDictionary();
    Integer v = d.reverseLookup(f.getValue(index));
    if(v == null) {
      v = 0;
    }
    f.setValue(index, d.lookup(v + value));
  }

  /**
   * Computes the sum of a field value for all records in a {@link QueryResult}. Result can be no higher than <tt>Long.MAX_VALUE</tt>.
   *
   * @param f the field used to compute the sum.
   * @param indexes the records that will be used in the sum computation.
   * @return the sum of the records value.
   */
  static public long sum(Field f, QueryResult indexes) {
    if(f == null) return 0;
    Dictionary<Integer> d = f.getDictionary();
    long sum = 0;
    for(int i = indexes.next(0); i != -1; i = indexes.next(i + 1)) {
      Integer v = d.reverseLookup(f.getValue(i));
      if(v != null) sum += v;
    }
    return sum;
  }

  /**
   * Computes the sum of a field value for all records. Result can be no higher than <tt>Long.MAX_VALUE</tt>.
   * @param f the field used to compute the sum.
   * @return the sum of all records value.
   */
  static public long sum(Field f) {
    if(f == null) return 0;
    long sum = 0;
    FieldValueIterator<Integer> fvi = new FieldValueIterator<Integer>(f);
    while(fvi.hasNext()) {
      FieldValueIterator<Integer>.FieldValue fv = fvi.next();
      Integer v = fv.getValue();
      if(v != null) sum += v;
    }
    return sum;
  }
}
