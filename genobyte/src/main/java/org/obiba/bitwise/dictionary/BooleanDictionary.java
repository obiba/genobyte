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
 * Provides encoding/decoding capabilities between a <tt>Boolean</tt> object and a <tt>BitVector</tt>. 
 */
public class BooleanDictionary extends AbstractStaticDictionary<Boolean> {

  /** Value representing <tt>false</tt> in a <tt>BitVector</tt>. */
  public final static byte FALSE = 0x01;
  
  /** <tt>BitVector</tt> holding the <tt>false</tt> value. */
  public final static BitVector FALSE_VECTOR = BitUtil.vectorise(FALSE, 2);
  
  /** Value representing <tt>true</tt> in a <tt>BitVector</tt>. */
  public final static byte TRUE = 0x02;
  
  /** <tt>BitVector</tt> holding the <tt>true</tt> value. */
  public final static BitVector TRUE_VECTOR = BitUtil.vectorise(TRUE, 2);

  private String name_ = null;


  public BooleanDictionary(String name) {
    super();
    name_ = name;
  }


  public String getName() {
    return name_;
  }


  public Boolean convert(String value) {
    return new Boolean(value);
  }


  public BitVector lookup(Boolean key) {
    if (key == null) {
      return null;
    }
    if(key.booleanValue()) {
      return TRUE_VECTOR;
    }
    return FALSE_VECTOR;
  }


  public Boolean reverseLookup(BitVector v) {
    if(v == null) {
      return null;
    }
    if(v.size() != 2) {
      throw new IllegalArgumentException("Invalid call vector ["+v+"]");
    }
    if(v.get(0)) {
      return Boolean.FALSE;
    } else if(v.get(1)) {
      return Boolean.TRUE;
    }
    return null;
  }


  public int dimension() {
    return 2;
  }


  public boolean isOrdered() {
    return false;
  }
  
  @Override
  public boolean equals(Object obj) {
    if(obj instanceof BooleanDictionary) return true;
    return super.equals(obj);
  }
}
