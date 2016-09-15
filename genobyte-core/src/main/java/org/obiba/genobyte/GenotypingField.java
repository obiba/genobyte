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
package org.obiba.genobyte;

/**
 * Defines a genotyping field used in a <tt>GenotypingStore</tt>, providing information about its role in the store.
 * Genotyping fields are the fields that are directly related to genotypes, such as the calls themselves and statistics
 * related to them. They do not include things such as a sample name, or relationship to other samples, an assay variant
 * position in the genome, etc.
 */
public interface GenotypingField {

  /**
   * Returns the name of this genotyping field.
   *
   * @return the genotyping field name.
   */
  public String getName();

  /**
   * Returns <tt>true</tt> if the values of this field should be transposed into the associated <tt>GenotypingRecordStore</tt>.
   *
   * @return <tt>true</tt> if the values should be transposed, <tt>false</tt> otherwise.
   */
  public boolean isTransposed();

  /**
   * Returns <tt>true</tt> if this field can be created when it does not exist in the bitwise.
   *
   * @return <tt>true</tt> when the field can be created.
   */
  public boolean isCreatable();

  /**
   * Returns <tt>true</tt> when the statistics of the associated record should be updated after transposing the values for this field.
   *
   * @return <tt>true</tt> to recalculate the record's stats after transposing the values, <tt>false</tt> otherwise.
   * @see GenotypingRecordStore.updateStats()
   */
  public boolean updateStats();

}
