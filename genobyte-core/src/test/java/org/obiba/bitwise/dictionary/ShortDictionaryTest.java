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

public class ShortDictionaryTest extends BaseBdbDaoTestCase {
  ShortDictionary dict_;

  //Helper methods
  public void setBounds(short lower, short upper, short step) {
    dict_ = new ShortDictionary("testDict");
    dict_.setLower(Short.toString(lower));
    dict_.setUpper(Short.toString(upper));
    dict_.setStep(Short.toString(step));
  }

  //Tests begin here
  public void testDimension() {
    setBounds((short) -5, (short) 5, (short) 1);
    assertTrue(dict_.dimension() > 0);
  }

  //Lookup and reverse lookup
  public void testLookupPositiveValue() {
    setBounds((short) -50, (short) 50, (short) 2);
    BitVector v = dict_.lookup((short) 2);
    int value = dict_.reverseLookup(v);
    assertEquals(2, value);
  }

  public void testLookupLowerBoundValue() {
    setBounds((short) -50, (short) 50, (short) 2);
    BitVector v = dict_.lookup((short) -48);
    assertEquals(1, v.count());
    int value = dict_.reverseLookup(v);
    assertEquals(-48, value);
  }

  public void testLookupUpperBoundValue() {
    setBounds((short) -50, (short) 50, (short) 2);
    BitVector v = dict_.lookup((short) 50);
    assertEquals(4, v.count());
    int value = dict_.reverseLookup(v);
    assertEquals(50, value);
  }

  public void testLookupMinValue() {
    setBounds(Short.MIN_VALUE, Short.MAX_VALUE, (short) 1);
    BitVector v = dict_.lookup(Short.MIN_VALUE);
    int value = dict_.reverseLookup(v);
    assertEquals(Short.MIN_VALUE, value);
  }

  public void testLookupMaxValue() {
    setBounds(Short.MIN_VALUE, Short.MAX_VALUE, (short) 1);
    BitVector v = dict_.lookup(Short.MAX_VALUE);
    int value = dict_.reverseLookup(v);
    assertEquals(Short.MAX_VALUE, value);
  }

  public void testLookupZero() {
    setBounds((short) -10, Short.MAX_VALUE, (short) 5);
    BitVector v = dict_.lookup((short) 0);
    assertNotNull(v);
    Short zero = dict_.reverseLookup(v);
    assertNotNull(zero);
    assertEquals(0, zero.shortValue());
  }

  public void testLookupModifiedByStep() {       //Value 0 will become -1 because of step size
    setBounds((short) -11, (short) 10, (short) 5);
    BitVector v = dict_.lookup((short) 0);
    assertNotNull(v);
    Short zero = dict_.reverseLookup(v);
    assertNotNull(zero);
    assertEquals(-1, zero.shortValue());
  }

  public void testLookupInvalid() {             //Trying to encode out-of-bound values.
    setBounds((short) -50, (short) 50, (short) 2);
    assertNull(dict_.lookup((short) -51));
    assertNull(dict_.lookup((short) 51));
  }

}
