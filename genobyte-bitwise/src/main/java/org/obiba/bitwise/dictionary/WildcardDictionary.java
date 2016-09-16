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

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.Dictionary;

/**
 * Defines a subtype of {@link Dictionary} that allows partial record field querying, using wildcards.
 *
 * @param <T> the type of values handled by this dictionary.
 */
public interface WildcardDictionary<T> extends Dictionary<T> {

  /**
   * Encodes bits provided by query value for a partial left side comparison.
   *
   * @param value the query term to be encoded into bits
   * @return a {@link BitVector} of encoded bits for the left side comparison.
   */
  public BitVector partialLookupLeft(T value);

  /**
   * Encodes bits provided by query value for a partial right side comparison.
   *
   * @param value the query term to be encoded into bits
   * @return a {@link BitVector} of encoded bits for the right side comparison.
   */
  public BitVector partialLookupRight(T value);

}
