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

import java.io.InputStream;

import org.xml.sax.InputSource;

import junit.framework.TestCase;

public class XmlStoreSchemaBuilderTest extends TestCase {

  public XmlStoreSchemaBuilderTest() {
    super();
  }

  public XmlStoreSchemaBuilderTest(String test) {
    super(test);
  }

  private InputStream getTestSchemaStream() {
    return getClass().getResourceAsStream("test-schema.xml");
  }

  public void testBuild() {
    XmlStoreSchemaBuilder builder = new XmlStoreSchemaBuilder();
    StoreSchema ss = null;
    try {
      ss = builder.parse(new InputSource(getTestSchemaStream()));
    } catch(Throwable t) {
      assertFalse("Unexpected exception thrown: " + t.getMessage(), true);
      return;
    }

    assertNotNull(ss);
    assertEquals("test-schema", ss.getName());
    assertEquals("1.0", ss.getVersion());

    assertNotNull(ss.getDictionary("test_dict_1"));
    assertNotNull(ss.getDictionary("test_dict_2"));
    assertNotNull(ss.getDictionary("test_dict_3"));
    assertNotNull(ss.getDictionary("test_dict_4"));

    DictionaryMetaData d = ss.getDictionary("test_dict_3");
    assertNotNull(d.getName());
    assertNotNull(d.getClazz());
    assertNotNull(d.getProperties());
    assertTrue(d.getProperties().size() > 0);
  }
}
