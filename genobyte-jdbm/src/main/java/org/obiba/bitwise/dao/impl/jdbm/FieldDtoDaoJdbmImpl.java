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
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;
import org.obiba.bitwise.dao.FieldDtoDao;
import org.obiba.bitwise.dao.impl.util.BitPackingUtil;
import org.obiba.bitwise.dto.FieldDto;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FieldDtoDaoJdbmImpl extends BaseBTreeJdbmDaoImpl<FieldDto, String> implements FieldDtoDao {

  private static final String FIELD_DB = "field";

  public FieldDtoDaoJdbmImpl(DaoManager manager) {
    super(manager);
  }

  @Override
  protected String getManagerName() {
    return FIELD_DB;
  }

  @SuppressWarnings("unchecked")
  public List<String> keys() {
    List<String> keys = new ArrayList<String>(getBtree().size());
    try {
      Tuple t = new Tuple();
      TupleBrowser tb = getBtree().browse();
      while (tb.getNext(t) == true) {
        keys.add((String) t.getKey());
      }
      return keys;
    } catch (IOException e) {
      throw new JdbmRuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public List<FieldDto> values() {
    List<FieldDto> values = new ArrayList<FieldDto>(getBtree().size());
    try {
      Tuple t = new Tuple();
      TupleBrowser tb = getBtree().browse();
      while (tb.getNext(t) == true) {
        values.add((FieldDto) t.getValue());
      }
      return values;
    } catch (IOException e) {
      throw new JdbmRuntimeException(e);
    }
  }

  @Override
  protected String getKey(FieldDto value) {
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
    return new FieldDtoBinding();
  }

  static private class FieldDtoBinding implements Serializer {

    private static final long serialVersionUID = 1781769312506788808L;

    public Object deserialize(byte[] bytes) throws IOException {
      ByteBuffer bb = BitPackingUtil.toByteBuffer(bytes);
      FieldDto d = new FieldDto();
      d.setName(BitPackingUtil.readString(bb));
      d.setSize(bb.getInt());
      d.setBitIndex(BitPackingUtil.readLongArray(bb));
      d.setDictionaryName(BitPackingUtil.readString(bb));
      return d;
    }

    public byte[] serialize(Object o) throws IOException {
      FieldDto d = (FieldDto) o;

      ByteBuffer bb = BitPackingUtil
          .allocate(d.getName().length() * 2 + 4 + 4 + d.getBitIndex().length * 8 + d.getDictionaryName().length() * 2);
      BitPackingUtil.putString(d.getName(), bb);
      bb.putInt(d.getSize());
      BitPackingUtil.putLongArray(d.getBitIndex(), bb);
      BitPackingUtil.putString(d.getDictionaryName(), bb);
      return bb.array();
    }

  }
}
