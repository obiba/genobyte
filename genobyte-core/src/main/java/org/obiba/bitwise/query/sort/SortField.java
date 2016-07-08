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

/**
 * Sorting clause on a field, in ascending or descending direction.
 * @author plaflamm
 */
public class SortField {

  /** The field name to sort on */
  private String field_ = null;

  /** Set to true to sort in reverse order */
  private boolean reverse_ = false;

  public SortField(String field) {
    this(field, false);
  }

  public SortField(String field, boolean reverse) {
    super();
    field_ = field;
    reverse_ = reverse;
  }

  /**
   * Gets the field this sort criterion is based on.
   * @return Returns the field.
   */
  public String getField() {
    return field_;
  }

  /**
   * Tells whether the sort direction is ascending or descending.
   * @return <tt>true</tt> is the sort direction is descending, <tt>false</tt> if it is ascending.
   */
  public boolean isReverse() {
    return reverse_;
  }

  /**
   * Compares this SortField with the specified Object for equality.
   */
  @Override
  public boolean equals(Object o) {
    if(o instanceof SortField) {
      SortField sf = (SortField) o;
      return field_.equals(sf.field_) && reverse_ == sf.reverse_;
    }
    return super.equals(o);
  }

  /**
   * Returns a hash code for this BitVector.
   */
  @Override
  public int hashCode() {
    return field_.hashCode() * (reverse_ ? -1 : 1);
  }

  /**
   * Creates a <tt>String</tt> representation of this sort criterion.
   */
  @Override
  public String toString() {
    return "sort{" + field_ + (reverse_ ? "!" : "") + "}";
  }

}
