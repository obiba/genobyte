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
package org.obiba.bitwise.dao.impl.jdbm;

import java.io.IOException;
import java.util.Comparator;

import jdbm.btree.BTree;
import jdbm.helper.Serializer;

import org.obiba.bitwise.dao.DaoKey;
import org.obiba.bitwise.dao.impl.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibatis.dao.client.DaoManager;

abstract class BaseBTreeJdbmDaoImpl<T, K> extends BaseRecordManagerDaoImpl<T> {

  static final Logger log = LoggerFactory.getLogger(BaseBTreeJdbmDaoImpl.class);

  private BTree btree = null;

  public BaseBTreeJdbmDaoImpl(DaoManager mgr) {
    super(mgr);
  }

  @Override
  public void setDaoKey(DaoKey key) {
    super.setDaoKey(key);
    if(enableTiming_ && timer_ == null) {
      timer_ = new Timer(this.getClass().getName(), key.toString());
    }
  }

  public void create(T value) {
    if(enableTiming_) timer_.start();
    K key = getKey(value);
    try {
      getBtree().insert(key, value, false);
    } catch (IOException e) {
      throw new JdbmRuntimeException(e);
    }
    if(enableTiming_) timer_.end();
  }

  public void delete(K name) {
    if(enableTiming_) timer_.start();
    try {
      getBtree().remove(name);
    } catch (IOException e) {
      throw new JdbmRuntimeException(e);
    }
    if(enableTiming_) timer_.end();
  }

  @SuppressWarnings("unchecked")
  public T load(K key) {
    if(enableTiming_) timer_.start();
    T t;
    try {
      t = (T)getBtree().find(key);
    } catch (IOException e) {
      throw new JdbmRuntimeException(e);
    }
    if(enableTiming_) timer_.end();
    return t;
  }

  public void save(T value) {
    if(enableTiming_) timer_.start();
    K key = getKey(value);
    try {
      getBtree().insert(key, value, true);
    } catch (IOException e) {
      throw new JdbmRuntimeException(e);
    }
    if(enableTiming_) timer_.end();
  }

  protected BTree getBtree() {
    if(btree == null) {
      synchronized(this) {
        try {
          // Test again due to possible race condition
          if(btree == null) {
            // If the manager already exists, we must call the load method, not the create method
            if(managerExists() == false) {
              log.debug("RecordManager {} does not exist, creating new BTree instance.", getManagerName());
              btree = BTree.createInstance(getManager(), getKeyComparator(), getKeySerializer(), getValueSerializer());
              log.debug("BTree record id {}", btree.getRecid());
              // Store the root's record ID under a known key so we can load it back...
              getManager().setNamedObject("myBtreeRoot", btree.getRecid());
            } else {
              log.debug("RecordManager {} exists, loading BTree instance.", getManagerName());
              btree = BTree.load(getManager(), getManager().getNamedObject("myBtreeRoot"));
            }
          }
          return btree;
        } catch (IOException e) {
          throw new JdbmRuntimeException(e);
        }
      }
    }
    return btree;
  }

  abstract protected K getKey(T value);

  abstract protected Comparator<?> getKeyComparator();

  abstract protected Serializer getKeySerializer();

  abstract protected Serializer getValueSerializer();

}
