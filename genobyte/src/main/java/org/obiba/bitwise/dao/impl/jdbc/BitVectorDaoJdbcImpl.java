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

import org.obiba.bitwise.dao.BitVectorDtoDao;
import org.obiba.bitwise.dao.impl.bdb.BdbUtil;
import org.obiba.bitwise.dto.BitVectorDto;


import com.ibatis.dao.client.DaoManager;

public class BitVectorDaoJdbcImpl extends BaseDaoJdbcImpl implements BitVectorDtoDao {

  public static long allo = 0;
  public static long count = 0;
  private static final String VECTOR_TABLE = "bit_vector";

  private static final String INSERT_VECTOR = "INSERT INTO " + VECTOR_TABLE + "(id, vector) VALUES(null, ?)";
  private static final String UPDATE_VECTOR = "UPDATE " + VECTOR_TABLE + " SET vector = ? WHERE id = ?";
  private static final String DELETE_VECTOR = "DELETE FROM " + VECTOR_TABLE + " WHERE id = ?";
  private static final String GET_VECTOR    = "SELECT vector FROM " + VECTOR_TABLE + " WHERE id = ?";
  
  private BitVectorDtoBinding binding = new BitVectorDtoBinding();
  public BitVectorDaoJdbcImpl(DaoManager manager) {
    super(manager);
  }

  /*
   * @see org.obiba.bitwise.dao.BitVectorDao#create()
   */
  public void create(BitVectorDto v) {
    Connection c = getConnection();
    PreparedStatement ps = null;
    try {
      ps = c.prepareStatement(INSERT_VECTOR);
      ByteBuffer bb = binding.objectToBuffer(v);
      ps.setBinaryStream(1, new ByteArrayInputStream(bb.array()), bb.position());
      ps.execute();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        if(ps != null) ps.close(); ps = null;
      } catch (SQLException e) {
      }
    }

    Integer key = getAutoKey(c);
    v.setId(key);
  }

  /*
   * @see org.obiba.bitwise.dao.BitVectorDao#delete(int)
   */
  public void delete(Integer key) {
    Connection c = getConnection();
    PreparedStatement ps = null;
    try {
      ps = c.prepareStatement(DELETE_VECTOR);
      ps.setInt(1, key);
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
  public BitVectorDto load(Integer key) {
    Connection c = getConnection();
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = c.prepareStatement(GET_VECTOR);
      ps.setInt(1, key);
      rs = ps.executeQuery();
      if(rs.next()) {
        ByteBuffer bb = ByteBuffer.wrap(rs.getBytes(1));
        return binding.bufferToObject(key, bb);
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
  public void save(BitVectorDto v) {
    Connection c = getConnection();
    PreparedStatement ps = null;
    try {
      ps = c.prepareStatement(UPDATE_VECTOR);
      ByteBuffer bb = binding.objectToBuffer(v);
      ps.setBinaryStream(1, new ByteArrayInputStream(bb.array()), bb.position());
      ps.setInt(2, v.getId());
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

  static private class BitVectorDtoBinding implements ByteBufferBinding<Integer, BitVectorDto> {

    /*
     * @see com.sleepycat.bind.EntityBinding#entryToObject(com.sleepycat.je.DatabaseEntry, com.sleepycat.je.DatabaseEntry)
     */
    public BitVectorDto bufferToObject(Integer key, ByteBuffer entry) {
      BitVectorDto v = new BitVectorDto();
      v.setId(key);
      v.setSize(entry.getInt());
      v.setBits(BdbUtil.readLongArray(entry));
      return v;
    }

    /*
     * @see com.sleepycat.bind.EntityBinding#objectToData(java.lang.Object, com.sleepycat.je.DatabaseEntry)
     */
    public ByteBuffer objectToBuffer(BitVectorDto v) {
      int length = v.getBits().length;
      byte data[] = new byte[4 + 4 + length]; 
      ByteBuffer bb = ByteBuffer.wrap(data);
      bb.putInt(v.getSize());
      BdbUtil.putLongArray(v.getBits(), bb);
      return bb;
    }
  }
}
