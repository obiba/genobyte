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

import com.ibatis.dao.client.DaoManager;
import com.sleepycat.collections.StoredMap;
import org.obiba.bitwise.dao.DaoKey;
import org.obiba.bitwise.dao.impl.util.Timer;

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
    if (enableTiming_ && timer_ == null) {
      timer_ = new Timer(this.getClass().getName(), key.toString());
    }
  }

  public void create(T value) {
    if (enableTiming_) timer_.start();
    K key = getKey(value);
    if (getMap().containsKey(key) == true) {
      throw new IllegalArgumentException("Key [" + key + "] already exists.");
    }
    getMap().put(key, value);
    if (enableTiming_) timer_.end();
  }

  public void delete(K name) {
    if (enableTiming_) timer_.start();
    getMap().remove(name);
    if (enableTiming_) timer_.end();
  }

  @SuppressWarnings("unchecked")
  public T load(K name) {
    if (enableTiming_) timer_.start();
    T t = (T) getMap().get(name);
    if (enableTiming_) timer_.end();
    return t;
  }

  public void save(T value) {
    if (enableTiming_) timer_.start();
    K key = getKey(value);
    getMap().put(key, value);
    if (enableTiming_) timer_.end();
  }

  synchronized protected StoredMap getMap() {
    if (entityMap_ == null) {
      entityMap_ = createStoredMap();
    }
    return entityMap_;
  }

  abstract protected K getKey(T value);

  abstract protected StoredMap createStoredMap();

}
