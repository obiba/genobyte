/*******************************************************************************
 * Copyright 2007(c) Génome Québec. All rights reserved.
 * 
 * This file is part of GenoByte.
 * 
 * GenoByte is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * GenoByte is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 *******************************************************************************/
package org.obiba.bitwise.util;

import java.io.Serializable;

/**
 * Bean used to hold a <tt>Dictionary</tt> instance property.
 */
public class Property implements Serializable {

  private static final long serialVersionUID = -6512136256076962038L;

  private String name_ = null;
  private String value_ = null;
  
  public Property() {
    super();
  }
  
  public Property(String name, String value) {
    super();
    name_ = name;
    value_ = value;
  }

  /**
   * Gets the name of the property.
   * @return the name.
   */
  public String getName() {
    return name_;
  }

  /**
   * Gets the property value.
   * @return the value.
   */
  public String getValue() {
    return value_;
  }

  /**
   * Sets the name of the property.
   * @param name the name to set.
   */
  public void setName(String name) {
    name_ = name;
  }

  /**
   * Sets the value of the property.
   * @param value the value to set.
   */
  public void setValue(String value) {
    value_ = value;
  }

}
