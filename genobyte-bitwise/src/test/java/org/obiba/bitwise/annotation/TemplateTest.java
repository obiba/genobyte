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

import junit.framework.TestCase;
import org.obiba.bitwise.dictionary.IntegerDictionary;
import org.obiba.bitwise.schema.DictionaryMetaData;
import org.obiba.bitwise.schema.FieldMetaData;
import org.obiba.bitwise.schema.StoreSchema;

public class TemplateTest extends TestCase {

  public void testSimpleTemplate() {
    AnnotationStoreSchemaBuilder sca = new AnnotationStoreSchemaBuilder();
    StoreSchema ss = sca.createSchema(TemplateModel.class);
    FieldMetaData fieldMetaData = ss.getField("calls_1234");
    assertNotNull(fieldMetaData);
    DictionaryMetaData d = ss.getDictionary(fieldMetaData.getDictionary());
    assertNotNull(d);
    assertEquals(IntegerDictionary.class.getName(), d.getClazz());
  }
}