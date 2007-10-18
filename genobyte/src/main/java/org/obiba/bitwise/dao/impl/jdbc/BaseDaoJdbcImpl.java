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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.obiba.bitwise.dao.DaoKey;
import org.obiba.bitwise.dao.KeyedDao;


import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.JdbcDaoTemplate;

public class BaseDaoJdbcImpl extends JdbcDaoTemplate implements KeyedDao {

  private static final String GET_KEY = "SELECT LAST_INSERT_ID()";

  protected DaoKey key_ = null;

  public BaseDaoJdbcImpl(DaoManager daoManager) {
    super(daoManager);
  }

  public void setDaoKey(DaoKey key) {
    key_ = key;
  }
  
  public void create() {
    
  }
  
  public Integer getAutoKey(Connection c) {
    Statement s = null;
    ResultSet rs = null;
    try {
      s = c.createStatement();
      rs = s.executeQuery(GET_KEY);
      if(rs.next()) {
        return rs.getInt(1);
      }
      return null;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        if(s != null) s.close(); s = null;
        if(rs != null) rs.close(); rs = null;
      } catch (SQLException e) {
      }
    }
  }
}
