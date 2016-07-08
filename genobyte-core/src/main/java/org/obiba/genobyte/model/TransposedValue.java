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
package org.obiba.genobyte.model;

/**
 * Bean whose purpose is to transfer data from one bitwise store to the other in a <tt>GenotypingStore</tt>.
 * @param <K> the transposed record key type.
 * @param <V> the type of the value to be transposed.
 */
public class TransposedValue<K, V> {

  private K transposedKey_ = null;

  private V value_ = null;

  public TransposedValue() {
    super();
  }

  public TransposedValue(K key, V value) {
    super();
    transposedKey_ = key;
    value_ = value;
  }

  /**
   * @return the call
   */
  public V getValue() {
    return value_;
  }

  /**
   * @return the transposedKey
   */
  public K getTransposedKey() {
    return transposedKey_;
  }

  /**
   * @param call the call to set
   */
  public void setValue(V value) {
    value_ = value;
  }

  /**
   * @param transposedKey the transposedKey to set
   */
  public void setTransposedKey(K transposedKey) {
    transposedKey_ = transposedKey;
  }

}
