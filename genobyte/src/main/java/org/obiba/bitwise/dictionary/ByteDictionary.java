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
 * Provides encoding/decoding capabilities between an <tt>Integer</tt> value and a <tt>BitVector</tt> by choosing value precision.
 * The original value will be sampled to the closest higher increment, and must be holded between a lower and upper bound (inclusive).
 */

public class ByteDictionary extends AbstractStaticDictionary<Byte> {

  String name_ = null;
  byte lower_;
  byte upper_;
  byte step_;
  int dimension_ = -1;
  
  boolean propsValidated = false;


  public ByteDictionary(String pName) {
    super();
    name_ = pName;
  }


  /**
   * Sets the lower bound (minimum) for encoded values. Any value to encode to a <tt>BitVector</tt> must be higher or equal to this value.
   * @param pLower the lower bound 
   */
  public void setLower(String pLower) {
    lower_ = Byte.parseByte(pLower);
    propsValidated = false;
  }


  /**
   * Sets the upper bound (maximum) for encoded values. Any value to encode to a <tt>BitVector</tt> must be lower or equal to this value.
   * @param pUpper the upper bound 
   */
  public void setUpper(String pUpper) {
    upper_ = Byte.parseByte(pUpper);
    propsValidated = false;
  }


  /**
   * Sets the value sampling increment for encoded values. Values that fall between two increment values in this this dictionary
   * will take the value of the closest higher increment.
   * @param pStep the step between each sampled value. 
   */
  public void setStep(String pStep) {
    step_ = Byte.parseByte(pStep);
    propsValidated = false;
  }


  public String getName() {
    return name_;
  }


  public Byte convert(String value) {
    validateProperties();
    if(value == null) {
      return null;
    }
    byte b = Byte.parseByte(value);
    if(b > upper_ || b < lower_) {
      return null;
    }
    return b;
  }


  public BitVector lookup(Byte key) {
    validateProperties();
    if(key == null) {
      return null;
    }
    int d = key.byteValue();
    if(lower_ > d) {
      return null;
    }
    if(upper_ < d) {
      return null;
    }
    long ord = ((long)(d - lower_) / step_) + 1;
    return BitUtil.vectorise(ord, dimension());
  }


  public Byte reverseLookup(BitVector v) {
    validateProperties();
    if(v == null) {
      return null;
    }

    return (byte)(lower_ + (BitUtil.longValue(v) - 1 ) * step_);
  }


  public int dimension() {
    validateProperties();
    if(dimension_ == -1) {
      dimension_ = BitUtil.dimension(((short)upper_ - (short)lower_) / (short)step_ + 1);
    }
    return dimension_;
  }


  public boolean isOrdered() {
    return true;
  }

  @Override
  public boolean equals(Object obj) {
    if(obj instanceof ByteDictionary) {
      ByteDictionary bd = (ByteDictionary)obj;
      return this.step_ == bd.step_ && this.lower_ == bd.lower_ && this.upper_ == bd.upper_; 
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
    if(step_ == 0) {
      throw new IllegalArgumentException("Argument step cannot be zero.");
    }
    if(lower_ >= upper_) {
      throw new IllegalArgumentException("Lower bound must be less than upper bound.");
    }
    propsValidated = true;
  }

}
