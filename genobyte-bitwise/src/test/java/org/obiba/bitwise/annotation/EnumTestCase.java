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
import org.obiba.bitwise.schema.DictionaryMetaData;
import org.obiba.bitwise.schema.FieldMetaData;
import org.obiba.bitwise.schema.StoreSchema;

public class EnumTestCase extends TestCase {

  // Test for GEN-30
  public void testEnumModel() {
    AnnotationStoreSchemaBuilder sca = new AnnotationStoreSchemaBuilder();
    StoreSchema ss = sca.createSchema(EnumTestCaseModel.class);
    assertEquals(2, ss.getFields().size());
    assertNotNull(ss.getField("id"));

    FieldMetaData enumMetaData = ss.getField("enumField");
    assertNotNull(enumMetaData);
    DictionaryMetaData enumDict = ss.getDictionary(enumMetaData.getDictionary());
    assertNotNull(enumDict);
    assertEquals("org.obiba.bitwise.dictionary.EnumDictionary", enumDict.getClazz());
  }
}
