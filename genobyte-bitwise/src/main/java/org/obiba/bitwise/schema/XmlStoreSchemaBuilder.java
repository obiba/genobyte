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

import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.apache.commons.digester.Digester;
import org.obiba.bitwise.util.Property;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * Using an XML document, builds a new <tt>StoreSchema</tt> to be transformed into a store.
 */
public class XmlStoreSchemaBuilder {

  static final private String ROOT = "store-schema";

  static final private String DICTIONARIES = ROOT + "/dictionaries";

  static final private String DICTIONARY = DICTIONARIES + "/dictionary";

  static final private String PROPERTY = DICTIONARY + "/property";

  static final private String FIELDS = ROOT + "/fields";

  static final private String FIELD = FIELDS + "/field";

  public XmlStoreSchemaBuilder() {
    super();
  }

  /**
   * Analyzes an XML document provided as an <tt>InputSource</tt> and build a <tt>StoreSchema</tt> context object from the XML's content.
   * @param an <tt>InputSource</tt> to an XML file containing a store definition data.
   * @return a <tt>StoreSchema</tt> built from the information found in the XML.
   */
  public StoreSchema parse(InputSource is) {
    StoreSchemaDigester digester = new StoreSchemaDigester();
    digester.parse(is);
    return digester.ss_;
  }

  /**
   * Exception thrown when somethind wrong was found within the XML store definition file.
   */
  static public class ConfigurationException extends Exception {
    public ConfigurationException(String s) {
      super(s);
    }
  }

  class StoreSchemaDigester {
    Digester digester_ = new Digester();

    StoreSchema ss_ = new StoreSchema();

    StoreSchemaDigester() {
      digester_.push(ss_);
      digester_.addSetProperties(ROOT);

      digester_.addFactoryCreate(DICTIONARY, new DictionaryFactory());
//      digester_.addObjectCreate(DICTIONARY, DictionaryMetaData.class);
//      digester_.addSetProperties(DICTIONARY);
      digester_.addSetNext(DICTIONARY, "addDictionary", "org.obiba.bitwise.schema.DictionaryMetaData");

      digester_.addObjectCreate(PROPERTY, Property.class);
      digester_.addSetProperties(PROPERTY);
      digester_.addSetNext(PROPERTY, "addProperty", "org.obiba.bitwise.util.Property");

      digester_.addObjectCreate(FIELD, FieldMetaData.class);
      digester_.addSetProperties(FIELD);
      digester_.addSetNext(FIELD, "addField", "org.obiba.bitwise.schema.FieldMetaData");

      digester_.addCallMethod("schema/defaultSearchField", "setDefaultSearchField", 1);
      digester_.addCallParam("schema/defaultSearchField", 0, "field");
    }

    void parse(InputSource is) {
      try {
        digester_.parse(is);
      } catch(IOException e) {
        throw new RuntimeException(e);
      } catch(SAXException e) {
        throw new RuntimeException(e);
      }
    }
  }

  static class DictionaryFactory extends AbstractObjectCreationFactory {
    @Override
    public Object createObject(Attributes atts) throws Exception {
      String name = atts.getValue("name");
      String clazz = atts.getValue("class");
      DictionaryMetaData meta = new DictionaryMetaData();
      meta.setName(name);
      meta.setClass(clazz);
      return meta;
    }
  }

}
