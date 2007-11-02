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
package org.obiba.bitwise.dictionary;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.util.BitUtil;


/**
 * Provides encoding/decoding capabilities between a <tt>Double</tt> value and a <tt>BitVector</tt> by preserving value precision.
 * The original floating point value will be preserved, in opposition to values enconded with <tt>DecimalDictionary</tt>.
 */
public class FloatingPointDictionary extends AbstractStaticDictionary<Double> {

  String name_ = null;
  int dimension_ = Double.SIZE + 1;     //Include an extra bit for null value

  
  public FloatingPointDictionary(String pName) {
    super();
    name_ = pName;
  }


  public String getName() {
    return name_;
  }


  public Double convert(String value) {
    return Double.parseDouble(value);
  }


  public BitVector lookup(Double key) {
    if(key == null) {
      return null;
    }
    long ord = Double.doubleToLongBits(key.doubleValue());
    BitVector v = BitUtil.vectorise(ord, dimension());
    v.set(dimension_ - 1);     //Setting the extra bit to "one" to make it a non-null value
    return v;
  }


  public Double reverseLookup(BitVector v) {
    if(v == null) {
      return null;
    }
    
    //The last bit is used to check if value is zero or null. If it's zero, just convert the 64 first bits to a double value.
    if (v.get(dimension_ - 1)) {
      double tempValue = Double.longBitsToDouble(BitUtil.longValue(v));
      return new Double(tempValue);
    }
    else {
      return null;
    }
  }


  public int dimension() {
    return dimension_;
  }


  public boolean isOrdered() {
    return false;
  }
}
