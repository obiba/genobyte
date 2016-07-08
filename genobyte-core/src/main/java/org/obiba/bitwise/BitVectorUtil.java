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
package org.obiba.bitwise;

import org.obiba.bitwise.dto.BitVectorDto;

/**
 * Defines utility methods to exchange data between a <code>BitVector</code> and a <code>BitVectorDto</code>.
 */
public class BitVectorUtil {

  BitVectorUtil() {
    super();
  }

  
  /**
   * Creates a new <code>BitVectorDto</code> object that posesses the same characteristics as a given <code>BitVector</code>.
   * @param id the internal id to give to this BitVectorDto object.
   * @param v the <code>BitVector</code> from which data will be obtained.
   * @return a <code>BitVectorDto</code> based on the information (size, bits) found in the <code>BitVector</code> <b>v</b>.
   */
  static public BitVectorDto toDto(long id, BitVector v) {
    BitVectorDto d = new BitVectorDto();
    d.setId(id);
    d.setSize(v.size());
    d.setBits(v.getBits());
    return d;
  }

  
  /**
   * Creates a new <code>BitVector</code> object that posesses the same characteristics as a given <code>BitVectorDto</code>.
   * @param d the <code>BitVectorDto</code> from which data will be obtained.
   * @return a <code>BitVector</code> based on the information (size, bits) found in the <code>BitVectorDto</code> <b>d</b>.
   */
  static public BitVector toVector(BitVectorDto d) {
    if(d == null) {
      throw new NullPointerException("dto cannot be null");
    }
    return new BitVector(d.getSize(), d.getBits());
  }

}
