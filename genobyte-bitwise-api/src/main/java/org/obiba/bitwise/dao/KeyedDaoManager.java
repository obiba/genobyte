/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.obiba.bitwise.dao;

import com.ibatis.dao.client.Dao;
import com.ibatis.dao.client.DaoTransaction;
import org.obiba.bitwise.ConfigurationPropertiesProvider;
import org.obiba.bitwise.dao.spi.BitwiseDaoManagerBuilder;

import java.util.*;

/**
 * Utility class to manage instances of com.ibatis.dao.client.DaoManager based on a <tt>DaoKey</tt>.
 * <p>
 * Each DaoKey has a com.ibatis.dao.client.DaoManager associated. This allows creating, accessing and destroying
 * these instances.
 */
public class KeyedDaoManager {

  private static Map<DaoKey, KeyedDaoManagerImpl> instanceMap_ = new HashMap<>();

  private static Map<DaoKey, List<KeyedDaoManagerDestroyListener>> destroyListener_
      = new HashMap<>();

  private KeyedDaoManager() {
    super();
  }

  static public void createInstance(DaoKey key, Properties p) {
    Properties local = p == null ? new Properties() : p;
    local.setProperty(DaoKey.DAO_MANAGER_KEY, key.toString());
    com.ibatis.dao.client.DaoManager instance = BitwiseDaoManagerBuilder.build(local);
    String context = p.getProperty(ConfigurationPropertiesProvider.BITWISE_DAO_IMPL);
    if (context == null || context.length() == 0) {
      throw new IllegalArgumentException(
          ConfigurationPropertiesProvider.BITWISE_DAO_IMPL + " property must be set.");
    }
    instanceMap_.put(key, new KeyedDaoManagerImpl(key, context, instance));
  }

  static public com.ibatis.dao.client.DaoManager getInstance(DaoKey key) {
    return instanceMap_.get(key);
  }

  static public void destroyInstance(DaoKey key) {
    if (instanceMap_.containsKey(key)) {
      instanceMap_.remove(key);
      if (destroyListener_.containsKey(key)) {
        destroyListener_.remove(key).forEach(KeyedDaoManagerDestroyListener::destroying);
      }
    }
  }

  static public void addDestroyListener(DaoKey key, KeyedDaoManagerDestroyListener listener) {
    if (!destroyListener_.containsKey(key)) {
      destroyListener_.put(key, new LinkedList<>());
    }
    destroyListener_.get(key).add(listener);
  }

  static private class KeyedDaoManagerImpl implements com.ibatis.dao.client.DaoManager {

    DaoKey key_ = null;

    String context = null;

    com.ibatis.dao.client.DaoManager impl_ = null;

    private KeyedDaoManagerImpl(DaoKey key, String ctx, com.ibatis.dao.client.DaoManager impl) {
      key_ = key;
      context = ctx;
      impl_ = impl;
    }

    /*
     * @see com.ibatis.dao.client.DaoManager#commitTransaction()
     */
    public void commitTransaction() {
      impl_.commitTransaction();
    }

    /*
     * @see com.ibatis.dao.client.DaoManager#endTransaction()
     */
    public void endTransaction() {
      impl_.endTransaction();
    }

    /*
     * @see com.ibatis.dao.client.DaoManager#getDao(java.lang.Class, java.lang.String)
     */
    public Dao getDao(Class iface, String contextId) {
      KeyedDao impl = (KeyedDao) impl_.getDao(iface, contextId);
      impl.setDaoKey(key_);
      return impl;
    }

    /*
     * @see com.ibatis.dao.client.DaoManager#getDao(java.lang.Class)
     */
    public Dao getDao(Class type) {
      return getDao(type, this.context);
    }

    /*
     * @see com.ibatis.dao.client.DaoManager#getTransaction(com.ibatis.dao.client.Dao)
     */
    public DaoTransaction getTransaction(Dao dao) {
      return impl_.getTransaction(dao);
    }

    /*
     * @see com.ibatis.dao.client.DaoManager#startTransaction()
     */
    public void startTransaction() {
      impl_.startTransaction();
    }
  }
}
