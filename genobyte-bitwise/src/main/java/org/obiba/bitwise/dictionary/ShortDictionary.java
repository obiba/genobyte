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
 * Provides encoding/decoding capabilities between an <tt>Integer</tt> value and a <tt>BitVector</tt> by choosing value precision.
 * The original value will be sampled to the closest higher increment, and must be holded between a lower and upper bound (inclusive).
 */

public class ShortDictionary extends AbstractStaticDictionary<Short> {

  String name_ = null;

  short lower_;

  short upper_;

  short step_;

  int dimension_ = -1;

  boolean propsValidated = false;

  public ShortDictionary(String pName) {
    super();
    name_ = pName;
  }

  /**
   * Sets the lower bound (minimum) for encoded values. Any value to encode to a <tt>BitVector</tt> must be higher or equal to this value.
   *
   * @param pLower the lower bound
   */
  public void setLower(String pLower) {
    lower_ = Short.parseShort(pLower);
    propsValidated = false;
  }

  /**
   * Sets the upper bound (maximum) for encoded values. Any value to encode to a <tt>BitVector</tt> must be lower or equal to this value.
   *
   * @param pUpper the upper bound
   */
  public void setUpper(String pUpper) {
    upper_ = Short.parseShort(pUpper);
    propsValidated = false;
  }

  /**
   * Sets the value sampling increment for encoded values. Values that fall between two increment values in this this dictionary
   * will take the value of the closest higher increment.
   *
   * @param pStep the step between each sampled value.
   */
  public void setStep(String pStep) {
    step_ = Short.parseShort(pStep);
    propsValidated = false;
  }

  public String getName() {
    return name_;
  }

  public Short convert(String value) {
    validateProperties();
    if (value == null) {
      return null;
    }
    short s = Short.parseShort(value);
    if (s > upper_ || s < lower_) {
      return null;
    }
    return s;
  }

  public BitVector lookup(Short key) {
    validateProperties();
    if (key == null) {
      return null;
    }
    short d = key.shortValue();
    if (lower_ > d) {
      return null;
    }
    if (upper_ < d) {
      return null;
    }
    long ord = ((long) (d - lower_) / step_) + 1;
    return BitUtil.vectorise(ord, dimension());
  }

  public Short reverseLookup(BitVector v) {
    validateProperties();
    if (v == null) {
      return null;
    }
    return (short) (lower_ + (BitUtil.longValue(v) - 1) * step_);
  }

  public int dimension() {
    validateProperties();
    if (dimension_ == -1) {
      dimension_ = BitUtil.dimension((((long) upper_ - (long) lower_) / (long) step_ + 1));
    }
    return dimension_;
  }

  public boolean isOrdered() {
    return true;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ShortDictionary) {
      ShortDictionary sd = (ShortDictionary) obj;
      return this.step_ == sd.step_ && this.lower_ == sd.lower_ && this.upper_ == sd.upper_;
    }
    return super.equals(obj);
  }

  /**
   * Makes sure that the properties <code>lower</code>, <code>upper</code> and <code>step</code> are
   * correctly defined. This method will be invoked in methods using these parameters, as there
   * is no way to know in which order they have been defined.
   */
  private void validateProperties() {
    if (propsValidated) {
      return;
    }
    if (step_ == 0) {
      throw new IllegalArgumentException("Argument step cannot be zero.");
    }
    if (lower_ >= upper_) {
      throw new IllegalArgumentException("Lower bound must be less than upper bound.");
    }
    propsValidated = true;
  }

}