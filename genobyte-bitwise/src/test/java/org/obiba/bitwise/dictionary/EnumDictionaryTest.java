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

import junit.framework.TestCase;
import org.obiba.bitwise.BitVector;

public class EnumDictionaryTest extends TestCase {

  public EnumDictionaryTest() {
    super();
  }

  public EnumDictionaryTest(String arg0) {
    super(arg0);
  }

  public enum TestEnum {
    TEST_1, TEST_2, TEST_3;
  }

  public class TestEnumDictionary extends EnumDictionary<TestEnum> {
    public TestEnumDictionary(String name) {
      super(name, TestEnum.class);
    }
  }

  public void testTestEnum() {
    TestEnumDictionary d = new TestEnumDictionary("testDict");
    assertEquals(2, d.dimension());

    BitVector v = d.lookup(TestEnum.TEST_1);
    assertNotNull(v);

    assertEquals(TestEnum.TEST_1, d.reverseLookup(v));
  }

  public void testToString() {
    EnumDictionary e = new EnumDictionary("testing");
    assertNotNull(e.toString());
  }
}
