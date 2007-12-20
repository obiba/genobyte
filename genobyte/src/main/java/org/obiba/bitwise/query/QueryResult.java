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
   * Returns the index of the record which was at <code>qrPosition</code> position in this
   * <code>QueryResult</code> instance. This is used to traverse the results in hit order (sorted).
   * <code>qrPosition</code> must be a value between 0 and <code>count()-1</code> (inclusive).
   * <br/><br/>
   * Example: A query returned 10 results in a <code>QueryResult</code> instance named <code>qr</code>.
   * Running <code>qr.hit(4)</code> would return the index of the fifth record matched by the query. 
   *
   * @param qrPosition the index of the hit to return
   * @return the hit at index <code>index</code>
   */
  public int hit(int qrPosition);

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
