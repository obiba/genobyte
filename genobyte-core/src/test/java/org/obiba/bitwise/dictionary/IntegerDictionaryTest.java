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
import org.obiba.bitwise.dao.BaseBdbDaoTestCase;

public class IntegerDictionaryTest extends BaseBdbDaoTestCase {
  IntegerDictionary dict_;

  //Fixtures
  public void setUp() {
    dict_ = new IntegerDictionary("testDict");
  }

  //Helper methods
  public void setBounds(int lower, int upper, int step) {
    dict_.setLower(Integer.toString(lower));
    dict_.setUpper(Integer.toString(upper));
    dict_.setStep(Integer.toString(step));
  }

  //Tests begin here
  public void testDimension() {
    setBounds(-1000, 1000, 1);
    assertTrue(dict_.dimension() > 0);
  }

  //Lookup and reverse lookup
  public void testLookupPositiveValue() {
    setBounds(-50, 50, 2);
    BitVector v = dict_.lookup(2);
    int value = dict_.reverseLookup(v);
    assertEquals(2, value);
  }

  public void testLookupLowerBoundValue() {
    setBounds(-50, 50, 2);
    BitVector v = dict_.lookup(-48);
    assertEquals(1, v.count());
    int value = dict_.reverseLookup(v);
    assertEquals(-48, value);
  }

  public void testLookupUpperBoundValue() {
    setBounds(-50, 50, 2);
    BitVector v = dict_.lookup(50);
    assertEquals(4, v.count());
    int value = dict_.reverseLookup(v);
    assertEquals(50, value);
  }

  public void testLookupMinValue() {
    setBounds(Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
    BitVector v = dict_.lookup(Integer.MIN_VALUE);
    int value = dict_.reverseLookup(v);
    assertEquals(Integer.MIN_VALUE, value);
  }

  public void testLookupMaxValue() {
    setBounds(Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
    BitVector v = dict_.lookup(Integer.MAX_VALUE);
    int value = dict_.reverseLookup(v);
    assertEquals(Integer.MAX_VALUE, value);
  }

  public void testLookupZero() {
    setBounds(-10, Integer.MAX_VALUE, 5);
    BitVector v = dict_.lookup(0);
    assertNotNull(v);
    Integer zero = dict_.reverseLookup(v);
    assertNotNull(zero);
    assertEquals(0, zero.intValue());
  }

  public void testLookupModifiedByStep() {       //Value 0 will become -1 because of step size
    setBounds(-11, 10, 5);
    BitVector v = dict_.lookup(0);
    assertNotNull(v);
    Integer zero = dict_.reverseLookup(v);
    assertNotNull(zero);
    assertEquals(-1, zero.intValue());
  }

  public void testLookupInvalid() {             //Trying to encode out-of-bound values.
    setBounds(-50, 50, 2);
    assertNull(dict_.lookup(-51));
    assertNull(dict_.lookup(51));
  }

}
