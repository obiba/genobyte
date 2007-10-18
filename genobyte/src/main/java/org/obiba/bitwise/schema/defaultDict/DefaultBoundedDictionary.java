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
package org.obiba.bitwise.schema.defaultDict;

import org.obiba.bitwise.Dictionary;
import org.obiba.bitwise.schema.DictionaryMetaData;


/**
 * Bounded dictionaries are dictionaries dealing with numeric data, and designed to be compressed when a value transformed
 * into a <tt>BitVector</tt>. The compression is accomplished by determining minimal and maximal bounds in which all values must fit, and
 * a step value (increment). There are therefore three mandatory properties to any dictionary of this type. <tt>lower</tt> is the minimal
 * bound for any value of the field. <tt>upper</tt> is the maximal bound. <tt>step</tt> is the minimal possible increment between two values. 
 */
public abstract class DefaultBoundedDictionary<T> extends DefaultDictionary {
  T lower_;
  T upper_;
  T step_;


  public DefaultBoundedDictionary() {
    super();
  }


  /**
   * Add the Bounded type essential parameters before invoking the superclass DictionaryMetaData constructor.
   */
  @Override
  public DictionaryMetaData getDict() {
    DictionaryMetaData dict = super.getDict();
    return dict;
  }
  
  
  /**
   * Gets the lower bound for the dictionary.
   * @return the lower bound.
   */
  public T getLower() { return lower_; }


  /**
   * Sets the lower bound for the dictionary.
   * @param pLower the lower bound.
   */
  public void setLower(T pLower) {
    properties_.put("lower", pLower.toString());
    lower_ = pLower;
  }


  /**
   * Gets the upper bound for the dictionary.
   * @return the upper bound.
   */
  public T getUpper() { return upper_; }


  /**
   * Sets the upper bound for the dictionary.
   * @param pUpper the upper bound.
   */
  public void setUpper(T pUpper) {
    properties_.put("upper", pUpper.toString());
    upper_ = pUpper;
  }


  /**
   * Gets the step value for the dictionary.
   * @return the step value.
   */
  public T getStep() { return step_; }


  /**
   * Sets the step value for the dictionary.
   * @param pStep the step value.
   */
  public void setStep(T pStep) {
    properties_.put("step", pStep.toString());
    step_ = pStep;
  }

}
