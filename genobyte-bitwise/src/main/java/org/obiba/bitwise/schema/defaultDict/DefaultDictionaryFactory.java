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

import java.util.HashMap;

/**
 * This is the default dictionary factory provided with the bitwise API. It provides default dictionary context
 * for many of the basic field types that could be found in a store, including the Java Primitives.
 */

//TODO: Implement primitive types handling
public class DefaultDictionaryFactory implements DictionaryFactory {
  HashMap<Class, DefaultDictionary> defaultDicts_;

  public DefaultDictionaryFactory() {
    super();

    defaultDicts_ = new HashMap<Class, DefaultDictionary>();

    //TODO: Load default dictionaries from properties file
    //Java primitive types
    setDictionary(byte.class, new DefaultByteDictionary());
    setDictionary(short.class, new DefaultShortDictionary());
    setDictionary(int.class, new DefaultIntegerDictionary());
    setDictionary(long.class, new DefaultLongDictionary());
    setDictionary(float.class, new DefaultFloatDictionary());
    setDictionary(double.class, new DefaultDoubleDictionary());
    setDictionary(boolean.class, new DefaultBooleanDictionary());
    setDictionary(char.class, new DefaultCharacterDictionary());

    setDictionary(Byte.class, new DefaultByteDictionary());
    setDictionary(Short.class, new DefaultShortDictionary());
    setDictionary(Integer.class, new DefaultIntegerDictionary());
    setDictionary(Float.class, new DefaultFloatDictionary());
    setDictionary(Double.class, new DefaultDoubleDictionary());
    setDictionary(Long.class, new DefaultLongDictionary());
    setDictionary(Boolean.class, new DefaultBooleanDictionary());
    setDictionary(Character.class, new DefaultCharacterDictionary());
    setDictionary(String.class, new DefaultStringDictionary());
  }

  /**
   * @see org.obiba.bitwise.schema.defaultDict.DictionaryFactory#setDefaultDictionary
   */
  public void setDictionary(Class pFieldClass, DefaultDictionary pDictClass) {
    defaultDicts_.put(pFieldClass, pDictClass);
  }

  /**
   * @see org.obiba.bitwise.schema.defaultDict.DictionaryFactory#getDefaultDictionary
   */
  public DictionaryMetaData getDictionary(Class pFieldClass) {
    DefaultDictionary dmd = defaultDicts_.get(pFieldClass);
    if (dmd != null) {
      return dmd.getDict();
    } else if (pFieldClass.isEnum()) {
      // Fixes GEN-30
      return new DefaultEnumDictionary(pFieldClass).getDict();
    }

    throw new IllegalArgumentException("No default dictionary for class " + pFieldClass.getName());
  }

  public Dictionary getInstance(Class pFieldClass, String pName) {
    DefaultDictionary dd = defaultDicts_.get(pFieldClass);
    if (dd == null) {
      if (pFieldClass.isEnum()) {
        // Fixes GEN-30
        return new DefaultEnumDictionary(pFieldClass).getInstance(pName);
      }
      throw new IllegalArgumentException("No default dictionary for class " + pFieldClass.getName());
    }

    return dd.getInstance(pName);
  }

}
