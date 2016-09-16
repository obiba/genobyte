/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.obiba.bitwise.dao.impl.bdb;

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
import org.obiba.bitwise.dao.BitVectorDtoDao;
import org.obiba.bitwise.dao.impl.util.BitPackingUtil;
import org.obiba.bitwise.dto.BitVectorDto;

import java.nio.ByteBuffer;

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
      ByteBuffer bb = BitPackingUtil.toByteBuffer(entry.getData());
      BitVectorDto d = new BitVectorDto();
      d.setId(LongBinding.entryToLong(key));
      d.setSize(bb.getInt());
      d.setBits(BitPackingUtil.readLongArray(bb));
      return d;
    }

    /*
     * @see com.sleepycat.bind.EntityBinding#objectToData(java.lang.Object, com.sleepycat.je.DatabaseEntry)
     */
    public void objectToData(Object o, DatabaseEntry entry) {
      BitVectorDto v = (BitVectorDto) o;
      ByteBuffer bb = BitPackingUtil.allocate(4 + 4 + v.getBits().length * 8);
      bb.putInt(v.getSize());
      BitPackingUtil.putLongArray(v.getBits(), bb);
      entry.setData(bb.array(), 0, bb.position());
    }

    /*
     * @see com.sleepycat.bind.EntityBinding#objectToKey(java.lang.Object, com.sleepycat.je.DatabaseEntry)
     */
    public void objectToKey(Object o, DatabaseEntry entry) {
      BitVectorDto d = (BitVectorDto) o;
      LongBinding.longToEntry(d.getId(), entry);
    }
  }
}
