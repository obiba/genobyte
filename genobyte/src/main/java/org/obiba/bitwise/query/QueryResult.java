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
package org.obiba.bitwise.query;

import org.obiba.bitwise.BitVector;

/**
 * Holds the result of a <code>Query</code>.
 * 
 * @author plaflamm
 */
public interface QueryResult {

  /**
   * Returns the bit representation of the results.
   * 
   * @return a <link>BitVector</link> containing the results.
   */
  public BitVector bits();
  
  /**
   * Get the filter vector.
   * @return A BitVector of the filter.
   */
  public BitVector getFilter();
  

  /**
   * Creates a copy of this <code>QueryResult</code> and returns it.
   * 
   * @return a copy of this <code>QueryResult</code>
   */
  public QueryResult copy();

  /**
   * Returns the index of the next hit starting from <code>index</code> (inclusive). The method
   * returns -1 if there are no more set bits. This method is used to traverse the hits in record index order.
   *
   * @param index the index of the first hit to test
   * @return the index of the next hit or -1 if none are found

   * @see org.obiba.bitwise.BitVector.nextSetBit()
   */
  public int next(int index);

  /**
   * Returns the hit at index <code>index</code>. This is used to traverse the results in hit order (sorted).
   * The index must be between 0 and <code>count()</code> (exclusive).
   *
   * @param index the index of the hit to return
   * @return the hit at index <code>index</code>
   */
  public int hit(int index);

  /**
   * Returns true if the index is a hit
   * 
   * @param index the index to test
   * @return true if <code>index</code> is a hit
   */
  public boolean get(int index);

  /**
   * Returns the number of hits
   * 
   * @return the number of hits
   */
  public int count();

  /**
   * Reverses the hits.
   * 
   * @return <code>this</code>
   */
  public QueryResult not();

  /**
   * Applies the AND operator to <code>this</code> and returns <code>this</code>.
   * 
   * @param r the vector to operate with
   * @return <code>this</code>
   */
  public QueryResult and(QueryResult r);

  /**
   * Applies the AND NOT operator to <code>this</code> and returns <code>this</code>.
   * 
   * @param r the vector to operate with
   * @return <code>this</code>
   */
  public QueryResult andNot(QueryResult r);

  /**
   * Applies the OR operator to <code>this</code> and returns <code>this</code>.
   * 
   * @param r the vector to operate with
   * @return <code>this</code>
   */
  public QueryResult or(QueryResult r);

  /**
   * Applies the XOR operator to <code>this</code> and returns <code>this</code>.
   * 
   * @param r the vector to operate with
   * @return <code>this</code>
   */
  public QueryResult xor(QueryResult r);

}
