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
package org.obiba.bitwise.dao.impl.jdbc;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.obiba.bitwise.dao.FieldDtoDao;
import org.obiba.bitwise.dao.impl.bdb.BdbUtil;
import org.obiba.bitwise.dto.FieldDto;


import com.ibatis.dao.client.DaoManager;

public class FieldDtoDaoJdbcImpl extends BaseDaoJdbcImpl implements FieldDtoDao {

  public static long allo = 0;
  public static long count = 0;

  private static final String FIELD_TABLE = "field";

  private static final String INSERT = "INSERT INTO " + FIELD_TABLE + "(id, value) VALUES(?, ?)";
  private static final String UPDATE = "UPDATE " + FIELD_TABLE + " SET value = ? WHERE id = ?";
  private static final String DELETE = "DELETE FROM " + FIELD_TABLE + " WHERE id = ?";
  private static final String SELECT = "SELECT value FROM " + FIELD_TABLE + " WHERE id = ?";

  private FieldDtoBinding binding = new FieldDtoBinding();

  public FieldDtoDaoJdbcImpl(DaoManager manager) {
    super(manager);
  }

  /*
   * @see org.obiba.bitwise.dao.BitVectorDao#create()
   */
  public void create(FieldDto v) {
    Connection c = getConnection();
    PreparedStatement ps = null;
    try {
      ps = c.prepareStatement(INSERT);
      ByteBuffer bb = binding.objectToBuffer(v);
      ps.setString(1, v.getName());
      ps.setBinaryStream(2, new ByteArrayInputStream(bb.array()), bb.position());
      ps.execute();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        if(ps != null) ps.close(); ps = null;
      } catch (SQLException e) {
      }
    }
  }

  /*
   * @see org.obiba.bitwise.dao.BitVectorDao#delete(int)
   */
  public void delete(String name) {
    Connection c = getConnection();
    PreparedStatement ps = null;
    try {
      ps = c.prepareStatement(DELETE);
      ps.setString(1, name);
      ps.execute();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        if(ps != null) ps.close(); ps = null;
      } catch (SQLException e) {
      }
    }
  }

  /*
   * @see org.obiba.bitwise.dao.BitVectorDao#load(int)
   */
  public FieldDto load(String name) {
    Connection c = getConnection();
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = c.prepareStatement(SELECT);
      ps.setString(1, name);
      rs = ps.executeQuery();
      if(rs.next()) {
        ByteBuffer bb = ByteBuffer.wrap(rs.getBytes(1));
        return binding.bufferToObject(name, bb);
      }
      return null;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        if(ps != null) ps.close(); ps = null;
        if(rs != null) rs.close(); rs = null;
      } catch (SQLException e) {
      }
    }
  }

  /*
   * @see org.obiba.bitwise.dao.BitVectorDao#save(org.obiba.bitwise.BitVector)
   */
  public void save(FieldDto d) {
    Connection c = getConnection();
    PreparedStatement ps = null;
    try {
      ps = c.prepareStatement(UPDATE);
      ByteBuffer bb = binding.objectToBuffer(d);
      ps.setBinaryStream(1, new ByteArrayInputStream(bb.array()), bb.position());
      ps.setString(2, d.getName());
      ps.execute();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        if(ps != null) ps.close(); ps = null;
      } catch (SQLException e) {
      }
    }
  }
  
  public List<String> keys() {
    return null;
  }
  
  public List<FieldDto> values() {
    return null;
  }

  static private class FieldDtoBinding implements ByteBufferBinding<String, FieldDto> {

    public FieldDto bufferToObject(String key, ByteBuffer bb) {
      FieldDto d = new FieldDto();
      d.setName(key);
      d.setSize(bb.getInt());
      d.setBitIndex(BdbUtil.readIntArray(bb));
      d.setDictionaryName(BdbUtil.readString(bb));
      return d;
    }
    
    public ByteBuffer objectToBuffer(FieldDto d) {
      ByteBuffer bb = BdbUtil.allocate(4 + d.getBitIndex().length * 4 + 256);
      bb.putInt(d.getSize());
      BdbUtil.putIntArray(d.getBitIndex(), bb);
      BdbUtil.putString(d.getDictionaryName(), bb);
      return bb;
    }

  }
}
