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
 * Base class used for <tt>Enum</tt> dictionaries.
 */
public class EnumDictionary<E extends Enum<E>> extends AbstractStaticDictionary<E> {

  private String name_ = null;
  private Class<E> enumClass_ = null;
  private int dimension_ = -1;
  private E[] enums_ = null;
  private BitVector[] vectors_ = null;

  /** Feature disabled since 0 is a reserved value in the bitwise store */
  private boolean allowOrdinalZero_ = false;

  public EnumDictionary(String name) {
    this.name_ = name;
  }
  
  protected EnumDictionary(String name, Class<E> enumClass) {
    this(name, enumClass, false);
  }

  private EnumDictionary(String name, Class<E> enumClass, boolean useOrdinalZero) {
    super();
    name_ = name;
    enumClass_ = enumClass;
    allowOrdinalZero_ = useOrdinalZero;
    preprocess();
  }

  public void setEnumClassName(String className) {
    try {
      Class c = Class.forName(className);
      if(c.isEnum() == false) {
        throw new IllegalArgumentException();
      }
      enumClass_ = c;
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    preprocess();
  }


  public String getName() {
    return name_;
  }


  public E convert(String value) {
    try {
      return Enum.valueOf(enumClass_, value);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }


  public BitVector lookup(E e) {
    if(e == null) {
      return null;
    }
    if(e.getClass().equals(enumClass_) == false) {
      throw new IllegalArgumentException("Enum " + e.getClass() + " incompatible with EnumDictionary<"+enumClass_+">");
    }
    return vectors_[e.ordinal()]; 
  }


  public E reverseLookup(BitVector v) {
    if(v == null) {
      return null;
    }
    int value = allowOrdinalZero_ ? (int)BitUtil.longValue(v) : (int)BitUtil.longValue(v) -1;
    return enums_[value];
  }


  public int dimension() {
    return dimension_;
  }


  public boolean isOrdered() {
    return true;
  }

  @Override
  public boolean equals(Object obj) {
    if(obj instanceof EnumDictionary) {
      return this.enumClass_.equals(((EnumDictionary<?>)obj).enumClass_);
    }
    return super.equals(obj);
  }

  @Override
  public String toString() {
    return "EnumDictionary<"+ (enumClass_ == null ? "NULL" : enumClass_.getName())+">";
  }

  /**
   * Processes the <tt>Enum</tt> class and prepares the Dictionary's internal bitwise representation.
   */
  private void preprocess() {
    enums_ = enumClass_.getEnumConstants();
    vectors_ = new BitVector[enums_.length];

    int size = allowOrdinalZero_ ? enums_.length : enums_.length + 1;

    dimension_ = (int)Math.ceil(Math.log(size)/Math.log(2.0d));
    for (int i = 0; i < enums_.length; i++) {
      E e = enums_[i];
      int ordinal = allowOrdinalZero_ ? e.ordinal() : e.ordinal() + 1;
      vectors_[i] = BitUtil.vectorise(ordinal, dimension_);
    }
  }

}
