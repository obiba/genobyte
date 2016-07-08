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

import java.util.LinkedList;
import java.util.List;

import org.obiba.bitwise.BitVector;

import junit.framework.TestCase;

public class HuffmanTest extends TestCase {

  public void testHuffman() {
    List<String> seed = new LinkedList<String>();
    seed.add("rs232");
    seed.add("rs123");
    seed.add("rs456");
    seed.add("rs222");

    Huffman hm = new Huffman(seed);

    String testStrings[] = { "r", "rs", "rs2", "rs23", "rs232" };
    for(int i = 0; i < testStrings.length; i++) {
      String test = testStrings[i];
      BitVector code = hm.encode(test);
      assertNotNull(code);
      assertEquals(test, hm.decode(code));
    }
    // Test characters not part of original alphabet
    String badTestStrings[] = { "a", "ab", "rsa", "222a" };
    for(int i = 0; i < badTestStrings.length; i++) {
      String test = badTestStrings[i];
      BitVector code = hm.encode(test);
      assertNull(code);
    }
  }

  public void testZeroCode() {
    List<String> seed = new LinkedList<String>();
    seed.add("rs232");
    seed.add("rs123");
    seed.add("rs456");
    seed.add("rs222");

    Huffman hm = new Huffman(seed);
    BitVector code = hm.encode("rs2323");
    code.grow(code.size() + 100);
    assertEquals("rs2323", hm.decode(code));
  }

  public void testEndOfStringCode() {
    List<String> seed = new LinkedList<String>();
    seed.add("rs232");
    seed.add("rs123");
    seed.add("rs456");
    seed.add("rs222");

    Huffman hm = new Huffman(seed);

    //Default encoding method must add an End Of String code at the end of the vector.
    assertEquals(hm.encode(""), hm.getEndOfStringCode());

    //End Of String code must not be only zeros.
    assertTrue(hm.getEndOfStringCode().count() > 0);
  }
}
