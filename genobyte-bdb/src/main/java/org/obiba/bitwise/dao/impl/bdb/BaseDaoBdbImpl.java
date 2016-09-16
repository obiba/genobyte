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
import org.obiba.bitwise.dao.DaoKey;
import org.obiba.bitwise.dao.KeyedDao;

public class BaseDaoBdbImpl implements KeyedDao {

  protected DaoKey key_ = null;

  protected BaseDaoBdbImpl(DaoManager mgr) {
  }

  protected BdbContext getContext() {
    return BdbContext.getInstance(key_);
  }

  public void setDaoKey(DaoKey key) {
    key_ = key;
  }

}
