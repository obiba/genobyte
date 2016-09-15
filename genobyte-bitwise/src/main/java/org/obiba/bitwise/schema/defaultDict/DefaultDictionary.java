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
import org.obiba.bitwise.DictionaryInstantiator;
import org.obiba.bitwise.schema.DictionaryMetaData;
import org.obiba.bitwise.util.Property;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A <tt>DefaultDictionary</tt> object holds a dictionary class name and default parameters for it, to be used as default
 * when a field is to be stored in the bitwise, but that the API user didn't provide a dictionary context as to how to encode its values. 
 */
public class DefaultDictionary {
  /** The name of the <tt>Dictionary</tt> implementation class that should be used for the default dictionary. */
  protected String className_ = null;

  /** The properties to give to this dictionary */
  protected Map<String, String> properties_ = null;

  public DefaultDictionary() {
    properties_ = new HashMap<String, String>();
  }

  /**
   * Creates a new dictionary that can be assigned to a field in a store schema.
   * @return A new DictionaryMetaData.
   */
  public DictionaryMetaData getDict() {
    DictionaryMetaData dict_ = new DictionaryMetaData();
    dict_.setClass(className_);

    for(String propName : properties_.keySet()) {
      dict_.addProperty(createProperty(propName, properties_.get(propName)));
    }
    return dict_;
  }

  public Dictionary getInstance(String pName) {
    Dictionary d = DictionaryInstantiator.createInstance(pName, className_);
    List<Property> lp = new LinkedList<Property>();
    for(String p : properties_.keySet()) {
      lp.add(new Property(p, properties_.get(p)));
    }
    DictionaryInstantiator.setProperties(d, lp);

    return d;
  }

  /**
   * Creates a Property object, ready to be added in a DictionaryMetaData object.
   * @param pName is the name of the property.
   * @param pValue is the value of the property.
   * @return The property object.
   */
  protected Property createProperty(String pName, String pValue) {
    Property prop = new Property();
    prop.setName(pName);
    prop.setValue(pValue);
    return prop;
  }

  /**
   * Gets the name of the <tt>Dictionary</tt> class that will implement this default dictionary.
   * @return the default dictionary class name.
   */
  public String getDictClassName() { return className_; }

  /**
   * Sets the name of the <tt>Dictionary</tt> class that will implement this default dictionary.
   * @param pDictClassName the default dictionary class name.
   */
  public void setDictClassName(String pDictClassName) { className_ = pDictClassName; }

  /**
   * Gets the value for a property with the specified name.
   * @param pName the name of the property.
   * @return the property value.
   */
  public String getProperty(String pName) { return properties_.get(pName); }

  /**
   * Sets a property for the default dictionary.
   * @param pName the name of the property.
   * @param pValue the value of the property.
   */
  public void setDictType(String pName, String pValue) { properties_.put(pName, pValue); }
}
