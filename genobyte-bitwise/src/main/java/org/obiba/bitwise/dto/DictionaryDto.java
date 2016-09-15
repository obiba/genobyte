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
package org.obiba.bitwise.dto;

import org.obiba.bitwise.util.Property;

import java.util.LinkedList;
import java.util.List;

/**
 * Transfer object for <tt>Dictionary</tt> implementations.
 *
 * Some <tt>Dictionary</tt> implementations may require persisting data 
 * upon reading and writing the BitwiseStore they are associated with.
 * This transfer object is used by DictionaryDao implementations. 
 */
public class DictionaryDto {

  /** The unique name of the dictionary	 */
  private String name_ = null;

  /** The implementing class */
  private String class_ = null;

  /** List of Property objects used to configure this instance */
  private List<Property> properties_ = new LinkedList<Property>();

  /** Storage for "runtime" data: collected during execution */
  private byte[] runtimeData_ = null;

  /**
   * Constructs a <tt>DictionaryDto</tt> instance 
   *
   * @param name the name of the dictionary
   * @param clazz the implementation of the <tt>Dictionary</tt> interface
   * @param properties a <tt>List</tt> of <tt>Property</tt> objects used to initialize/configure the dictionary instance
   */
  public DictionaryDto(String name, String clazz, List<Property> properties) {
    this(name, clazz, properties, null);
  }

  /**
   * Constructs a <tt>DictionaryDto</tt> instance 
   *
   * @param name the name of the dictionary
   * @param clazz the implementation of the <tt>Dictionary</tt> interface
   * @param properties a <tt>List</tt> of <tt>Property</tt> objects used to initialize/configure the dictionary instance
   * @param runtime an array of bytes that contains data collected during runtime execution of the dictionary
   */
  public DictionaryDto(String name, String clazz, List<Property> properties, byte[] runtime) {
    name_ = name;
    class_ = clazz;
    properties_ = properties;
    runtimeData_ = runtime;
  }

  /**
   * @return the name of the dictionary
   */
  public String getName() {
    return name_;
  }

  /**
   * @return the implementing class
   */
  public String getClazz() {
    return class_;
  }

  /**
   * @return a <tt>List</tt> of <tt>Property</tt> object
   */
  public List<Property> getProperties() {
    return properties_;
  }

  /**
   * @return an array of bytes that may have been collected at runtime
   */
  public byte[] getRuntimeData() {
    return runtimeData_;
  }

  /**
   * @param runtimeData an array of bytes collected during runtime execution
   */
  public void setRuntimeData(byte[] runtimeData) {
    runtimeData_ = runtimeData;
  }

}
