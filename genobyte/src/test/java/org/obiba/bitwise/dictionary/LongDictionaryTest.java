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
import org.obiba.bitwise.dao.BaseBdbDaoTestCase;
import org.obiba.bitwise.dictionary.LongDictionary;


public class LongDictionaryTest extends BaseBdbDaoTestCase {
  LongDictionary dict_;
  
  //Fixtures
  public void setUp() {
    dict_ = new LongDictionary("testDict");
  }
  
  
  //Tests begin here
  public void testDimension() {
    assertTrue(dict_.dimension() > 0);
  }
  
  
  //Lookup and reverse lookup
  public void testLookupPositiveValue() {
    BitVector v = dict_.lookup(1l);
    assertEquals(2, v.count());
    double value = dict_.reverseLookup(v);
    assertEquals(0, Double.compare(1, value));
  }
  
  public void testLookupNegativeValue() {
    BitVector v = dict_.lookup(-999l);
    double value = dict_.reverseLookup(v);
    assertEquals(0, Double.compare(-999l, value));
  }
  
  
  public void testLookupMinValue() {
    BitVector v = dict_.lookup(Long.MIN_VALUE);
    long value = dict_.reverseLookup(v);
    assertEquals(Long.MIN_VALUE, value);
  }
  
  public void testLookupMaxValue() {
    BitVector v = dict_.lookup(Long.MAX_VALUE);
    long value = dict_.reverseLookup(v);
    assertEquals(Long.MAX_VALUE, value);
  }
  
  public void testLookupZero() {
    BitVector v = dict_.lookup(0l);
    assertNotNull(v);
    Long zero = dict_.reverseLookup(v);
    assertNotNull(zero);
    assertEquals(zero.longValue(), 0l);
  }
}
