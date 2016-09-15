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
package org.obiba.bitwise;

/**
 * Defines the interface for a dictionary that encode/decodes values between its original type and a <tt>BitVector</tt>.
 * @param <T> the type of values handled by this dictionary.
 */
public interface Dictionary<T> {

  /**
   * Returns the name assigned to this dictionary when it got instanciated.
   * @return A string holding the name of the dictionary.
   */
  public String getName();

  /**
   * Converts a string to the type handled by this dictionary. For example, if a dictionary is handling Integers,
   * the provided string will be transformed into an Integer that can be manipulated by this dictionary.
   * @param value is the string representing the value to be converted.
   * @return The value encoded
   * @return <b>null</b> if the string cannot be converted to a value that is encodable by this dictionary. 
   */
  public T convert(String value);

  /**
   * Encodes a value into the corresponding bit vector.
   * @param key is the value to be encoded.
   * @return A bit vector that can be used in a bitwise store for a record's field.
   */
  public BitVector lookup(T key);

  /**
   * Decodes a value that was encoded into a bit vector by this dictionary, back into its original type.
   * @param v is the bit vector holding the value to be decoded.
   * @return The value transformed into its original type.
   */
  public T reverseLookup(BitVector v);

  /**
   * Returns the maximum number of bits that will be used to hold a value.
   * @return
   */
  public int dimension();

  //TODO: Write detailed explanation
  public boolean isOrdered();

  /**
   * Determines if this dictionary encodes field values with variable length. In other words, determines values encoded
   * by this dictionary will always take the full field bit size or not.
   * For example, if a field has a size of 32 bits, a dictionary for which all encoded values use all 32 bits does not
   * encode with variable length. A dictionary that can encode a certain field value on 10 bits and another
   * value on 22 bits is a dictionary encoding with variable length.   
   * @return <b>true</b> if encoded values can be of variable bit length<BR>
   * <b>false</b> if encoded values are always using all the field's bits<BR>
   */
  public boolean isVariableLength();

  /**
   * Deserializes this dictionary's context in the store.
   * @param data the serialization data from which to extract the dictionary context.
   */
  public void setRuntimeData(byte[] data);

  /**
   * Serializes this dictionary's context in the store.
   * @return the serialized dictionary context.
   */
  public byte[] getRuntimeData();

}
