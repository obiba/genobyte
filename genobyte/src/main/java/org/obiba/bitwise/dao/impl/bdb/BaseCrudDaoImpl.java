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
package org.obiba.bitwise.dao.impl.bdb;

import org.obiba.bitwise.dao.DaoKey;

import com.ibatis.dao.client.DaoManager;
import com.sleepycat.collections.StoredMap;

abstract class BaseCrudDaoImpl<T, K> extends BaseDaoBdbImpl {

  protected boolean enableTiming_ = false;
  protected Timer timer_ = null;

  private StoredMap entityMap_ = null;

  public BaseCrudDaoImpl(DaoManager mgr) {
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
    if(getMap().containsKey(key) == true) {
      throw new IllegalArgumentException("Key ["+key+"] already exists.");
    }
    getMap().put(key, value);
    if(enableTiming_) timer_.end();
  }

  public void delete(K name) {
    if(enableTiming_) timer_.start();
    getMap().remove(name);
    if(enableTiming_) timer_.end();
  }

  @SuppressWarnings("unchecked")
  public T load(K name) {
    if(enableTiming_) timer_.start();
    T t = (T)getMap().get(name);
    if(enableTiming_) timer_.end();
    return t;
  }

  public void save(T value) {
    if(enableTiming_) timer_.start();
    K key = getKey(value);
    getMap().put(key, value);
    if(enableTiming_) timer_.end();
  }

  synchronized protected StoredMap getMap() {
    if(entityMap_ == null) {
      entityMap_ = createStoredMap();
    }
    return entityMap_;
  }

  abstract protected K getKey(T value);

  abstract protected StoredMap createStoredMap();

}
