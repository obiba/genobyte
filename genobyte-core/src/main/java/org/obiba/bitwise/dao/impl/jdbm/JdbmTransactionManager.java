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

import java.util.Properties;

import org.obiba.bitwise.dao.DaoKey;
import org.obiba.bitwise.dao.KeyedDaoManager;

import com.ibatis.dao.client.DaoTransaction;
import com.ibatis.dao.engine.transaction.DaoTransactionManager;

public class JdbmTransactionManager implements DaoTransactionManager {

  private DaoKey key = null;

  private boolean isTransactional_ = false;

  public JdbmTransactionManager() {
    super();
  }

  public void configure(Properties props) {
    key = new DaoKey((String) props.get(KeyedDaoManager.DAO_MANAGER_KEY));
    JdbmContext.createInstance(key, props);
  }

  public DaoTransaction startTransaction() {
    return new DaoTransactionJdbmImpl();
  }

  public void commitTransaction(DaoTransaction transaction) {
    JdbmContext.getInstance(key).commit();
    if(isTransactional_) {
    }
  }

  public void rollbackTransaction(DaoTransaction transaction) {
    JdbmContext.getInstance(key).rollback();
    if(isTransactional_) {
    }
  }

  static private class DaoTransactionJdbmImpl implements DaoTransaction {
    DaoTransactionJdbmImpl() {
    }
  }
}
