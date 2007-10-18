/*******************************************************************************
 * Copyright 2007(c) G�nome Qu�bec. All rights reserved.
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
package org.obiba.bitwise;

import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.obiba.bitwise.dao.BitwiseStoreDtoDao;
import org.obiba.bitwise.dao.DaoKey;
import org.obiba.bitwise.dao.KeyedDaoManager;
import org.obiba.bitwise.dto.BitwiseStoreDto;
import org.obiba.bitwise.schema.StoreSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.ibatis.dao.client.DaoManager;

/**
 * Offers various utility classes to manage all <tt>BitwiseStore</tt> instances.
 */

public class BitwiseStoreUtil {

  private final Logger log = LoggerFactory.getLogger(BitwiseStoreUtil.class);

  private static final BitwiseStoreUtil instance_ = new BitwiseStoreUtil();

  private static HashMap<String, BitwiseStoreRef> STORE_MAP = new HashMap<String, BitwiseStoreRef>();
  private static Set<String> LOCKS = new TreeSet<String>();
  private static Object MUTEX = new Object();

  private ConfigurationPropertiesProvider provider_ = null;

  private BitwiseStoreUtil() {
    
  }


  /**
   * Returns the <tt>BitwiseStoreUtil</tt> unique instance, used to manage stores.
   * @return The unique instance of this object.
   */
  public static BitwiseStoreUtil getInstance() {
    return instance_;
  }


  /**
   * Sets the object providing properties to the bitwise stores.
   * @param provider is the properties provider.
   */
  public void setConfigurationPropertiesProvider(ConfigurationPropertiesProvider provider) {
    provider_ = provider;
  }


  /**
   * Gets the object that has been set to provide properties to the bitwise stores.
   * @return The properties provider.
   */
  public ConfigurationPropertiesProvider getConfigurationPropertiesProvider() {
    return provider_;
  }


  /**
   * Gets the list of <tt>BitwiseStore</tt> instances currently opened.
   * @return the list of all opened instances of <tt>BitwiseStore</tt>. 
   */
  public Set<String> list() {
    return Collections.unmodifiableSet(STORE_MAP.keySet());
  }


  /**
   * Creates a new bitwise store.
   * @param name is the name of the newly create store.
   * @param schema is the data used to define the store, such as the fields and dictionaries.
   * @param capacity is the initial capacity of the store (in other words, how many records will be initially created with the store.)
   * @return The newly created bitwise store.
   */
  public BitwiseStore create(String name, StoreSchema schema, int capacity) {
      return create(name, schema, capacity, null);
  }


  /**
  * Creates a bitwise store with extra parameters that are unique to it.
  * @param name is the name of the newly create store.
  * @param schema is the data used to define the store, such as the fields and dictionaries.
  * @param capacity is the initial capacity of the store (in other words, how many records will be initially created with the store.)
  * @param specificProps the list of extra properties that are specific to this store.
  * @return The newly created bitwise store.
  */
  public BitwiseStore create(String name, StoreSchema schema, int capacity, Properties specificProps) {
    if(name == null) {
      throw new IllegalArgumentException("Store name cannot be null");
    }
    if(schema == null) {
      throw new IllegalArgumentException("Store schema cannot be null");
    }
    synchronized(MUTEX) {
      DaoKey key = new DaoKey(name);
      log.debug("Creating store [{}]", key);

      //Get default properties
      Properties p = null;
      if (provider_ != null) {
        p = provider_.loadProperties(name);
      }
      
      //If store-specific properties were defined, merge them with default properties
      //(giving priority to the store-specific ones).
      if (specificProps != null) {
        if (p == null) {        //There are no default properties, so use only the specific ones.    
          p = specificProps;
        }
        else {
          p.putAll(specificProps);
        }
      }
      
      DaoManager daoManager = KeyedDaoManager.getInstance(key);
      if(daoManager == null) {
        KeyedDaoManager.createInstance(key, p);
        daoManager = KeyedDaoManager.getInstance(key);
      }

      try {
        daoManager.startTransaction();
        BitwiseStoreDtoDao dao = (BitwiseStoreDtoDao)daoManager.getDao(BitwiseStoreDtoDao.class);
        BitwiseStoreDto data = new BitwiseStoreDto();
        data.setName(name);
        data.setCapacity(capacity);
        BitVector deleted = new BitVector(capacity);
        deleted.setAll();
        data.setDeleted(deleted);
        BitVector cleared = new BitVector(capacity);
        cleared.setAll();
        data.setCleared(cleared);
        data.setSchema(schema);
        dao.create(data);
        BitwiseStore store = new BitwiseStore(data);
        store.create(p);
        STORE_MAP.put(name, new BitwiseStoreRef(key));
  
        // Flush the store to write all created fields so that other threads that open the store will "see" the fields.
        store.flush();
  
        //Save specific properties if provided
        if (specificProps != null) {
          provider_.saveSpecificProperties(name, specificProps);
        }
        daoManager.commitTransaction();
        return store;
      } finally {
        daoManager.endTransaction();
      }
    }
  }


  /**
   * Opens a bitwise store. 
   * @param name is the name of the bitwise store to be opened.
   * @return The requested bitwise store.
   */
  public BitwiseStore open(String name) {
    synchronized(MUTEX) {
      while(LOCKS.contains(name)) {
        try {
          MUTEX.wait(10 * 1000);
        } catch(InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
      DaoKey key = new DaoKey(name);
      log.debug("Opening store [{}]",key);
      
      //Get properties used by iBatis and by Bitwise Store
      Properties p = null;
      if (provider_ != null) {
        p = provider_.loadProperties(name);
      }
      
      DaoManager daoManager = KeyedDaoManager.getInstance(key);
      if(daoManager == null) {
        KeyedDaoManager.createInstance(key, p);
        daoManager = KeyedDaoManager.getInstance(key);
      }
      BitwiseStore store = null;
      try {
        daoManager.startTransaction();
        BitwiseStoreDtoDao dao = (BitwiseStoreDtoDao)daoManager.getDao(BitwiseStoreDtoDao.class);
        // If the store does not exist, this method will return null
        BitwiseStoreDto data = dao.load(name);
        if(data != null) {
          store = new BitwiseStore(data);
          store.open(p);
        }
        daoManager.commitTransaction();
      } catch(RuntimeException e) {
        log.error("Fatal error opening store [{}]: {}", key, e.getMessage());
        store = null;
        throw e;
      } finally {
        daoManager.endTransaction();
        if(store == null) {
          // If we weren't able to open the store and no other reference exists, then we destroy the DaoManager
          if(STORE_MAP.containsKey(name) == false) {
            KeyedDaoManager.destroyInstance(key);
          }
        } else {
          // RefCount the store
          if(STORE_MAP.containsKey(name)) {
            STORE_MAP.get(name).inc();
          } else {
            STORE_MAP.put(name, new BitwiseStoreRef(key));
          }
        }
      }
      return store;
    }
  }


  /**
   * Verifies that store with the given pStoreName really exists on the server.
   * @param pStoreName
   * @return <B>true</B> if a store exists on the server.<BR>
   * <B>false</B> if it doesn't exist.
   */
  public boolean exists(String pStoreName) {
    BitwiseStore testedStore = this.open(pStoreName);
    boolean storeExists;
    if (testedStore != null) {
      storeExists = true;
      this.close(testedStore);
    }
    else {
      storeExists = false;
    }
    return storeExists;
  }


  /**
   * Closes an opened bitwise store.
   * @param store is the store to be closed.
   */
  public void close(BitwiseStore store) {
    synchronized(MUTEX) {
      if(STORE_MAP.get(store.getName()).dec() == false) {
        MUTEX.notify();
        return;
      }
      STORE_MAP.remove(store.getName());
      MUTEX.notify();
      KeyedDaoManager.destroyInstance(store.getDaoKey());
    }
  }


  //TODO: Better explain the behaviour of forceClose()
  /**
   * Forces a bitwise store to be closed.
   */
  public void forceClose(String name) {
    synchronized(MUTEX) {
      BitwiseStoreRef ref = STORE_MAP.remove(name);
      if(ref != null) {
        KeyedDaoManager.destroyInstance(ref.key_);
      }
      LOCKS.remove(name);
      MUTEX.notify();
    }
  }


  /**
   * Deletes permanently a bitwise store. For example, if the store was on disk, files will be deleted.
   * If the store was an SQL table, it will be removed.
   * @param store is the store to be deleted.
   */
  public void delete(BitwiseStore store) {
    synchronized(MUTEX) {
      DaoManager daoManager = KeyedDaoManager.getInstance(store.getDaoKey());
      BitwiseStoreDtoDao dao = (BitwiseStoreDtoDao)daoManager.getDao(BitwiseStoreDtoDao.class);
      dao.delete(store.getName());
      close(store);
    }
  }


  /**
   * Locks a <tt>BitwiseStore</tt> to prevent it from being accessed in the middle of a <tt>Runnable</tt> operation.
   * @param name the name of the store on which to put the lock.
   * @param r the code segment to execute in lock mode.
   */
  public void lock(String name, Runnable r) {
    synchronized(MUTEX) {
      BitwiseStoreRef ref = STORE_MAP.get(name);
      while( (ref != null && ref.refCount_ > 0) || LOCKS.contains(name) ) {
        try {
          ref = null;
          // Some other thread is holding a reference to the store. Wait for it.
          MUTEX.wait(10 * 1000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        ref = STORE_MAP.get(name);
      }
      log.debug("Store [{}] has been locked by thread [{}]", name, Thread.currentThread().getId());

      // We've locked the whole BitwiseStoreUtil, make it fast...
      LOCKS.add(name);
    }

    // The Store "name" is locked: no thread may open it.
    // Execute the Runnable within this restriction
    r.run();

    synchronized(MUTEX) {
      LOCKS.remove(name);
      MUTEX.notify();
    }
  }


  /**
   * Holds a reference to a specific <tt>BitwiseStore</tt> instance.
   */
  private class BitwiseStoreRef {
    int refCount_ = 0;
    private DaoKey key_ = null;

    BitwiseStoreRef(DaoKey dto) {
      key_ = dto;
      inc();
    }

    DaoKey inc() {
      refCount_++;
      log.debug("Store [{}] refCount if now [{}]", key_, refCount_);
      return key_;
    }

    boolean dec() {
      refCount_--;
      log.debug("Store [{}] refCount if now [{}]", key_, refCount_);
      return refCount_ == 0;
    }
  }
}
