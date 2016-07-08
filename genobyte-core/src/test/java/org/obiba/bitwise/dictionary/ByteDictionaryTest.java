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

public class ByteDictionaryTest extends BaseBdbDaoTestCase {
  ByteDictionary dict_;

  //Fixtures
  public void setUp() {
    dict_ = new ByteDictionary("testDict");
  }

  //Helper methods
  public void setBounds(byte lower, byte upper, byte step) {
    dict_.setLower(Byte.toString(lower));
    dict_.setUpper(Byte.toString(upper));
    dict_.setStep(Byte.toString(step));
  }

  //Tests begin here
  public void testDimension() {
    setBounds((byte) -5, (byte) 5, (byte) 1);
    assertTrue(dict_.dimension() > 0);
  }

  //Lookup and reverse lookup
  public void testLookupPositiveValue() {
    setBounds((byte) -50, (byte) 50, (byte) 2);
    BitVector v = dict_.lookup((byte) 2);
    int value = dict_.reverseLookup(v);
    assertEquals(2, value);
  }

  public void testLookupLowerBoundValue() {
    setBounds((byte) -50, (byte) 50, (byte) 2);
    BitVector v = dict_.lookup((byte) -48);
    assertEquals(1, v.count());
    int value = dict_.reverseLookup(v);
    assertEquals(-48, value);
  }

  public void testLookupUpperBoundValue() {
    setBounds((byte) -50, (byte) 50, (byte) 2);
    BitVector v = dict_.lookup((byte) 50);
    assertEquals(4, v.count());
    int value = dict_.reverseLookup(v);
    assertEquals(50, value);
  }

  public void testLookupMinValue() {
    setBounds(Byte.MIN_VALUE, Byte.MAX_VALUE, (byte) 1);
    BitVector v = dict_.lookup(Byte.MIN_VALUE);
    int value = dict_.reverseLookup(v);
    assertEquals(Byte.MIN_VALUE, value);
  }

  public void testLookupMaxValue() {
    setBounds(Byte.MIN_VALUE, Byte.MAX_VALUE, (byte) 1);
    BitVector v = dict_.lookup(Byte.MAX_VALUE);
    int value = dict_.reverseLookup(v);
    assertEquals(Byte.MAX_VALUE, value);
  }

  public void testLookupZero() {
    setBounds((byte) -10, Byte.MAX_VALUE, (byte) 5);
    BitVector v = dict_.lookup((byte) 0);
    assertNotNull(v);
    Byte zero = dict_.reverseLookup(v);
    assertNotNull(zero);
    assertEquals(0, zero.byteValue());
  }

  public void testLookupModifiedByStep() {       //Value 0 will become -1 because of step size
    setBounds((byte) -11, (byte) 10, (byte) 5);
    BitVector v = dict_.lookup((byte) 0);
    assertNotNull(v);
    Byte zero = dict_.reverseLookup(v);
    assertNotNull(zero);
    assertEquals(-1, zero.byteValue());
  }

  public void testLookupInvalid() {             //Trying to encode out-of-bound values.
    setBounds((byte) -50, (byte) 50, (byte) 2);
    assertNull(dict_.lookup((byte) -51));
    assertNull(dict_.lookup((byte) 51));
  }

}
