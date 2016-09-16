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
package org.obiba.bitwise.schema;

import org.obiba.bitwise.util.Property;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides information about a dictionary in a bitwise store context. It describes the way a given dictionary object
 * implementing the <tt>Dictionary</tt> interface will be used to encode values for certain fields by listing properties
 * specific to this dictionary in the store context. Then, <tt>Field</tt> objects can reference it by its instance name.
 */
public class DictionaryMetaData implements Serializable {

  private static final long serialVersionUID = -6951813022817129929L;

  private String name_ = null;

  private String class_ = null;

  private List<Property> properties_ = new LinkedList<Property>();

  public DictionaryMetaData() {
    super();
  }

  /**
   * Gets the dictionary class that will be used to encode/decode between original values and <tt>BitVectors</tt>.
   *
   * @return the dictionary class.
   */
  public String getClazz() {
    return class_;
  }

  /**
   * Gets the name given to this <tt>DictionaryMetaData</tt> instance.
   *
   * @return the name of this instance.
   */
  public String getName() {
    return name_;
  }

  /**
   * Sets the dictionary class that will be used to encode/decode between original values and <tt>BitVectors</tt>.
   *
   * @param class1 the dictionary class.
   */
  public void setClass(String class1) {
    class_ = class1;
  }

  /**
   * Sets the name given to this <tt>DictionaryMetaData</tt> instance.
   *
   * @param name the name to set.
   */
  public void setName(String name) {
    name_ = name;
  }

  /**
   * Adds a property to this instance of a <tt>Dictionary</tt>. A property is always a name/value couple giving precision
   * about the way this dictionary will be used in a context.
   *
   * @param prop the property to add to the dictionary.
   */
  public void addProperty(Property prop) {
    properties_.add(prop);
  }

  /**
   * Gets the list of properties used for this dictionary in this context.
   *
   * @return the list of properties.
   */
  public List<Property> getProperties() {
    return properties_;
  }

  /**
   * Returns a string giving information about this dictionary used in this context, with its name and set of properties.
   *
   * @return A String with the information relevant to this <tt>DictionaryMetaData</tt>.
   */
  @Override
  public String toString() {
    StringBuffer b = new StringBuffer();
    b.append("Name=[").append(name_).append("] Class=[").append(class_).append("] Properties=[").append(properties_);
    return b.toString();
  }

}
