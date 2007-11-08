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
package org.obiba.bitwise.dao.impl.bdb;

import java.nio.ByteBuffer;

import org.obiba.bitwise.dao.BitVectorDtoDao;
import org.obiba.bitwise.dto.BitVectorDto;

import com.ibatis.dao.client.DaoManager;
import com.sleepycat.bind.EntityBinding;
import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.collections.PrimaryKeyAssigner;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;

public class BitVectorDtoDaoBdbImpl extends BaseAutoKeyDaoImpl<BitVectorDto, Long> implements BitVectorDtoDao {

  private static final String VECTOR_DB = "vector.db";
  private static final String VECTOR_PK_SEQ = "vector.id";

  public BitVectorDtoDaoBdbImpl(DaoManager manager) {
    super(manager);
  }

  private Database getVectorDb() {
    try {
      return getContext().getDatabase(VECTOR_DB);
    } catch (DatabaseException e) {
      throw new RuntimeException(e);
    }
  }
  
  @Override
  protected Long getKey(BitVectorDto value) {
    return value.getId();
  }

  @Override
  protected void setAutoKey(Long key, BitVectorDto value) {
    value.setId(key);
  }

  @Override
  synchronized protected StoredMap createStoredMap() {
    EntryBinding keyBinding = TupleBinding.getPrimitiveBinding(Long.class);
    EntityBinding valueBinding = new BitVectorDtoBinding();
    PrimaryKeyAssigner pk = new BitVectorPk();
    return new StoredMap(getVectorDb(), keyBinding, valueBinding, pk);
  }

  class BitVectorPk implements PrimaryKeyAssigner {
    public void assignKey(DatabaseEntry entry) throws DatabaseException {
      long key = getContext().getSequence(VECTOR_PK_SEQ).get(getContext().getEnvironment().getThreadTransaction(), 1);
      LongBinding.longToEntry(key, entry);
    }
  }

  static private class BitVectorDtoBinding implements EntityBinding {

    /*
     * @see com.sleepycat.bind.EntityBinding#entryToObject(com.sleepycat.je.DatabaseEntry, com.sleepycat.je.DatabaseEntry)
     */
    public Object entryToObject(DatabaseEntry key, DatabaseEntry entry) {
      ByteBuffer bb = BdbUtil.toByteBuffer(entry);
      BitVectorDto d = new BitVectorDto();
      d.setId(LongBinding.entryToLong(key));
      d.setSize(bb.getInt());
      d.setBits(BdbUtil.readLongArray(bb));
      return d;
    }

    /*
     * @see com.sleepycat.bind.EntityBinding#objectToData(java.lang.Object, com.sleepycat.je.DatabaseEntry)
     */
    public void objectToData(Object o, DatabaseEntry entry) {
      BitVectorDto v = (BitVectorDto)o;
      ByteBuffer bb = BdbUtil.allocate(4 + 4 + v.getBits().length * 8); 
      bb.putInt(v.getSize());
      BdbUtil.putLongArray(v.getBits(), bb);
      entry.setData(bb.array(), 0, bb.position());
    }

    /*
     * @see com.sleepycat.bind.EntityBinding#objectToKey(java.lang.Object, com.sleepycat.je.DatabaseEntry)
     */
    public void objectToKey(Object o, DatabaseEntry entry) {
      BitVectorDto d = (BitVectorDto)o;
      LongBinding.longToEntry(d.getId(), entry);
    }
  }
}
