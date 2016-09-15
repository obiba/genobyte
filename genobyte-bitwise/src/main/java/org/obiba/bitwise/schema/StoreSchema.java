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
import java.util.*;

/**
 * Holds information about a bitwise store context. This includes the fields of the store and the dictionaries
 * used to encode values into <tt>BitVectors</tt> and decode them back to original values.
 */
public class StoreSchema implements Serializable {

  private static final long serialVersionUID = 6299436323378565183L;

  private String name_ = null;

  private String version_ = null;

  private Map<String, DictionaryMetaData> dictionaries_ = new HashMap<String, DictionaryMetaData>();

  private Map<String, FieldMetaData> fields_ = new HashMap<String, FieldMetaData>();

  private List<FieldMetaData> templates_ = new LinkedList<FieldMetaData>();

  public StoreSchema() {
    super();
  }

  /**
   * Sets the name of this store schema.
   *
   * @param name the name to give to this schema.
   */
  public void setName(String name) {
    name_ = name;
  }

  /**
   * Sets the version of this store schema.
   *
   * @param version the version of the schema.
   */
  public void setVersion(String version) {
    version_ = version;
  }

  /**
   * Gets the name of this store schema.
   *
   * @return the name of this schema.
   */
  public String getName() {
    return name_;
  }

  /**
   * Gets the version of this store schema.
   *
   * @return the name of this schema.
   */
  public String getVersion() {
    return version_;
  }

  /**
   * Get the dictionary with given name.
   *
   * @param name is the name of the dictionary to get from the schema.
   * @return a DictionaryMetaData corresponding to the given name, or <code>null</code> if there is
   * no dictionary with that name.
   */
  public DictionaryMetaData getDictionary(String name) {
    return dictionaries_.get(name);
  }

  /**
   * Gets contextual information for all dictionaries in use in the store.
   *
   * @return a <tt>Collection</tt> holding dictionaries contextual information.
   */
  public Collection<DictionaryMetaData> getDictionaries() {
    return dictionaries_.values();
  }

  /**
   * Adds a dictionary to this store context.
   *
   * @param d the contextual information for this dictionary.
   */
  public void addDictionary(DictionaryMetaData d) {
    dictionaries_.put(d.getName(), d);
  }

  /**
   * Gets contextual information for field that exists in the store.
   *
   * @param name the name of the field.
   * @return the contextual information for a field.
   */
  public FieldMetaData getField(String name) {
    FieldMetaData meta = fields_.get(name);
    if (meta == null) {
      for (FieldMetaData template : templates_) {
        if (name.startsWith(template.getName())) {
          meta = new FieldMetaData();
          meta.setName(name);
          meta.setDictionary(template.getDictionary());
          meta.setTemplate(false);
          break;
        }
      }
    }
    return meta;
  }

  /**
   * Gets contextual information for all fields found in the store.
   *
   * @return a <tt>Collection</tt> holding fields contextual information.
   */
  public Collection<FieldMetaData> getFields() {
    return fields_.values();
  }

  /**
   * Adds a field to this store context.
   *
   * @param f the contextual information for this field.
   */
  public void addField(FieldMetaData f) {
    if (f.isTemplate()) {
      templates_.add(f);
    } else {
      fields_.put(f.getName(), f);
    }
  }

}
