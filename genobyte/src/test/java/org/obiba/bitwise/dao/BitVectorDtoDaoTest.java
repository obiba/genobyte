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
package org.obiba.bitwise.dao;

import java.util.Arrays;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.BitVectorUtil;
import org.obiba.bitwise.BitwiseStoreTestingHelper;
import org.obiba.bitwise.dto.BitVectorDto;

public class BitVectorDtoDaoTest extends BaseBdbDaoTestCase {
  BitVectorDtoDao dao = null;

  BitwiseStoreTestingHelper store_ = null;
  
  public BitVectorDtoDaoTest() {
    super();
  }

  public BitVectorDtoDaoTest(String t) {
    super(t);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    store_ = createMockStore("MOCK_STORE", 10000);
    dao = (BitVectorDtoDao)KeyedDaoManager.getInstance(store_.getDaoKey()).getDao(BitVectorDtoDao.class);
  }
  
  public void testCreate() {
    BitVector v = new BitVector(10000);
    BitVectorDto d = BitVectorUtil.toDto(-1, v);
    dao.create(d);
    assertTrue(d.getId() > 0);

    BitVectorDto d2 = dao.load(d.getId());
    assertNotNull(d2);
    BitVector v2 = BitVectorUtil.toVector(d2);

    assertEquals(v, v2);
  }

  public void testDelete() {
    BitVector v = new BitVector(10000);
    BitVectorDto d = BitVectorUtil.toDto(-1, v);
    dao.create(d);
    assertTrue(d.getId() > 0);
    
    dao.delete(d.getId());
    
    assertNull(dao.load(d.getId()));
  }

  public void testLoad() {
    BitVector v = new BitVector(10000);
    BitVectorDto d = BitVectorUtil.toDto(-1, v);
    dao.create(d);
    assertTrue(d.getId() > 0);
    assertNotNull(dao.load(d.getId()));
  }

  public void testSave() {
    BitVector v = new BitVector(10000);
    BitVectorDto d = BitVectorUtil.toDto(-1, v);
    dao.create(d);
    long id = d.getId();
    assertTrue(id > 0);
    
    for(int i = 0; i < 10000; i+=2) {
      v.set(i);
    }
    
    d = BitVectorUtil.toDto(-1, v);
    long[] array = d.getBits();
    d.setId(id);
    dao.save(d);

    d = dao.load(id);
    long[] newarray = d.getBits();
    assertTrue(Arrays.equals(array, newarray));
  }
}
