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
import org.obiba.bitwise.dao.DictionaryDtoDao;
import org.obiba.bitwise.dto.DictionaryDto;
import org.obiba.bitwise.util.Property;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DictionaryDtoDaoBdbImpl extends BaseCrudDaoImpl<DictionaryDto, String> implements DictionaryDtoDao {

  /**
   * Maps dictionary names to DictionaryDto objects
   */
  private static final String DICTIONARY_DB = "dictionary.db";

  public DictionaryDtoDaoBdbImpl(DaoManager manager) {
    super(manager);
  }

  private Database getDictionaryDb() {
    try {
      return getContext().getDatabase(DICTIONARY_DB);
    } catch(DatabaseException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected String getKey(DictionaryDto value) {
    return value.getName();
  }

  @SuppressWarnings("unchecked")
  public List<String> keys() {
    return new LinkedList<String>(getMap().keySet());
  }

  @Override
  protected StoredMap createStoredMap() {
    EntryBinding keyBinding = TupleBinding.getPrimitiveBinding(String.class);
    EntityBinding valueBinding = new DictionaryDtoBinding();
    return new StoredMap(getDictionaryDb(), keyBinding, valueBinding, true);
  }

  static private class DictionaryDtoBinding implements EntityBinding {

    /*
     * @see com.sleepycat.bind.EntityBinding#entryToObject(com.sleepycat.je.DatabaseEntry, com.sleepycat.je.DatabaseEntry)
     */
    public Object entryToObject(DatabaseEntry key, DatabaseEntry entry) {
      String name;
      String className;
      List<Property> props;
      try {
        ByteArrayInputStream bais = new ByteArrayInputStream(entry.getData());
        DataInputStream dis = new DataInputStream(bais);
        name = StringBinding.entryToString(key);
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

    /*
     * @see com.sleepycat.bind.EntityBinding#objectToData(java.lang.Object, com.sleepycat.je.DatabaseEntry)
     */
    public void objectToData(Object o, DatabaseEntry entry) {
      DictionaryDto d = (DictionaryDto) o;
      List<Property> props = d.getProperties();

      int size = props != null ? props.size() : 0;

      try {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
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
        entry.setData(baos.toByteArray());
      } catch(IOException e) {
        throw new RuntimeException(e);
      }
    }

    /*
     * @see com.sleepycat.bind.EntityBinding#objectToKey(java.lang.Object, com.sleepycat.je.DatabaseEntry)
     */
    public void objectToKey(Object o, DatabaseEntry entry) {
      DictionaryDto d = (DictionaryDto) o;
      StringBinding.stringToEntry(d.getName(), entry);
    }
  }

}
