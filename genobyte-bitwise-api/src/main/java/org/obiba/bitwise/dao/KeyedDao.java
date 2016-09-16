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

/**
 * Base interface for DAOs that use a DaoKey to partition the data to be accessed.
 * <p>
 * Keyed DAOs allows having many DAO implementations of the same DAO interface but that work on different sets of
 * data. The DaoKey is used to partition the different DAO instances. Its value can be anything that is
 * meaningful to the Dao implementation.
 *
 * @author plaflamm
 */
public interface KeyedDao extends Dao {

  /**
   * Set the <code>DaoKey</code> of this DAO instance.
   *
   * @param key the <code>DaoKey</code>
   */
  void setDaoKey(DaoKey key);

}
