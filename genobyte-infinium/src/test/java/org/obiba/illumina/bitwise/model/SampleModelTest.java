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
package org.obiba.illumina.bitwise.model;

import junit.framework.TestCase;

import org.obiba.bitwise.annotation.AnnotationStoreSchemaBuilder;
import org.obiba.bitwise.schema.StoreSchema;

public class SampleModelTest extends TestCase {

  public void testAnnotationSchema() {
    AnnotationStoreSchemaBuilder builder = new AnnotationStoreSchemaBuilder();
    try {
      StoreSchema ss = builder.createSchema(Sample.class);
      String dName = ss.getField("gender").getDictionary();
      assertEquals("org.obiba.bitwise.dictionary.EnumDictionary", ss.getDictionary(dName).getClazz());
    } catch(Exception e) {
      fail(e.getMessage());
    }
  }
}
