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
package org.obiba.bitwise.dao;

import org.obiba.bitwise.dto.FieldDto;
import org.obiba.bitwise.util.BitwiseStoreTestingHelper;

public class FieldDtoDaoTest extends BaseBdbDaoTestCase {

  FieldDtoDao dao = null;

  BitwiseStoreTestingHelper store_ = null;

  public FieldDtoDaoTest() {
    super();
  }

  public FieldDtoDaoTest(String t) {
    super(t);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    store_ = createMockStore("MOCK_STORE", 10000);
    dao = (FieldDtoDao) KeyedDaoManager.getInstance(store_.getDaoKey()).getDao(FieldDtoDao.class);
  }

  @Override
  protected void tearDown() throws Exception {
    dao.delete("newField");
    super.tearDown();
  }

  private FieldDto getBogusDto() {
    FieldDto d = new FieldDto();
    d.setName("newField");
    d.setBitIndex(new long[5]);
    d.setSize(40);
    d.setDictionaryName("bogusDictionary");
    return d;
  }

  public void testDelete() {
    FieldDto d = getBogusDto();
    dao.create(d);
    assertEquals("newField", d.getName());

    dao.delete("newField");

    assertNull(dao.load("newField"));
  }

  public void testLoad() {
    FieldDto d = getBogusDto();
    dao.create(d);
    assertEquals("newField", d.getName());
    assertNotNull(dao.load("newField"));
  }

  public void testSave() {
    FieldDto d = getBogusDto();
    dao.create(d);
    assertEquals("newField", d.getName());

    d.getBitIndex()[0] = 5106;

    dao.save(d);

    d = dao.load("newField");

    assertEquals(5106, d.getBitIndex()[0]);
  }
}
