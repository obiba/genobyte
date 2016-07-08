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
import org.obiba.bitwise.util.BitUtil;

public class DecimalDictionaryTest extends BaseBdbDaoTestCase {

  public DecimalDictionaryTest() {
    super();
  }

  public DecimalDictionaryTest(String t) {
    super(t);
  }

  public void testLookup() {
    DecimalDictionary d = new DecimalDictionary("testD");
    d.setLower(-1000);
    d.setUpper(1000);
    d.setStep(0.1);
    assertTrue(d.dimension() > 0);
    System.out.println(d.dimension());
    BitVector v = d.lookup(-1000d);
    assertEquals(1, v.count());
    assertEquals(-1000d, d.reverseLookup(v));
    v = d.lookup(-999.9d);
    assertEquals(2, BitUtil.longValue(v));
    double value = d.reverseLookup(v);
    assertEquals(-999.9d, value);
  }

  public void testReverseLookup() {
    DecimalDictionary d = new DecimalDictionary("testD");
    d.setLower(-1000);
    d.setUpper(1000);
    d.setStep(0.00001);
    BitVector v = d.lookup(0.00001);
  }

  public void testZero() {
    DecimalDictionary d = new DecimalDictionary("testD");
    d.setLower(Integer.MIN_VALUE);
    d.setUpper(Integer.MAX_VALUE);
    d.setStep(0.000001d);
    BitVector v = d.lookup(0.0);
    assertNotNull(v);
    Double zero = d.reverseLookup(v);
    assertNotNull(zero);
    System.out.println(zero);
    assertEquals(0, Double.compare(zero, 0));
  }

}
