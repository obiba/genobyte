/*******************************************************************************
 * Copyright 2007(c) G�nome Qu�bec. All rights reserved.
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
import org.obiba.bitwise.dao.BaseBdbDaoTestCase;
import org.obiba.bitwise.dictionary.FloatingPointDictionary;


public class FloatingPointDictionaryTest extends BaseBdbDaoTestCase {
  FloatingPointDictionary dict_;
  
  //Fixtures
  public void setUp() {
    dict_ = new FloatingPointDictionary("testDict");
  }
  
  
  //Tests begin here
  public void testDimension() {
    assertTrue(dict_.dimension() > 0);
  }
  
  
  //Lookup and reverse lookup
//  public void testLookupPositiveValue() {
//    BitVector v = dict_.lookup(1d);
//    assertEquals(11, v.count());
//    double value = dict_.reverseLookup(v);
//    assertEquals(0, Double.compare(1d, value));
//  }
  
  public void testLookupNegativeValue() {
    BitVector v = dict_.lookup(-999.9d);
    double value = dict_.reverseLookup(v);
    assertEquals(0, Double.compare(-999.9d, value));
  }
  
  
  public void testLookupMinValue() {
    BitVector v = dict_.lookup(Double.MIN_VALUE);
    double value = dict_.reverseLookup(v);
    assertEquals(0, Double.compare(Double.MIN_VALUE, value));
  }
  
  public void testLookupMaxValue() {
    BitVector v = dict_.lookup(Double.MAX_VALUE);
    double value = dict_.reverseLookup(v);
    assertEquals(Double.MAX_VALUE, value);
  }
  
  public void testLookupNaN() {
    BitVector v = dict_.lookup(Double.NaN);
    double value = dict_.reverseLookup(v);
    assertEquals(Double.NaN, value);
  }
    
  public void testLookupZero() {
    BitVector v = dict_.lookup(0.0);
    assertNotNull(v);
    Double zero = dict_.reverseLookup(v);
    assertNotNull(zero);
    assertEquals(0, Double.compare(zero, 0));
  }
}
