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
package org.obiba.bitwise.util;

import java.util.Iterator;

/**
 * Provides the seed text for the {@link Huffman} encoding algorithm. The <tt>Huffman</tt> 
 * algorithm needs to be seeded with text that closely represents the data that will
 * be encoded. This interface allows providing this text dynamically. 
 */
public interface HuffmanSeedProvider {

  /**
   * Returns an implementation of <tt>Iterator</tt> that iterates on all the 
   * seeding text. 
   */
  public Iterator<String> getSeed();

}
