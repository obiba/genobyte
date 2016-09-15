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

import org.obiba.bitwise.dao.BaseBdbDaoTestCase;
import org.obiba.bitwise.schema.DictionaryMetaData;
import org.obiba.bitwise.util.Property;

import java.util.HashSet;
import java.util.List;

/**
 * Makes sure that default dictionary are well defined for various Java basic types (primitives, Strings, etc.) 
 */
public class DefaultDictionariesTest extends BaseBdbDaoTestCase {
  DefaultDictionaryFactory df_ = new DefaultDictionaryFactory();

  HashSet<String> boundedProperties_ = new HashSet<String>();

  HashSet<String> huffmanProperties_ = new HashSet<String>();

  public DefaultDictionariesTest() {
    boundedProperties_.add("lower");
    boundedProperties_.add("upper");
    boundedProperties_.add("step");

    huffmanProperties_.add("seedFile");
    huffmanProperties_.add("seedString");
    huffmanProperties_.add("seed");
    huffmanProperties_.add("provider");
  }

  public void checkProperties(HashSet<String> pPossibleProp, DictionaryMetaData meta) {
    //Extract properties defined by this default dictionary instance
    List<Property> pList = meta.getProperties();
    HashSet<String> providedProp = new HashSet<String>();
    for(Property p : pList) {
      providedProp.add(p.getName());
    }

    //Check that all provided properties are part of the possible properties for an instance of this dictionary.
    for(String propName : providedProp) {
      assertTrue(pPossibleProp.contains(propName));
    }
  }

  //Tests begin here
  public void testDefaultBooleanDictionary() {
    DictionaryMetaData meta = df_.getDictionary(Boolean.class);
    assertEquals("org.obiba.bitwise.dictionary.BooleanDictionary", meta.getClazz());
    assertTrue(meta.getProperties().isEmpty());    //There are no properties for this dictionary.
  }

  public void testDefaultIntegerDictionary() {
    DictionaryMetaData meta = df_.getDictionary(Integer.class);
    assertEquals("org.obiba.bitwise.dictionary.IntegerDictionary", meta.getClazz());
    checkProperties(boundedProperties_, meta);
  }

  public void testDefaultDoubleDictionary() {
    DictionaryMetaData meta = df_.getDictionary(Double.class);
    assertEquals("org.obiba.bitwise.dictionary.FloatingPointDictionary", meta.getClazz());
    checkProperties(boundedProperties_, meta);
  }

  public void testDefaultStringDictionary() {
    DictionaryMetaData meta = df_.getDictionary(String.class);
    assertEquals("org.obiba.bitwise.dictionary.HuffmanDictionary", meta.getClazz());
    checkProperties(huffmanProperties_, meta);
  }

  //Primitive types default dictionaries
  public void testDefaultPrimitiveByteDictionary() {
    DictionaryMetaData meta = df_.getDictionary(byte.class);
    assertEquals("org.obiba.bitwise.dictionary.ByteDictionary", meta.getClazz());
    checkProperties(boundedProperties_, meta);
  }

  public void testDefaultPrimitiveShortDictionary() {
    DictionaryMetaData meta = df_.getDictionary(short.class);
    assertEquals("org.obiba.bitwise.dictionary.ShortDictionary", meta.getClazz());
    checkProperties(boundedProperties_, meta);
  }

  public void testDefaultPrimitiveIntDictionary() {
    DictionaryMetaData meta = df_.getDictionary(int.class);
    assertEquals("org.obiba.bitwise.dictionary.IntegerDictionary", meta.getClazz());
    checkProperties(boundedProperties_, meta);
  }

  public void testDefaultPrimitiveLongDictionary() {
    DictionaryMetaData meta = df_.getDictionary(long.class);
    assertEquals("org.obiba.bitwise.dictionary.LongDictionary", meta.getClazz());
    assertTrue(meta.getProperties().isEmpty());    //There are no properties for this dictionary.
  }

  public void testDefaultPrimitiveFloatDictionary() {
    DictionaryMetaData meta = df_.getDictionary(float.class);
    assertEquals("org.obiba.bitwise.dictionary.FloatDictionary", meta.getClazz());
    assertTrue(meta.getProperties().isEmpty());    //There are no properties for this dictionary.
  }

  public void testDefaultPrimitiveDoubleDictionary() {
    DictionaryMetaData meta = df_.getDictionary(double.class);
    assertEquals("org.obiba.bitwise.dictionary.FloatingPointDictionary", meta.getClazz());
    assertTrue(meta.getProperties().isEmpty());    //There are no properties for this dictionary.
  }

  public void testDefaultPrimitiveBooleanDictionary() {
    DictionaryMetaData meta = df_.getDictionary(boolean.class);
    assertEquals("org.obiba.bitwise.dictionary.BooleanDictionary", meta.getClazz());
    assertTrue(meta.getProperties().isEmpty());    //There are no properties for this dictionary.
  }

  public void testDefaultPrimitiveCharDictionary() {
    DictionaryMetaData meta = df_.getDictionary(char.class);
    assertEquals("org.obiba.bitwise.dictionary.HuffmanCharacterDictionary", meta.getClazz());
    checkProperties(huffmanProperties_, meta);
  }
}
