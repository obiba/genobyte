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
package org.obiba.bitwise.annotation;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.obiba.bitwise.schema.DictionaryMetaData;
import org.obiba.bitwise.schema.FieldMetaData;
import org.obiba.bitwise.schema.StoreSchema;
import org.obiba.bitwise.schema.defaultDict.DefaultDictionaryFactory;
import org.obiba.bitwise.schema.defaultDict.DictionaryFactory;
import org.obiba.bitwise.util.Property;

/**
 * Using Java Annotations, builds a new <tt>StoreSchema</tt> from a user-defined class to be transformed into a store.
 */
public class AnnotationStoreSchemaBuilder {
  HashMap<Class<?>, String> defaultDicts_ = null;

  StoreSchema schema_ = null;

  DictionaryFactory dictFactory_ = null;

  /**
   * By default, we will use the default dictionary factory to build dictionaries that haven't been
   * specified with annotations.
   */
  public AnnotationStoreSchemaBuilder() {
    this(new DefaultDictionaryFactory());
  }

  /**
   * Use a dictionary factory provided by the user, extending DictionaryFactory.
   */
  public AnnotationStoreSchemaBuilder(DictionaryFactory pDictFact) {
    dictFactory_ = pDictFact;
  }

  /**
   * Creates a <tt>StoreSchema</tt> based on the annotations found in a given record class.
   * @param pClass the record class where the annotation will be found.
   * @return the <tt>StoreSchema</tt> created with the help of the Java bitwise annotations.
   */
  public StoreSchema createSchema(Class<?> pClass) {
    defaultDicts_ = new HashMap<Class<?>, String>();
    schema_ = new StoreSchema();

    BitwiseAnnotationManager ba = new BitwiseAnnotationManager(pClass);
    schema_.setVersion(ba.getRecordStoreVersion());
    schema_.setName(ba.getRecordStoreName());

    //Create dictionaries from dictionary annotations
    Map<String, DictionaryDef> declaredDicts = ba.getDictionaries();
    for(String dictName : declaredDicts.keySet()) {
      schema_.addDictionary(createDictionaryFromAnnotation(ba, declaredDicts.get(dictName)));
    }

    //Create fields in the store, from annotations
    List<PropertyDescriptor> properties = ba.getStoredDescriptors();
    Map<String, String> fieldNames = ba.getStoredFields();
    for(PropertyDescriptor property : properties) {
      FieldMetaData newField = new FieldMetaData();
      newField.setName(fieldNames.get(property.getName()));

      //Get dictionary name. If there is no dictionary defined by annotation, create a default one.
      String dictName = ba.getPropertyDictName(property.getName());
      if(dictName == null) {
        dictName = getDefaultDictionary(property.getPropertyType());
      }
      newField.setDictionary(dictName);

      schema_.addField(newField);
    }

    for(String template : ba.getTemplateFields()) {
      FieldMetaData newField = new FieldMetaData();
      newField.setName(template);
      newField.setTemplate(true);
      String dName = ba.getPropertyDictName(template);
      if(dName == null) throw new RuntimeException("no dictionary for template [" + template + "]");
      newField.setDictionary(dName);
      schema_.addField(newField);
    }

    return schema_;
  }

  /**
   * Get the name of a default dictionary for the given class.
   * First, check if a default dictionary has already been created, and create one if not.
   * @param pClass is the class of the current field for which we need a default dictionary.
   * @return The name of the default dictionary to use in this case.
   */
  private String getDefaultDictionary(Class<?> pClass) {
    //If a default dictionary has already been generated for this field type
    if(defaultDicts_.get(pClass) != null) {
      return defaultDicts_.get(pClass);
    }
    //Otherwise, create a new dictionary with default values
    else {
      DictionaryMetaData newDict = dictFactory_.getDictionary(pClass);

      //Assign a name for this dictionary
      String dictName = createDictionaryNameFromClass(pClass);
      newDict.setName(dictName);

      //Add dictionary to schema and the dictionary name in the HashMap of already existing default dictionaries.
      schema_.addDictionary(newDict);
      defaultDicts_.put(pClass, dictName);
      return dictName;
    }
  }

  /**
   * Attempt to create a new dictionary named "Default_" + className. If it already exists, append
   * an underscore at the end of the name and check if that name exists. Do this as long as the
   * automatically generated name is the duplicate of another existing dictionary.
   * @param pClass
   * @return
   */
  private String createDictionaryNameFromClass(Class<?> pClass) {
    StringBuilder autoName = new StringBuilder("Default_").append(pClass.getName());
    while(schema_.getDictionary(autoName.toString()) != null) {
      autoName.append("_");
    }
    return autoName.toString();
  }

  /**
   * Create a new dictionary from class annotations.
   * @param pDictionary is the annotation of the dictionary definition.
   * @return is the newly created dictionary to be added in the schema.
   */
  private DictionaryMetaData createDictionaryFromAnnotation(BitwiseAnnotationManager ba, DictionaryDef pDictionary) {
    DictionaryMetaData newDict = new DictionaryMetaData();
    newDict.setName(pDictionary.name());

    Class<?> dictClass = pDictionary.dictionaryClass();
    String dictClassName = pDictionary.dictionaryClassName();

    if(dictClass != void.class) {
      if(dictClassName != null && dictClassName.equals("") == false) {
        throw new InvalidAnnotationException("Class [" + ba.getRecordClass().getName() +
            "] can only have one of these attributes: dictionaryClass, dictionaryClassName.");
      }
      newDict.setClass(dictClass.getName());
    } else {
      if(dictClassName.equals("")) {
        throw new InvalidAnnotationException("Class [" + ba.getRecordClass().getName() +
            "] should have at least one of these attributes: dictionaryClass, dictionaryClassName.");
      }
      newDict.setClass(dictClassName);
    }

    //Loop on all properties
    DictionaryProperty dictProps[] = pDictionary.property();
    for(int k = 0; k < dictProps.length; k++) {
      Property currentProp = new Property();
      currentProp.setName(dictProps[k].name());
      currentProp.setValue(dictProps[k].value());
      newDict.addProperty(currentProp);
    }

    return newDict;
  }

}
