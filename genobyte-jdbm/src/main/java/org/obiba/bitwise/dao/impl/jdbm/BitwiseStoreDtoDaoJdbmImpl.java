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
package org.obiba.bitwise.dao.impl.jdbm;

import com.ibatis.dao.client.DaoManager;
import jdbm.helper.Serializer;
import jdbm.helper.StringComparator;
import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.BitVectorUtil;
import org.obiba.bitwise.dao.BitwiseStoreDtoDao;
import org.obiba.bitwise.dto.BitVectorDto;
import org.obiba.bitwise.dto.BitwiseStoreDto;
import org.obiba.bitwise.schema.StoreSchema;

import java.io.*;
import java.util.Comparator;

public class BitwiseStoreDtoDaoJdbmImpl extends BaseBTreeJdbmDaoImpl<BitwiseStoreDto, String>
    implements BitwiseStoreDtoDao {

  private static final String BITWISE_DB = "bitwise";

  public BitwiseStoreDtoDaoJdbmImpl(DaoManager manager) {
    super(manager);
  }

  @Override
  protected String getManagerName() {
    return BITWISE_DB;
  }

  @Override
  public void delete(String name) {
    super.delete(name);
  }

  @Override
  protected String getKey(BitwiseStoreDto value) {
    return value.getName();
  }

  @Override
  protected Comparator<?> getKeyComparator() {
    return new StringComparator();
  }

  @Override
  protected Serializer getKeySerializer() {
    return new StringSerializer();
  }

  @Override
  protected Serializer getValueSerializer() {
    return new BitwiseStoreDtoBinding();
  }

  static private class BitwiseStoreDtoBinding implements Serializer {

    private static final long serialVersionUID = -8720152032567492670L;

    public Object deserialize(byte[] bytes) throws IOException {
      ByteArrayInputStream input = new ByteArrayInputStream(bytes);
      BitwiseStoreDto d = new BitwiseStoreDto();
      try {
        DataInputStream dis = new DataInputStream(input);
        d.setName(dis.readUTF());
        d.setCapacity(dis.readInt());
        d.setDeleted(readBitVector(d.getCapacity(), dis));
        d.setCleared(readBitVector(d.getCapacity(), dis));

        ObjectInputStream ois = new ObjectInputStream(dis);
        StoreSchema ss = (StoreSchema) ois.readObject();
        d.setSchema(ss);
      } catch (IOException e) {
        throw new RuntimeException(e);
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
      return d;
    }

    public byte[] serialize(Object o) throws IOException {
      BitwiseStoreDto d = (BitwiseStoreDto) o;

      ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
      try {
        DataOutputStream dos = new DataOutputStream(output);
        dos.writeUTF(d.getName());
        dos.writeInt(d.getCapacity());
        writeBitVector(dos, d.getDeleted());
        writeBitVector(dos, d.getCleared());

        ObjectOutputStream oos = new ObjectOutputStream(dos);
        oos.writeObject(d.getSchema());
        oos.flush();
        oos.close();

        return output.toByteArray();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    /**
     * Writes a {@link BitVector} instance to the specified stream
     *
     * @param dos the stream to write to
     * @param v   the {@link BitVector} to write to
     * @throws IOException when an unexpected error occurs while writing to the stream
     */
    private void writeBitVector(DataOutputStream dos, BitVector v) throws IOException {
      BitVectorDto dto = BitVectorUtil.toDto(-1, v);
      long[] bits = dto.getBits();
      if (bits == null) bits = new long[0];
      dos.writeInt(bits.length);
      for (int i = 0; i < bits.length; i++) dos.writeLong(bits[i]);
    }

    private BitVector readBitVector(int capacity, DataInputStream dis) throws IOException {
      int size = dis.readInt();
      long bits[] = new long[size];
      for (int i = 0; i < size; i++) {
        bits[i] = dis.readLong();
      }
      BitVectorDto dto = new BitVectorDto();
      dto.setSize(capacity);
      dto.setBits(bits);
      return BitVectorUtil.toVector(dto);
    }
  }
}
