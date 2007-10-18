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
package org.obiba.bitwise;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.util.BitUtil;

import junit.framework.TestCase;

public class BitVectorTest extends TestCase {

  public BitVectorTest() {
    super();
  }

  public BitVectorTest(String test) {
    super(test);
  }

  public void testToValue() {
    BitVector v = new BitVector(63);
    assertEquals(0, v.longValue());
    v.set(0);
    assertEquals(1, v.longValue());

    v = new BitVector(63);
    v.set(8);
    assertEquals((1<<8), v.longValue());

    v.set(0);
    assertEquals((1<<8) + 1, v.longValue());

    v = new BitVector(63);
    for (int i = 0; i < 63; i++) {
      v.set(i);
    }
    long start = System.currentTimeMillis();
    long value = v.longValue();
    for(int i = 0; i < 1000000; i++) {
      v.longValue();
    }
    long end = System.currentTimeMillis();
    assertEquals(Long.MAX_VALUE, value);
    System.out.println("BitVector.toValue(): " + (end - start));
    start = System.currentTimeMillis();
    value = BitUtil.longValue(v);
    for(int i = 0; i < 1000000; i++) {
      BitUtil.longValue(v);
    }
    end = System.currentTimeMillis();
    assertEquals(Long.MAX_VALUE, value);
    System.out.println("BitUtil.toValue():   " + (end - start));
  }
  
  public void testNextSetBit() {
    BitVector v = new BitVector(1003);
    v.set(1001);
    assertEquals(1001, v.nextSetBit(0));
    assertEquals(1001, v.nextSetBit(1001));
    assertEquals(-1, v.nextSetBit(1002));

    v.set(1002);
    assertEquals(1001, v.nextSetBit(0));
    assertEquals(1001, v.nextSetBit(1001));
    assertEquals(1002, v.nextSetBit(1002));
    assertEquals(-1, v.nextSetBit(1003));

    for (int i = v.nextSetBit(0); i >= 0; i = v.nextSetBit(i + 1)) {
      assertTrue("index should be 1001 or 1002 but was " + i, i == 1001 || i == 1002);
    }

    // Test on byte boundary
    v = new BitVector(10);
    v.set(7);
    v.set(8);
    assertEquals(7, v.nextSetBit(7));
    assertEquals(8, v.nextSetBit(8));
    assertEquals(-1, v.nextSetBit(9));

    // Test on byte boundary
    v = new BitVector(10);
    v.set(8);
    assertEquals(8, v.nextSetBit(7));
  }
  
  public void testNextClearBit() {
    BitVector v = new BitVector(200);
    v.set(0);
    v.set(2);
    
    assertEquals(1, v.nextClearBit(0));
    assertEquals(1, v.nextClearBit(1));
    assertEquals(3, v.nextClearBit(2));
    v.setAll();
    assertEquals(-1, v.nextClearBit(0));
    assertEquals(-1, v.nextClearBit(199));
    assertEquals(-1, v.nextClearBit(200));

    v.clear(99);
    assertEquals(99, v.nextClearBit(0));
  }
  
  public void testNot() {
    final int size = 10;
    final int set = 5;
    BitVector v = new BitVector(size);
    v.set(set);
    assertEquals(1, v.count());
    v.not();
    assertEquals(size - 1, v.count());
    for(int i = 0; i < size; i++) {
      if(i != set) {
        assertTrue(v.get(i));
      } else {
        assertFalse(v.get(i));
      }
    }
  }

  public void testCount() {
    final int size = 100000;
    long time = 0;
    BitVector v = new BitVector(size);
    for(int i = 0; i < size; i+=2) {
      v.set(i);
      long start = System.currentTimeMillis();
      v.count();
      time += System.currentTimeMillis() - start;
    }
    assertEquals(size / 2, v.count());
    System.out.println("Count time : " + time);
    v.setAll();
    v.clear(0);
    assertEquals(size-1, v.count());
  }

  public void testOr() {
    final int size = 100000;
    BitVector v1 = new BitVector(size);
    BitVector v2 = new BitVector(size);
    for(int i = 0; i < size; i+=2) {
      v1.set(i);
      if(i+1 < size) {
        v2.set(i+1);
      }
    }
    BitVector v3 = new BitVector(v1);
    v3.or(v1);
    assertEquals(v1, v3);
    v3.or(v2);
    assertEquals((new BitVector(size)).setAll(), v3);
  }

  public void testXor() {
    final int size = 100000;
    BitVector v1 = new BitVector(size);
    BitVector v2 = new BitVector(size);
    for(int i = 0; i < size; i+=2) {
      v1.set(i);
      if(i+1 < size) {
        v2.set(i+1);
      }
    }
    BitVector v3 = new BitVector(v1);
    v3.xor(v2);
    assertEquals((new BitVector(size)).setAll(), v3);
    v3 = new BitVector(v1);
    v3.xor(v1);
    assertEquals(new BitVector(size), v3);
  }

  public void testAnd() {
    final int size = 100000;
    BitVector v1 = new BitVector(size);
    BitVector v2 = new BitVector(size);
    for(int i = 0; i < size; i+=2) {
      v1.set(i);
      if(i+1 < size) {
        v2.set(i+1);
      }
    }
    BitVector v3 = new BitVector(v1);
    v3.and(v1);
    assertEquals(v1, v3);
    v3.and(v2);
    assertEquals(new BitVector(size), v3);
  }

  public void testAndNot() {
    final int size = 100000;
    BitVector v1 = new BitVector(size);
    BitVector v2 = new BitVector(size);
    for(int i = 0; i < size; i+=2) {
      v1.set(i);
      if(i+1 < size) {
        v2.set(i+1);
      }
    }
    BitVector v3 = new BitVector(v1);
    v3.andNot(v2);
    assertEquals(v1, v3);
    v3.andNot(v1);
    assertEquals(new BitVector(size), v3);
  }
  
  public void testGrow() {
    final int size = 100000;
    final int newSize = 100001;
    BitVector v1 = new BitVector(size);
    v1.set(0);
    v1.set(size-1);
    v1.grow(newSize);
    v1.set(size);
    
    assertEquals(newSize, v1.size());
    assertTrue(v1.get(0));
    assertTrue(v1.get(size));
    assertTrue(v1.get(size-1));
  }
  
  public void testCompareTo() {
    BitVector v1 = BitUtil.vectorise(5897, 32);
    BitVector v2 = BitUtil.vectorise(5898, 32);
    BitVector v3 = BitUtil.vectorise(5899, 32);

    assertTrue(v1.compareTo(v1) == 0);
    assertTrue(v1.compareTo(v2) < 0);
    assertTrue(v1.compareTo(v3) < 0);

    assertTrue(v2.compareTo(v1) > 0);
    assertTrue(v2.compareTo(v2) == 0);
    assertTrue(v2.compareTo(v3) < 0);
    
    assertTrue(v3.compareTo(v1) > 0);
    assertTrue(v3.compareTo(v2) > 0);
    assertTrue(v3.compareTo(v3) == 0);

    v1 = new BitVector(10000);
    v1.set(5896);
    v1.set(5897);
    
    v2 = new BitVector(10000);
    v2.set(5895);
    v2.set(5898);
    
    v3 = new BitVector(10000);
    v3.set(5894);
    v3.set(5899);

    assertTrue(v1.compareTo(v1) == 0);
    assertTrue(v1.compareTo(v2) < 0);
    assertTrue(v1.compareTo(v3) < 0);

    assertTrue(v2.compareTo(v1) > 0);
    assertTrue(v2.compareTo(v2) == 0);
    assertTrue(v2.compareTo(v3) < 0);
    
    assertTrue(v3.compareTo(v1) > 0);
    assertTrue(v3.compareTo(v2) > 0);
    assertTrue(v3.compareTo(v3) == 0);
  }
}
