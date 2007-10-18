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
package org.obiba.bitwise.schema;

import org.obiba.bitwise.annotation.AnnotationStoreSchemaBuilder;
import org.xml.sax.InputSource;


/**
 * Creates a <tt>StoreSchema</tt> using either Java Annotations or an XML document to define the store context.
 */
public class StoreSchemaFactory {

  private StoreSchemaFactory() {
    super();
  }


  /**
   * Create a schema using the annotations of the class given as parameter.
   * @param pClass is the model class to build the store schema.
   * @return The newly created store schema.
   */
  public static StoreSchema getStoreSchema(Class pClass) {
    AnnotationStoreSchemaBuilder builder = new AnnotationStoreSchemaBuilder();
    return builder.createSchema(pClass);
  }


  /**
   * Create a schema using an XML document provided as an InputSource.
   * @param pIs is the InputSource object linked to the XML Store definition document.
   * @return The newly created store schema.
   */
  public static StoreSchema getStoreSchema(InputSource pIs) {
    XmlStoreSchemaBuilder builder = new XmlStoreSchemaBuilder();
    return builder.parse(pIs);
  }

}
