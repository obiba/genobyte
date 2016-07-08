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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.BitVectorUtil;
import org.obiba.bitwise.dao.BitwiseStoreDtoDao;
import org.obiba.bitwise.dto.BitVectorDto;
import org.obiba.bitwise.dto.BitwiseStoreDto;
import org.obiba.bitwise.schema.StoreSchema;

import com.ibatis.dao.client.DaoManager;
import com.sleepycat.bind.EntityBinding;
import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;

public class BitwiseStoreDtoDaoBdbImpl extends BaseCrudDaoImpl<BitwiseStoreDto, String> implements BitwiseStoreDtoDao {

  private static final String BITWISE_DB = "bitwise.db";

  private StoredMap storeMap_ = null;

  public BitwiseStoreDtoDaoBdbImpl(DaoManager manager) {
    super(manager);
  }

  @Override
  public void delete(String name) {
    super.delete(name);

    // Truncate the BDB Environment before closing. This will effectively delete everything in the store.
    getContext().setTruncateOnClose(true);
  }

  @Override
  protected String getKey(BitwiseStoreDto value) {
    return value.getName();
  }

  @Override
  synchronized protected StoredMap createStoredMap() {
    if(storeMap_ == null) {
      EntryBinding keyBinding = TupleBinding.getPrimitiveBinding(String.class);
      EntityBinding valueBinding = new BitwiseStoreDtoBinding();
      storeMap_ = new StoredMap(getBitwiseStoreDb(), keyBinding, valueBinding, true);
    }
    return storeMap_;
  }

  private Database getBitwiseStoreDb() {
    try {
      return getContext().getDatabase(BITWISE_DB);
    } catch(DatabaseException e) {
      throw new RuntimeException(e);
    }
  }

  static private class BitwiseStoreDtoBinding implements EntityBinding {

    /*
     * @see com.sleepycat.bind.EntityBinding#entryToObject(com.sleepycat.je.DatabaseEntry, com.sleepycat.je.DatabaseEntry)
     */
    public Object entryToObject(DatabaseEntry key, DatabaseEntry entry) {

      ByteArrayInputStream input = new ByteArrayInputStream(entry.getData());
      BitwiseStoreDto d = new BitwiseStoreDto();
      try {
        DataInputStream dis = new DataInputStream(input);
        d.setName(StringBinding.entryToString(key));
        d.setCapacity(dis.readInt());
        d.setDeleted(readBitVector(d.getCapacity(), dis));
        d.setCleared(readBitVector(d.getCapacity(), dis));

        ObjectInputStream ois = new ObjectInputStream(dis);
        StoreSchema ss = (StoreSchema) ois.readObject();
        d.setSchema(ss);
      } catch(IOException e) {
        throw new RuntimeException(e);
      } catch(ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
      return d;
    }

    /*
     * @see com.sleepycat.bind.EntityBinding#objectToData(java.lang.Object, com.sleepycat.je.DatabaseEntry)
     */
    public void objectToData(Object o, DatabaseEntry entry) {
      BitwiseStoreDto d = (BitwiseStoreDto) o;

      ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
      try {
        DataOutputStream dos = new DataOutputStream(output);
        dos.writeInt(d.getCapacity());
        writeBitVector(dos, d.getDeleted());
        writeBitVector(dos, d.getCleared());

        ObjectOutputStream oos = new ObjectOutputStream(dos);
        oos.writeObject(d.getSchema());
        oos.flush();
        oos.close();

        entry.setData(output.toByteArray());
      } catch(IOException e) {
        throw new RuntimeException(e);
      }
    }

    /*
     * @see com.sleepycat.bind.EntityBinding#objectToKey(java.lang.Object, com.sleepycat.je.DatabaseEntry)
     */
    public void objectToKey(Object o, DatabaseEntry entry) {
      BitwiseStoreDto d = (BitwiseStoreDto) o;
      StringBinding.stringToEntry(d.getName(), entry);
    }

    /**
     * Writes a {@link BitVector} instance to the specified stream
     * @param dos the stream to write to
     * @param v the {@link BitVector} to write to
     * @throws IOException when an unexpected error occurs while writing to the stream
     */
    private void writeBitVector(DataOutputStream dos, BitVector v) throws IOException {
      BitVectorDto dto = BitVectorUtil.toDto(-1, v);
      long[] bits = dto.getBits();
      if(bits == null) bits = new long[0];
      dos.writeInt(bits.length);
      for(int i = 0; i < bits.length; i++) dos.writeLong(bits[i]);
    }

    private BitVector readBitVector(int capacity, DataInputStream dis) throws IOException {
      int size = dis.readInt();
      long bits[] = new long[size];
      for(int i = 0; i < size; i++) {
        bits[i] = dis.readLong();
      }
      BitVectorDto dto = new BitVectorDto();
      dto.setSize(capacity);
      dto.setBits(bits);
      return BitVectorUtil.toVector(dto);
    }
  }
}
