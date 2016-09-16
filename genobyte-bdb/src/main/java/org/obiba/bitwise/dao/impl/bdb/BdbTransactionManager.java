/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.obiba.bitwise.dao.impl.bdb;

import com.ibatis.dao.client.DaoTransaction;
import com.ibatis.dao.engine.transaction.DaoTransactionManager;
import com.sleepycat.collections.CurrentTransaction;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;
import org.obiba.bitwise.dao.DaoKey;

import java.util.Properties;

public class BdbTransactionManager implements DaoTransactionManager {

  private DaoKey key_ = null;

  private boolean isTransactional_ = false;

  private TransactionConfig txnCfg_ = null;

  public BdbTransactionManager() {
    super();
  }

  public void configure(Properties props) {
    try {
      key_ = new DaoKey((String) props.get(DaoKey.DAO_MANAGER_KEY));
      BdbContext.createInstance(key_, props);
      isTransactional_ = BdbContext.getInstance(key_).getEnvironment().getConfig().getTransactional();
      if (isTransactional_) {
        txnCfg_ = new TransactionConfig();
        txnCfg_.setNoSync(true);
        txnCfg_.setSync(false);
        txnCfg_.setWriteNoSync(true);
      }
    } catch (DatabaseException e) {
      throw new RuntimeException(e);
    }
  }

  public DaoTransaction startTransaction() {
    if (isTransactional_) {
      BdbContext bdbEnv = BdbContext.getInstance(key_);
      try {
        bdbEnv.getEnvironment().checkpoint(null);
        CurrentTransaction ct = CurrentTransaction.getInstance(bdbEnv.getEnvironment());
        Transaction t = ct.beginTransaction(txnCfg_);
        return new DaoTransactionBdbImpl(ct);
      } catch (DatabaseException e) {
        throw new RuntimeException(e);
      }
    }

    return new DaoTransactionNotTransactionalImpl();
  }

  public void commitTransaction(DaoTransaction transaction) {
    if (isTransactional_) {
      BdbContext bdbEnv = BdbContext.getInstance(key_);
      try {
        CurrentTransaction ct = CurrentTransaction.getInstance(bdbEnv.getEnvironment());
        ct.commitTransaction();
      } catch (DatabaseException e) {
        throw new RuntimeException(e);
      } finally {
      }
    }
  }

  public void rollbackTransaction(DaoTransaction transaction) {
    if (isTransactional_) {
      BdbContext bdbEnv = BdbContext.getInstance(key_);
      try {
        CurrentTransaction ct = CurrentTransaction.getInstance(bdbEnv.getEnvironment());
        ct.abortTransaction();
      } catch (DatabaseException e) {
        throw new RuntimeException(e);
      } finally {
      }
    }
  }

  static private class DaoTransactionBdbImpl implements DaoTransaction {
    // Keep a reference to CurrentTransaction instead of Transaction.
    // This is due to a bug in JE: http://forums.oracle.com/forums/thread.jspa?threadID=553351&tstart=0
    // This prevents the GC from collecting the CurrentTransaction instance.
    private CurrentTransaction tr_ = null;

    DaoTransactionBdbImpl(CurrentTransaction t) {
      tr_ = t;
    }
  }

  static private class DaoTransactionNotTransactionalImpl implements DaoTransaction {
  }

}
