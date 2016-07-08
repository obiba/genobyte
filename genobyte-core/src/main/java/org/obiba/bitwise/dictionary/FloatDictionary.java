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
package org.obiba.bitwise.dictionary;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.util.BitUtil;

/**
 * Provides encoding/decoding capabilities between a <tt>Float</tt> value and a <tt>BitVector</tt> by preserving value precision.
 * The original floating point value will be preserved, in opposition to values enconded with <tt>DecimalDictionary</tt>.
 */
public class FloatDictionary extends AbstractStaticDictionary<Float> {

  String name_ = null;

  int dimension_ = Float.SIZE + 1;     //Include an extra bit for null value

  public FloatDictionary(String pName) {
    super();
    name_ = pName;
  }

  public String getName() {
    return name_;
  }

  public Float convert(String value) {
    return Float.parseFloat(value);
  }

  public BitVector lookup(Float key) {
    if(key == null) {
      return null;
    }
    int ord = Float.floatToIntBits(key.floatValue());
    BitVector v = BitUtil.vectorise(ord, dimension());
    v.set(dimension_ - 1);     //Setting the extra bit to "one" to make it a non-null value
    return v;
  }

  public Float reverseLookup(BitVector v) {
    if(v == null) {
      return null;
    }

    //The last bit is used to check if value is zero or null. If it's zero, just convert the 64 first bits to a double value.
    if(v.get(dimension_ - 1)) {
      float tempValue = Float.intBitsToFloat((int) BitUtil.longValue(v));
      return new Float(tempValue);
    } else {
      return null;
    }
  }

  public int dimension() {
    return dimension_;
  }

  public boolean isOrdered() {
    return true;
  }

  @Override
  public boolean equals(Object obj) {
    if(obj instanceof FloatDictionary) return true;
    return super.equals(obj);
  }
}
