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
 * Provides encoding/decoding capabilities between a <tt>Double</tt> value and a <tt>BitVector</tt> by choosing value precision.
 * The original value will be sampled to the closest higher increment, and must be holded between a lower and upper bound (inclusive).
 * It is therefore compressed to be more space efficient, resulting in a loss in precision.
 */
public class DecimalDictionary extends AbstractStaticDictionary<Double> {

  String name_ = null;

  double lower_;

  double upper_;

  double step_;

  int dimension_ = -1;

  boolean propsValidated = false;

  public DecimalDictionary(String pName) {
    super();
    name_ = pName;
  }

  /**
   * Sets the lower bound (minimum) for encoded values. Any value to encode to a <tt>BitVector</tt> must be higher or equal to this value.
   *
   * @param pLower the lower bound
   */
  public void setLower(double pLower) {
    lower_ = pLower;
    propsValidated = false;
  }

  /**
   * Sets the upper bound (maximum) for encoded values. Any value to encode to a <tt>BitVector</tt> must be lower or equal to this value.
   *
   * @param pUpper the upper bound
   */
  public void setUpper(double pUpper) {
    upper_ = pUpper;
    propsValidated = false;
  }

  /**
   * Sets the value sampling increment for encoded values. Values that fall between two increment values in this this dictionary
   * will take the value of the closest higher increment.
   *
   * @param pStep the step between each sampled value.
   */
  public void setStep(double pStep) {
    step_ = pStep;
    propsValidated = false;
  }

  public String getName() {
    return name_;
  }

  public Double convert(String value) {
    validateProperties();
    double d = Double.parseDouble(value);
    if (Double.compare(lower_, d) > 0 || Double.compare(upper_, d) < 0) {
      return null;
    }
    return d;
  }

  public BitVector lookup(Double key) {
    validateProperties();
    if (key == null) {
      return null;
    }
    double d = key.doubleValue();
    if (Double.compare(lower_, d) > 0) {
      return null;
    }
    if (Double.compare(upper_, d) < 0) {
      return null;
    }
    long ord = (long) Math.ceil(((d - lower_) / step_) + 1.0d - step_);
    return BitUtil.vectorise(ord, dimension());
  }

  public Double reverseLookup(BitVector v) {
    validateProperties();
    if (v == null) {
      return null;
    }
    return lower_ + (BitUtil.longValue(v) - 1) * step_;
  }

  public int dimension() {
    validateProperties();
    if (dimension_ == -1) {
      dimension_ = BitUtil.dimension((long) ((upper_ - lower_) / step_ + 1l));
    }
    return dimension_;
  }

  public boolean isOrdered() {
    return true;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof DecimalDictionary) {
      DecimalDictionary dd = (DecimalDictionary) obj;
      return this.step_ == dd.step_ && this.lower_ == dd.lower_ && this.upper_ == dd.upper_;
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

    if (Double.compare(step_, 0) == 0) {
      throw new IllegalArgumentException("Argument step cannot be zero.");
    }

    if (Double.compare(lower_, upper_) >= 0) {
      throw new IllegalArgumentException("Lower bound must be less than upper bound.");
    }

    double maxOrder = (upper_ - lower_) / step_;
    if (Double.compare(Long.MAX_VALUE, maxOrder) < 0) {
      throw new IllegalArgumentException(
          "Dictionary bounds are too large. Cannot represent values from [" + lower_ + "] to [" + upper_ +
              "] with a step of [" + step_ + "]. Reduce the step or the bounds.");
    }

    propsValidated = true;
  }

}