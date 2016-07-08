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
package org.obiba.bitwise.schema.defaultDict;

import org.obiba.bitwise.Dictionary;
import org.obiba.bitwise.schema.DictionaryMetaData;

/**
 * Provides an interface to a factory that creates new <tt>DictionaryMetaData</tt> objects (dictionary contextual information for a store),
 * to be used by fields that do not provide their own dictionary contextual data.
 */
public interface DictionaryFactory {
  /**
   * Add a new default dictionary in the library.
   * @param pDict is the new dictionary object.
   */
  public void setDictionary(Class pFieldClass, DefaultDictionary pDictClass);

  /**
   * Create a new default dictionary for the provided class.
   * @param pClass is the class for which we need a new dictionary.
   * @return An unnamed dictionary that can be used for a field with the class pClass, or
   * <code>null</code> if a dictionary couldn't be created.
   */
  public DictionaryMetaData getDictionary(Class pFieldClass);

  /**
   * Creates a new <tt>Dictionary</tt> instance that can encode/decode values that are of the type
   * given in parameter. The new instance doesn't have a name.
   * @param pFieldClass the class for which we need a new dictionary.
   * @return the new dictionary instance.
   */
  public Dictionary getInstance(Class pFieldClass, String pName);
}
