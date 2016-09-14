/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.bitwise.dao.impl.lmdb;

import com.ibatis.dao.client.DaoTransaction;
import com.ibatis.dao.engine.transaction.DaoTransactionManager;
import org.fusesource.lmdbjni.Transaction;
import org.obiba.bitwise.dao.DaoKey;
import org.obiba.bitwise.dao.KeyedDaoManager;
import org.obiba.bitwise.dao.impl.jdbm.JdbmContext;

import java.util.Properties;

public class LmdbTransactionManager implements DaoTransactionManager {

  private DaoKey key = null;

  @Override
  public void configure(Properties properties) {
    key = new DaoKey((String) properties.get(KeyedDaoManager.DAO_MANAGER_KEY));
    LmdbContext.createInstance(key, properties);
  }

  @Override
  public DaoTransaction startTransaction() {
    return new DaoTransactionLmdbImpl(LmdbContext.getInstance(key).createTransaction());
  }

  @Override
  public void commitTransaction(DaoTransaction daoTransaction) {
    ((DaoTransactionLmdbImpl) daoTransaction).getTransaction().commit();
  }

  @Override
  public void rollbackTransaction(DaoTransaction daoTransaction) {
    ((DaoTransactionLmdbImpl) daoTransaction).getTransaction().abort();
  }

  static private class DaoTransactionLmdbImpl implements DaoTransaction {

    private Transaction transaction;

    DaoTransactionLmdbImpl(Transaction transaction) {
      this.transaction = transaction;
    }

    public Transaction getTransaction() {
      return transaction;
    }
  }
}
