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
 * Holds the information relative to the sorting that should be applied on a query resultset.
 */
public class Sort {

  SortField[] fields_ = null;

  public Sort() {
    this(null);
  }

  public Sort(SortField[] fields) {
    super();
    fields_ = fields;
  }

  /**
   * Gets the sort criteria to be applied by this sort, in the order that they will be applied to a resultset.
   *
   * @return the sort criteria.
   */
  public SortField[] getSort() {
    return fields_;
  }

  /**
   * Sets the unique field on which resultsets will be sorted. By providing a <tt>SortField</tt> object, both the field
   * and its sort order can be specified.
   *
   * @param field
   */
  public void setSort(SortField field) {
    fields_ = new SortField[]{field};
  }

  /**
   * Sets the group of fields on which resultsets will be sorted, following the order in which they will be sorted.
   * By providing <tt>SortField</tt> objects, both the fields and their sort order can be specified.
   *
   * @param fields the array of <tt>SortField</tt> objects specifying the sorting fields and their direction.
   */
  public void setSort(SortField[] fields) {
    fields_ = fields;
  }

  /**
   * Sets the group of fields on which resultsets will be sorted, by providing only the name of the field. The fields will
   * always be sorted in ascending order.
   *
   * @param fields the array of fields used in the sort.
   */
  public void setSort(String[] fields) {
    fields_ = new SortField[fields.length];
    for (int i = 0; i < fields.length; i++) {
      String field = fields[i];
      fields_[i] = new SortField(field);
    }
  }

}
