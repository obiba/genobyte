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

import java.io.IOException;
import java.nio.ByteBuffer;

import org.obiba.bitwise.dao.BitVectorDtoDao;
import org.obiba.bitwise.dao.impl.util.BitPackingUtil;
import org.obiba.bitwise.dto.BitVectorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibatis.dao.client.DaoManager;

import jdbm.helper.Serializer;

public class BitVectorDtoDaoJdbmImpl extends BaseAutoKeyDaoImpl<BitVectorDto> implements BitVectorDtoDao {

  private static final String VECTOR_TREE = "vector";

  static final Logger log = LoggerFactory.getLogger(BaseRecordManagerDaoImpl.class);

  private Serializer s = new BitVectorSerializer();

  public BitVectorDtoDaoJdbmImpl(DaoManager manager) {
    super(manager);
  }

  @Override
  protected String getManagerName() {
    return VECTOR_TREE;
  }

  @Override
  protected Serializer getValueSerializer() {
    return s;
  }

  @Override
  protected long getKey(BitVectorDto value) {
    return value.getId();
  }

  @Override
  protected void setAutoKey(long key, BitVectorDto value) {
    value.setId(key);
  }

  static private class BitVectorSerializer implements Serializer {

    private static final long serialVersionUID = -1262584375056397712L;

    public byte[] serialize(Object o) throws IOException {
      BitVectorDto v = (BitVectorDto) o;
      ByteBuffer bb = BitPackingUtil.allocate(4 + 4 + v.getBits().length * 8);
      bb.putInt(v.getSize());
      BitPackingUtil.putLongArray(v.getBits(), bb);
      return bb.array();
    }

    public Object deserialize(byte[] bytes) throws IOException {
      if(bytes == null || bytes.length == 0) return null;
      ByteBuffer bb = BitPackingUtil.toByteBuffer(bytes);
      BitVectorDto d = new BitVectorDto();
      d.setSize(bb.getInt());
      d.setBits(BitPackingUtil.readLongArray(bb));
      return d;
    }

  }
}
