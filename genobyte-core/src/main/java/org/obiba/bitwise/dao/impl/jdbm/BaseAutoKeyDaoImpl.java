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

import jdbm.helper.Serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibatis.dao.client.DaoManager;

abstract class BaseAutoKeyDaoImpl<T> extends BaseRecordManagerDaoImpl<T> {

  static final Logger log = LoggerFactory.getLogger(BaseAutoKeyDaoImpl.class);

  public BaseAutoKeyDaoImpl(DaoManager mgr) {
    super(mgr);
  }

  abstract protected Serializer getValueSerializer();

  abstract protected long getKey(T value);

  abstract protected void setAutoKey(long key, T value);

  @SuppressWarnings("unchecked")
  public void create(T v) {
    try {
      long id = getManager().insert(v, getValueSerializer());
      if(id <= 0 ) log.error("Invalid key returned after creating record {}", id);
      setAutoKey(id, v);
    } catch (IOException e) {
      throw new JdbmRuntimeException(e);
    }
  }

  public void delete(Long key) {
    if(enableTiming_) timer_.start();
    try {
      getManager().delete(key);
    } catch (IOException e) {
      throw new JdbmRuntimeException(e);
    }
    if(enableTiming_) timer_.end();
  }

  @SuppressWarnings("unchecked")
  public T load(Long key) {
    if(enableTiming_) timer_.start();
    T t;
    try {
      t = (T)getManager().fetch(key, getValueSerializer());
      if(t != null) setAutoKey(key, t);
    } catch (IOException e) {
      throw new JdbmRuntimeException(e);
    }
    if(enableTiming_) timer_.end();
    return t;
  }

  public void save(T value) {
    if(enableTiming_) timer_.start();
    long key = getKey(value);
    try {
      getManager().update(key, value, getValueSerializer());
    } catch (IOException e) {
      throw new JdbmRuntimeException(e);
    }
    if(enableTiming_) timer_.end();
  }

}
