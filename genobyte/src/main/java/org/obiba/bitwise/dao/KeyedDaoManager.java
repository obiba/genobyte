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
package org.obiba.bitwise.dao;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.ibatis.dao.client.Dao;
import com.ibatis.dao.client.DaoManagerBuilder;
import com.ibatis.dao.client.DaoTransaction;

/**
 * Utility class to manage instances of com.ibatis.dao.client.DaoManager based on a <tt>DaoKey</tt>.
 * 
 * Each DaoKey has a com.ibatis.dao.client.DaoManager associated. This allows creating, accessing and destroying
 * these instances.
 */
public class KeyedDaoManager {

  public static final String DAO_MANAGER_KEY = "_daoMgrKey_";

  static Map<DaoKey, KeyedDaoManagerImpl> instanceMap_ = new HashMap<DaoKey, KeyedDaoManagerImpl>();
  static Map<DaoKey, List<KeyedDaoManagerDestroyListener>> destroyListener_ = new HashMap<DaoKey, List<KeyedDaoManagerDestroyListener>>();

  private KeyedDaoManager() {
    super();
  }

  static public void createInstance(DaoKey key, Properties p) {
    Properties local = null;
    if(p != null) {
      local = new Properties(p);
    } else {
      local = new Properties();
    }
    Reader config = new InputStreamReader(KeyedDaoManager.class.getResourceAsStream("conf/ibatis-dao-config.xml"));
    local.setProperty(DAO_MANAGER_KEY, key.toString());
    com.ibatis.dao.client.DaoManager instance = DaoManagerBuilder.buildDaoManager(config, local);
    instanceMap_.put(key, new KeyedDaoManagerImpl(key, instance));
  }

  static public com.ibatis.dao.client.DaoManager getInstance(DaoKey key) {
    return instanceMap_.get(key);
  }

  static public void destroyInstance(DaoKey key) {
    if(instanceMap_.containsKey(key)) {
      instanceMap_.remove(key);
      for (KeyedDaoManagerDestroyListener listener : destroyListener_.remove(key)) {
        listener.destroying();
      }
    }
  }

  static public void addDestroyListener(DaoKey key, KeyedDaoManagerDestroyListener listener) {
    if(destroyListener_.containsKey(key) == false) {
      destroyListener_.put(key, new LinkedList<KeyedDaoManagerDestroyListener>());
    }
    destroyListener_.get(key).add(listener);
  }

  static private class KeyedDaoManagerImpl implements com.ibatis.dao.client.DaoManager {

    DaoKey key_ = null;
    com.ibatis.dao.client.DaoManager impl_ = null;

    private KeyedDaoManagerImpl(DaoKey key, com.ibatis.dao.client.DaoManager impl) {
      key_ = key;
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
      KeyedDao impl = (KeyedDao)impl_.getDao(iface, contextId);
      impl.setDaoKey(key_);
      return impl;
    }
    /*
     * @see com.ibatis.dao.client.DaoManager#getDao(java.lang.Class)
     */
    public Dao getDao(Class type) {
      KeyedDao impl = (KeyedDao)impl_.getDao(type);
      impl.setDaoKey(key_);
      return impl;
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
