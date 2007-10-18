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
package org.obiba.bitwise.dictionary;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.dao.BaseBdbDaoTestCase;
import org.obiba.bitwise.dictionary.BooleanDictionary;


public class BooleanDictionaryTest extends BaseBdbDaoTestCase {
  BooleanDictionary dict_;
  
  //Fixtures
  public void setUp() {
    dict_ = new BooleanDictionary("testDict");
  }
  

  //Tests begin here
  
  //Lookup and reverse lookup
  public void testLookupTrueValue() {
    BitVector v = dict_.lookup(Boolean.TRUE);
    assertEquals(1, v.count());
    Boolean value = dict_.reverseLookup(v);
    assertEquals(Boolean.TRUE, value);
  }
  
  public void testLookupFalseValue() {
    BitVector v = dict_.lookup(Boolean.FALSE);
    assertEquals(1, v.count());
    Boolean value = dict_.reverseLookup(v);
    assertEquals(Boolean.FALSE, value);
  }
  
  public void testLookupInvalid() {             //Trying to encode out-of-bound values.
    assertNull(dict_.lookup(null));
  }

}
