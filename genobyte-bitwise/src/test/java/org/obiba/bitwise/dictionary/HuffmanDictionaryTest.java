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

public class HuffmanDictionaryTest extends TestCase {

  public void testSeed() {
    HuffmanDictionary d = new HuffmanDictionary("Bogus");
    d.setSeedFile("./src/test/java/org/obiba/bitwise/rsnames.txt");
  }

  public void testLookup() {
    HuffmanDictionary d = new HuffmanDictionary("Bogus");
    d.setSeedFile("./src/test/java/org/obiba/bitwise/rsnames.txt");
    assertNotNull(d.lookup("rs232"));
  }

//  public void testLookupPerformance() {
//    HuffmanDictionary d = new HuffmanDictionary("Bogus");
//    d.setSeedFile("./src/test/java/org/obiba/bitwise/rsnames.txt");
//    final int count = 1000000;
//    long total = 0;
//    for(int i =0; i < count;i++) {
//      long start = System.currentTimeMillis();
//      d.lookup("rs232");
//      long end = System.currentTimeMillis();
//      total += end - start;
//    }
//    
//    //Performance problem...
//    assertTrue(total < 2000);
//    
//    //System.out.println("Total runtime: " + total);    
//    //System.out.println("Average lookup time: " + (total / (double)count));
//  }

  public void testBundleIdDictionary() {
    HuffmanDictionary d = new HuffmanDictionary("Bogus");
    d.setSeedString("0123456789_ABCDEFGH");
    String[] values = { "1536392204_A", "1536392323_A", "1536392332_B" };
    for(String value : values) {
      d.lookup(value);
      System.out.println(value + ": " + d.dimension());
    }
  }
}
