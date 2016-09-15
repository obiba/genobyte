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

import org.obiba.bitwise.util.StringUtil;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Analyzes Bitwise annotations on a given object and extracts all necessary informations.
 * In this context, a field is an information stored in the Bitwise for each record. It is approximately like a column in SQL.
 * A property is, in a bean, a combination of a field, a get method and a set method.
 */
class BitwiseAnnotationManager {
  String recordStoreName_ = null;       //Name of the BitwiseRecord as specified in the annotation

  String recordStoreVersion_ = null;    //Version of BitwiseRecord as specified in the annotation

  private Class<?> recordClass_ = null;    //The annotated class that we are analyzing

  //The "unique" field defined in the class. There can only be one!
  private PropertyDescriptor uniqueFieldDescriptor_ = null;

  //PropertyDescriptor for fields being stored in the bitwise.
  private List<PropertyDescriptor> storedDescriptors_ = new LinkedList<PropertyDescriptor>();

  //Map of names for all stored fields in the analyzed object. Key is property name and value is the name of the field in the Bitwise store.
  private Map<String, String> storedFields_ = new HashMap<String, String>();

  // List of field names that are templates
  private List<String> templateFields_ = new LinkedList<String>();

  //DictionaryDef elements defined in the BitwiseRecord annotation, organized by dictionary name
  private Map<String, DictionaryDef> dictionaries_ = new HashMap<String, DictionaryDef>();

  //Map of property-dictionary pairing organized by property name.
  private Map<String, String> propertyDictionary_ = new HashMap<String, String>();

  /**
   * Field Accessors
   */
  @SuppressWarnings("unchecked")
  public <T> Class<T> getRecordClass() {
    return (Class<T>) recordClass_;
  }

  public PropertyDescriptor getUniqueFieldDescriptor() {
    return uniqueFieldDescriptor_;
  }

  public List<PropertyDescriptor> getStoredDescriptors() {
    return storedDescriptors_;
  }

  public Map<String, String> getStoredFields() {
    return storedFields_;
  }

  public List<String> getTemplateFields() {
    return templateFields_;
  }

  public String getRecordStoreName() {
    return recordStoreName_;
  }

  public String getRecordStoreVersion() {
    return recordStoreVersion_;
  }

  public Map<String, DictionaryDef> getDictionaries() {
    return dictionaries_;

  }

  public String getPropertyDictName(String pPropName) {
    return propertyDictionary_.get(pPropName);
  }

  /**
   * @param c is a class that will be analyzed to extract the bitwise store annotation data.
   */
  BitwiseAnnotationManager(Class<?> c) {
    //Make sure there is a BitwiseRecord annotation in the provided class.
    if(c.isAnnotationPresent(BitwiseRecord.class) == false) {
      throw new InvalidAnnotationException(
          "Class [" + c.getName() + "] should be annotated with the @BitwiseRecord annotation.");
    }

    //Keep the class being analyzed for annotations
    recordClass_ = c;

    //Extract BitwiseRecord annotation
    BitwiseRecord bitwiseAnnotation = c.getAnnotation(BitwiseRecord.class);

    //Extract general information
    recordStoreVersion_ = bitwiseAnnotation.version();
    recordStoreName_ = bitwiseAnnotation.storeName();

    //Extract DictionaryDef annotations from class and superclases
    extractDictionaryDef(c);

    // Extract templates from @FieldTemplate annotations
    extractTemplates(c);

    //Extract @Stored and @NotStored annotations for fields
    PropertyDescriptor[] properties = null;
    try {
      properties = Introspector.getBeanInfo(c).getPropertyDescriptors();
    } catch(IntrospectionException e) {
      throw new InvalidAnnotationException(e.getMessage());
    }

    //Analyze each property. Discard properties having a @NotStored annotation, or if the "storeAll"
    //flag is not set to true.
    for(int i = 0; i < properties.length; i++) {
      PropertyDescriptor property = properties[i];

      //Skip the class descriptor
      if("class".equals(property.getName())) continue;

      //Get the PropertyDescriptor's field (the storing annotation might be there instead of with the get/set accessors)
      java.lang.reflect.Field currentField = extractPropertyField(c, property.getName());

      //Extract annotations for the current property's get/set methods
      Stored stored = null;
      NotStored notStored = null;
      boolean foundAnn = false;
      Annotation[] storingAnn = { currentField != null ? currentField.getAnnotation(Stored.class) : null,
          property.getReadMethod() != null ? property.getReadMethod().getAnnotation(Stored.class) : null,
          property.getWriteMethod() != null ? property.getWriteMethod().getAnnotation(Stored.class) : null,
          currentField != null ? currentField.getAnnotation(NotStored.class) : null,
          property.getReadMethod() != null ? property.getReadMethod().getAnnotation(NotStored.class) : null,
          property.getWriteMethod() != null ? property.getWriteMethod().getAnnotation(NotStored.class) : null, };

      for(int j = 0; j < storingAnn.length; j++) {
        Annotation currentAnn = storingAnn[j];

        if(currentAnn != null) {
          //Make sure there is at most one of (@Stored or @NotStored) for a descriptor and its get/set methods
          if(foundAnn == true) {
            throw new InvalidAnnotationException(
                "Class [" + c.getName() + "] declares a property [" + property.getName() +
                    "] that has more than one of these annotations (@Stored or @NotStored).");
          }

          foundAnn = true;
          if(currentAnn.annotationType().equals(Stored.class)) {
            stored = (Stored) currentAnn;
          } else {
            notStored = (NotStored) currentAnn;
          }
        }
      }

      //Skip the fields with @NotStored annotation
      if(notStored != null) {
        continue;
      }

      //Skip fields without a @Stored annotation if parameter StoreAll == false
      if(bitwiseAnnotation.storeAll() == false && stored == null) {
        continue;
      }

      //If a name was specified for the given field, use it. Otherwise, use the field name itself.
      String bitwiseField = property.getName();
      if(stored != null && StringUtil.isEmptyString(stored.field()) == false) {
        bitwiseField = stored.field();
      }
      storedFields_.put(property.getName(), bitwiseField);

      //If a dictionary was defined for the field, add the connection.
      if(stored != null && StringUtil.isEmptyString(stored.dictionary()) == false) {
        propertyDictionary_.put(property.getName(), stored.dictionary());
      }

      //Add current PropertyDescriptor to the list of stored properties.
      storedDescriptors_.add(property);

      //If a field is marked "unique", store it. There can only be one unique field.
      if(stored != null && stored.unique() == true) {
        if(uniqueFieldDescriptor_ != null) {
          throw new InvalidAnnotationException(
              "Class [" + c.getName() + "] should can only have one Stored annotation with attribute uniqe == true.");
        }
        uniqueFieldDescriptor_ = property;
      }
    }
  }

  private void extractDictionaryDef(Class<?> c) {
    BitwiseRecord bitwiseAnnotation = c.getAnnotation(BitwiseRecord.class);

    if(bitwiseAnnotation != null) {
      DictionaryDef dictAnn[] = bitwiseAnnotation.dictionary();
      for(int i = 0; i < dictAnn.length; i++) {
        String dictName = dictAnn[i].name();
        if(dictionaries_.get(dictName) != null) {
          throw new InvalidAnnotationException(
              "There are two dictionaries with name [" + dictName + "] in the class hierarchy.");
        }

        dictionaries_.put(dictName, dictAnn[i]);
      }
    }

    Class<?> sc = c.getSuperclass();
    if(sc != null) {
      extractDictionaryDef(sc);
    }
  }

  /**
   * Processes class <tt>c</tt> to extract template fields from {@link FieldTemplate} annotation
   * @param c the class to process
   */
  private void extractTemplates(Class<?> c) {
    BitwiseRecord bitwiseAnnotation = c.getAnnotation(BitwiseRecord.class);

    if(bitwiseAnnotation != null) {
      FieldTemplate[] templates = bitwiseAnnotation.templates();
      for(int i = 0; i < templates.length; i++) {
        String name = templates[i].prefix();
        String dictName = templates[i].dictionary();
        this.templateFields_.add(name);
        this.propertyDictionary_.put(name, dictName);
      }
    }

    Class<?> sc = c.getSuperclass();
    if(sc != null) {
      extractTemplates(sc);
    }
  }

  /**
   * Looks recursively in a class and all of its super classes for a field with a given name.
   * @param c is the class from where we will start looking.
   * @param pName is the name of the field to look for.
   * @return The field with the name given in parameter.
   */
  private java.lang.reflect.Field extractPropertyField(Class<?> c, String pName) {
    try {
      return c.getDeclaredField(pName);
    } catch(NoSuchFieldException e) {
      Class<?> sc = c.getSuperclass();
      if(sc != null) {
        return extractPropertyField(sc, pName);
      } else {
        return null;
      }
    }
  }
}
