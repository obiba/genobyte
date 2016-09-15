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
package org.obiba.bitwise.dao.impl.bdb;

import com.ibatis.dao.client.DaoManager;
import com.sleepycat.bind.EntityBinding;
import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import org.obiba.bitwise.dao.FieldDtoDao;
import org.obiba.bitwise.dao.impl.util.BitPackingUtil;
import org.obiba.bitwise.dto.FieldDto;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class FieldDtoDaoBdbImpl extends BaseCrudDaoImpl<FieldDto, String> implements FieldDtoDao {

  private static final String FIELD_DB = "field.db";

  public FieldDtoDaoBdbImpl(DaoManager manager) {
    super(manager);
  }

  @SuppressWarnings("unchecked")
  public List<String> keys() {
    return new LinkedList<String>(getMap().keySet());
  }

  @SuppressWarnings("unchecked")
  public List<FieldDto> values() {
    return new LinkedList<FieldDto>(getMap().values());
  }

  private Database getFieldDb() {
    try {
      return getContext().getDatabase(FIELD_DB);
    } catch(DatabaseException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected String getKey(FieldDto value) {
    return value.getName();
  }

  @Override
  protected StoredMap createStoredMap() {
    EntryBinding keyBinding = TupleBinding.getPrimitiveBinding(String.class);
    EntityBinding valueBinding = new FieldDtoBinding();
    return new StoredMap(getFieldDb(), keyBinding, valueBinding, true);
  }

  static private class FieldDtoBinding implements EntityBinding {

    /*
     * @see com.sleepycat.bind.EntityBinding#entryToObject(com.sleepycat.je.DatabaseEntry, com.sleepycat.je.DatabaseEntry)
     */
    public Object entryToObject(DatabaseEntry key, DatabaseEntry entry) {
      ByteBuffer bb = BitPackingUtil.toByteBuffer(entry.getData());
      FieldDto d = new FieldDto();
      d.setName(StringBinding.entryToString(key));
      d.setSize(bb.getInt());
      d.setBitIndex(BitPackingUtil.readLongArray(bb));
      d.setDictionaryName(BitPackingUtil.readString(bb));
      return d;
    }

    /*
     * @see com.sleepycat.bind.EntityBinding#objectToData(java.lang.Object, com.sleepycat.je.DatabaseEntry)
     */
    public void objectToData(Object o, DatabaseEntry entry) {
      FieldDto d = (FieldDto) o;

      ByteBuffer bb = BitPackingUtil.allocate(4 + d.getBitIndex().length * 8 + 256);
      bb.putInt(d.getSize());
      BitPackingUtil.putLongArray(d.getBitIndex(), bb);
      BitPackingUtil.putString(d.getDictionaryName(), bb);
      entry.setData(bb.array(), 0, bb.position());
    }

    /*
     * @see com.sleepycat.bind.EntityBinding#objectToKey(java.lang.Object, com.sleepycat.je.DatabaseEntry)
     */
    public void objectToKey(Object o, DatabaseEntry entry) {
      FieldDto d = (FieldDto) o;
      StringBinding.stringToEntry(d.getName(), entry);
    }
  }
}
