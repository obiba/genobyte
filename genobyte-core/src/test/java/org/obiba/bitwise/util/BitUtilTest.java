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
package org.obiba.bitwise.util;

import junit.framework.TestCase;

public class BitUtilTest extends TestCase {

  public BitUtilTest() {
    super();
  }

  public BitUtilTest(String arg0) {
    super(arg0);
  }

  public void testBounds() {
    int i = 0;
    assertEquals(0, BitUtil.dimension(i));
    assertEquals(0, BitUtil.dimension((long) i));

    i = 1;
    assertEquals(1, BitUtil.dimension(i));
    assertEquals(1, BitUtil.dimension((long) i));

    i = Integer.MAX_VALUE;
    assertEquals(31, BitUtil.dimension(i));
    assertEquals(31, BitUtil.dimension((long) i));

    long l = Long.MAX_VALUE;
    assertEquals(63, BitUtil.dimension(l));
  }

}
