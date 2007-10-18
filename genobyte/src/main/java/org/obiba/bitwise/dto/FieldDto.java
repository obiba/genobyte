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
package org.obiba.bitwise.dto;

/**
 * Transfer object for a <tt>Field</tt> object.
 * 
 * Allows persisting a <tt>Field</tt> object through a FieldDao implementation.
 */
public class FieldDto {

  /** The unique name of this Field */
  private String name_ = null;
  /** Number of bits per bit vector */
  private int size_ = -1;
  /** Indexes of every bit vectors of this field. */
  private int[] bitIndex_ = null;
  /** The name of the field dictionary */
  private String dictionaryName_ = null;

  public FieldDto() {
    super();
  }

  /**
   * @return the bitIndex.
   */
  public int[] getBitIndex() {
    return bitIndex_;
  }

  /**
   * @return the dictionaryName.
   */
  public String getDictionaryName() {
    return dictionaryName_;
  }

  /**
   * @return the name.
   */
  public String getName() {
    return name_;
  }

  /**
   * @return the size.
   */
  public int getSize() {
    return size_;
  }

  /**
   * @param bitIndex the bitIndex to set.
   */
  public void setBitIndex(int[] bitIndex) {
    bitIndex_ = bitIndex;
  }

  /**
   * @param dictionaryName the dictionaryName to set.
   */
  public void setDictionaryName(String dictionaryName) {
    dictionaryName_ = dictionaryName;
  }

  /**
   * @param name the name to set.
   */
  public void setName(String name) {
    name_ = name;
  }

  /**
   * @param size the size to set.
   */
  public void setSize(int size) {
    size_ = size;
  }
  
  
}