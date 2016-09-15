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
import org.obiba.bitwise.dao.DictionaryDtoDao;
import org.obiba.bitwise.dto.DictionaryDto;
import org.obiba.bitwise.util.Property;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DictionaryDtoDaoJdbmImpl extends BaseBTreeJdbmDaoImpl<DictionaryDto, String> implements DictionaryDtoDao {

  /**
   * Maps dictionary names to DictionaryDto objects
   */
  private static final String DICTIONARY_DB = "dictionary";

  public DictionaryDtoDaoJdbmImpl(DaoManager manager) {
    super(manager);
  }

  @Override
  protected String getManagerName() {
    return DICTIONARY_DB;
  }

  @Override
  protected String getKey(DictionaryDto value) {
    return value.getName();
  }

  @SuppressWarnings("unchecked")
  public List<String> keys() {
    List<String> keys = new ArrayList<String>(getBtree().size());
    try {
      Tuple t = new Tuple();
      TupleBrowser tb = getBtree().browse();
      while(tb.getNext(t) == true) {
        keys.add((String) t.getKey());
      }
      return keys;
    } catch(IOException e) {
      throw new JdbmRuntimeException(e);
    }
  }

  @Override
  protected Serializer getValueSerializer() {
    return new DictionaryDtoBinding();
  }

  @Override
  protected Serializer getKeySerializer() {
    return new StringSerializer();
  }

  @Override
  protected Comparator<?> getKeyComparator() {
    return new StringComparator();
  }

  static private class DictionaryDtoBinding implements Serializer {

    private static final long serialVersionUID = 4138149368470237531L;

    public Object deserialize(byte[] bytes) throws IOException {
      String name;
      String className;
      List<Property> props;
      try {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);
        name = dis.readUTF();
        className = dis.readUTF();

        int propertyCount = dis.readInt();
        props = new ArrayList<Property>(propertyCount);
        for(int i = 0; i < propertyCount; i++) {
          String prop = dis.readUTF();
          String value = dis.readUTF();
          props.add(new Property(prop, value));
        }
        byte[] runtime = null;
        int runtimeSize = dis.readInt();
        if(runtimeSize >= 0) {
          runtime = new byte[runtimeSize];
          dis.read(runtime);
        }

        dis.close();
        return new DictionaryDto(name, className, props, runtime);
      } catch(IOException e) {
        throw new RuntimeException(e);
      }
    }

    public byte[] serialize(Object o) throws IOException {
      DictionaryDto d = (DictionaryDto) o;
      List<Property> props = d.getProperties();

      int size = props != null ? props.size() : 0;

      try {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(d.getName());
        dos.writeUTF(d.getClazz());
        dos.writeInt(size);
        if(props != null) {
          for(Property property : props) {
            dos.writeUTF(property.getName());
            dos.writeUTF(property.getValue());
          }
        }
        byte[] runtime = d.getRuntimeData();
        if(runtime != null) {
          dos.writeInt(runtime.length);
          dos.write(runtime);
        } else {
          dos.writeInt(-1);
        }
        dos.flush();
        return baos.toByteArray();
      } catch(IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

}
