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

import org.obiba.bitwise.dto.FieldDto;
import org.obiba.bitwise.query.QueryResult;

/**
 * Represents one field of a Bitwise store. A <code>Field</code> contains a series of <code>BitVector</code>
 * that is used to contain a certain value for any given record. For example, a value encodable on a maximum of
 * 5 bits for any given record will be held in the field using 5 <code>BitVectors</code>. Other information is
 * encapsulated here, such as the dictionary used to convert a value to and from Bitwise bits format, and
 * the <code>BitwiseStore</code> to which the <code>Field</code> belongs.
 */

public class Field extends AbstractField {
  private boolean dirty_ = false;
      //If the Field content has changed since it has been persisted the last time.

  Field(BitwiseStore store, FieldDto data, Dictionary dict, BitVector[] vectors) {
    super();
    super.store_ = store;
    super.data_ = data;
    super.dictionary_ = dict;
    super.vectors_ = vectors;
  }

  /**
   * Returns true if this field was modified since it was persisted.
   * @return true if this field was modified since it was persisted.
   */
  public boolean isDirty() {
    return dirty_;
  }

  void setDirty(boolean d) {
    if(d == true && dirty_ == false) {
      super.store_.flushField(this);
    }
    dirty_ = d;
  }

  /**
   * Increases the number of records that can be held in this <code>Field</code> object.
   * @param size the new number of records that can be held in the <code>Field</code>. This number
   * is the new total number of records, not the number of records to increase. It must be equal or
   * higher than the current maximum number of records.
   */
  public void grow(int size) {
    setDirty(true);
    super.grow(size);
  }

  public void setValue(int record, BitVector value) {
    super.setValue(record, value);
    setDirty(true);
  }

  public void copyValue(int targetIndex, int sourceIndex, Field sourceField) {
    setDirty(true);
    super.copyValue(targetIndex, sourceIndex, sourceField);
  }

  /**
   * Compares this <code>Field</code> with the specified Object for equality.
   * @param o <code>Object</code> to which this Field is to be compared.
   * @return true if the name of both <code>Field</code> objects is the same.
   */
  public boolean equals(Object o) {
    if(o instanceof Field == false) {
      return false;
    }
    Field d = (Field) o;
    return this.data_.getName().equals(d.data_.getName());
  }

  /**
   * Returns a hash code for this Field.
   * @return a hash code value for this object.
   */
  public int hashCode() {
    return super.data_.getName().hashCode();
  }

  @Override
  public void copyValues(AbstractField pSource, QueryResult pQr) {
    super.copyValues(pSource, pQr);
    this.setDirty(true);
  }

  @Override
  public void copyValues(AbstractField source) {
    super.copyValues(source);
    this.setDirty(true);
  }
}
