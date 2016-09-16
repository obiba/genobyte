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

abstract class BaseAutoKeyDaoImpl<T, K> extends BaseCrudDaoImpl<T, K> {

  public BaseAutoKeyDaoImpl(DaoManager mgr) {
    super(mgr);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void create(T v) {
    K k = (K) getMap().append(v);
    if (k == null) {
      throw new RuntimeException("Auto incremented key returned was null");
    }
    setAutoKey(k, v);
  }

  abstract protected void setAutoKey(K key, T value);

}
