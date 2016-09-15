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
package org.obiba.bitwise.dao.impl.util;

import junit.framework.TestCase;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class BdbUtilTest extends TestCase {

  public void testAllocate() {
    ByteBuffer bb = BitPackingUtil.allocate(1024);
    assertEquals(1024, bb.capacity());
  }

  public void testAllocateZero() {
    ByteBuffer bb = BitPackingUtil.allocate(0);
    assertEquals(0, bb.capacity());
  }

  public void testAllocateIllegal() {
    try {
      ByteBuffer bb = BitPackingUtil.allocate(-1);
      assertFalse("Expected exception not thrown.", true);
    } catch(RuntimeException e) {
    }
  }

  public void testWriteString() {
    ByteBuffer bb = BitPackingUtil.allocate(1024);
    String testString = "testString";
    BitPackingUtil.putString(testString, bb);
    bb.rewind();
    String read = BitPackingUtil.readString(bb);
    assertEquals(testString, read);
  }

  public void testIntArray() {
    ByteBuffer bb = BitPackingUtil.allocate(1024);

    int[] testValues = new int[10];
    for(int i = 0; i < testValues.length; i++) {
      testValues[i] = i;
    }
    BitPackingUtil.putIntArray(testValues, bb);
    // Should have stored one int for the size of the array and testValues.length ints (4 bytes each)
    assertEquals(4 + testValues.length * 4, bb.position());

    bb.rewind();
    int[] read = BitPackingUtil.readIntArray(bb);
    assertTrue(Arrays.equals(testValues, read));
  }

  public void testLongArray() {
    ByteBuffer bb = BitPackingUtil.allocate(1024);

    long[] testValues = new long[10];
    for(int i = 0; i < testValues.length; i++) {
      testValues[i] = i;
    }
    BitPackingUtil.putLongArray(testValues, bb);
    // Should have stored one int for the size of the array and testValues.length longs (8 bytes each)
    assertEquals(4 + testValues.length * 8, bb.position());

    bb.rewind();
    long[] read = BitPackingUtil.readLongArray(bb);
    assertTrue(Arrays.equals(testValues, read));
  }
}
