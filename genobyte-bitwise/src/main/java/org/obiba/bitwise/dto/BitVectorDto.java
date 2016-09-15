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
package org.obiba.bitwise.dto;

/**
 * Transfer object for a <tt>BitVector</tt>.
 *
 * Allows persisting a <tt>BitVector</tt> object through a BitVectorDao implementation.
 */
public class BitVectorDto {

  /** The unique identifier of this bit vector (primary key)*/
  long id_ = -1;

  /** The number of bits this vector holds */
  private int size_;

  /** Array of bits (holds the actual bit values) */
  private long[] bits_;

  /**
   * Constructs an empty transfer object. 
   */
  public BitVectorDto() {
    super();
  }

  /**
   * @return the bits.
   */
  public long[] getBits() {
    return bits_;
  }

  /**
   * @return the id.
   */
  public long getId() {
    return id_;
  }

  /**
   * @return the size.
   */
  public int getSize() {
    return size_;
  }

  /**
   * @param bits the bits to set.
   */
  public void setBits(long[] bits) {
    bits_ = bits;
  }

  /**
   * @param id the id to set.
   */
  public void setId(long id) {
    id_ = id;
  }

  /**
   * @param size the size to set.
   */
  public void setSize(int size) {
    size_ = size;
  }

}
