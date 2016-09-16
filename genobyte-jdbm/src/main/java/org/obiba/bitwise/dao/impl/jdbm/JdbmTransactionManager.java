/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.obiba.bitwise.dao.impl.jdbm;

import com.ibatis.dao.client.DaoTransaction;
import com.ibatis.dao.engine.transaction.DaoTransactionManager;
import org.obiba.bitwise.dao.DaoKey;

import java.util.Properties;

public class JdbmTransactionManager implements DaoTransactionManager {

  private DaoKey key = null;

  private boolean isTransactional_ = false;

  public JdbmTransactionManager() {
    super();
  }

  public void configure(Properties props) {
    key = new DaoKey((String) props.get(DaoKey.DAO_MANAGER_KEY));
    JdbmContext.createInstance(key, props);
  }

  public DaoTransaction startTransaction() {
    return new DaoTransactionJdbmImpl();
  }

  public void commitTransaction(DaoTransaction transaction) {
    JdbmContext.getInstance(key).commit();
    if (isTransactional_) {
    }
  }

  public void rollbackTransaction(DaoTransaction transaction) {
    JdbmContext.getInstance(key).rollback();
    if (isTransactional_) {
    }
  }

  static private class DaoTransactionJdbmImpl implements DaoTransaction {
    DaoTransactionJdbmImpl() {
    }
  }
}
