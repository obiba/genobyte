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

import java.io.Serializable;

/**
 * Provides field information about its context in the store. Suc information includes the field name
 * in the store and which dictionary is used to encode/decode the field values in the store.
 */
public class FieldMetaData implements Serializable {

  private static final long serialVersionUID = -2443490074082774578L;

  String name_ = null;

  String dictionary_ = null;

  boolean template_ = false;

  public FieldMetaData() {
    super();
  }

  /**
   * Gets the name of the dictionary used to encode values to <tt>BitVectors</tt> and to decode
   * those vectors back to their original value.
   * @return the name of the dictionary used by this field.
   */
  public String getDictionary() {
    return dictionary_;
  }

  /**
   * Gets the name of this field in this store context.
   * @return this field name.
   */
  public String getName() {
    return name_;
  }

  /**
   * Determines whether this field is a template or an original field.
   * @return Returns the template.
   */
  //TODO: Clarify isTemplate / setTemplate explanation in Javadoc.
  public boolean isTemplate() {
    return template_;
  }

  /**
   * Sets the dictionary that will be used for encoding to <tt>BitVectors</tt> and to decode vectors
   * back to original values.
   * @param dictionary the name of the dictionary to use for this field.
   */
  public void setDictionary(String dictionary) {
    dictionary_ = dictionary;
  }

  /**
   * Sets the name of this field.
   * @param name the name to set.
   */
  public void setName(String name) {
    name_ = name;
  }

  /**
   * Sets the template to use for this field.
   * @param template the template to set.
   */
  public void setTemplate(boolean template) {
    template_ = template;
  }

}
