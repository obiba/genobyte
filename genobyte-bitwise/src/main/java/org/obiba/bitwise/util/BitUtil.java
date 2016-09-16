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
package org.obiba.bitwise.util;

import org.obiba.bitwise.BitVector;

/**
 * Utility methods for working with bits and <tt>BitVector</tt> objects.
 */
public class BitUtil {

  /**
   * Returns the index of the last one bit in <code>value</code>.
   *
   * @param value the value to test
   * @return the index of the last one bit
   */
  static public int dimension(long value) {
    for (int i = 63; i >= 0; i--) {
      if ((value & ((long) 1 << i)) != 0) {
        return i + 1;
      }
    }
    return 0;
  }

  /**
   * Returns the index of the last one bit in <code>value</code>.
   *
   * @param value the value to test
   * @return the index of the last one bit
   */
  static public int dimension(int value) {
    for (int i = 31; i >= 0; i--) {
      if ((value & (1 << i)) != 0) {
        return i + 1;
      }
    }
    return 0;
  }

  /**
   * Converts <code>value</code> into a BitVector of size <code>dimension</code>.
   *
   * @param value     the value to convert
   * @param dimension the size of the resulting <code>BitVector</code>
   * @return a <code>BitVector</code> of size <code>dimension</code>.
   */
  static public BitVector vectorise(long value, int dimension) {
    BitVector v = new BitVector(dimension);
    for (int i = 0; i < dimension; i++) {
      if ((value & ((long) 1 << i)) != 0) {
        v.set(i);
      }
    }
    return v;
  }

  /**
   * Converts <code>value</code> into a BitVector of size <code>dimension</code>.
   *
   * @param value     the value to convert
   * @param dimension the size of the resulting <code>BitVector</code>
   * @return a <code>BitVector</code> of size <code>dimension</code>.
   */
  static public BitVector vectorise(int value, int dimension) {
    BitVector v = new BitVector(dimension);
    for (int i = 0; i < dimension; i++) {
      if ((value & (1 << i)) != 0) {
        v.set(i);
      }
    }
    return v;
  }

  /**
   * Converts <code>v</code> into a long. If this BitVector is too big to fit in a long,
   * only the low-order 64 bits are returned.
   *
   * @param v the <code>BitVector</code> to convert
   * @return a long value
   */
  static public long longValue(BitVector v) {
    return v.longValue();
  }

}
