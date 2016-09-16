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
 * Provides encoding/decoding capabilities between an <tt>Long</tt> value and a <tt>BitVector</tt> by preserving value precision.
 * The original integer value will be preserved, in opposition to values enconded with <tt>IntegerDictionary</tt>.
 */
public class LongDictionary extends AbstractStaticDictionary<Long> {

  String name_ = null;

  int dimension_ = Long.SIZE + 1;     //Include an extra bit for null value

  public LongDictionary(String pName) {
    super();
    name_ = pName;
  }

  public String getName() {
    return name_;
  }

  public Long convert(String value) {
    return Long.parseLong(value);
  }

  public BitVector lookup(Long key) {
    if (key == null) {
      return null;
    }
    long ord = key.longValue();
    BitVector v = BitUtil.vectorise(ord, dimension());
    v.set(dimension_ - 1);     //Setting the extra bit to "one" to make it a non-null value
    return v;
  }

  public Long reverseLookup(BitVector v) {
    if (v == null) {
      return null;
    }

    //The last bit is used to check if value is zero or null. If it's zero, just convert the 64 first bits to a long value.
    if (v.get(dimension_ - 1)) {
      long tempValue = BitUtil.longValue(v);
      return new Long(tempValue);
    } else {
      return null;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof LongDictionary) {
      return true;
    }
    return super.equals(obj);
  }

  public int dimension() {
    return dimension_;
  }

  public boolean isOrdered() {
    return true;
  }

}