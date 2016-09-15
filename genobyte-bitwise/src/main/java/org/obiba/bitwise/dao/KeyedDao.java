/*******************************************************************************
 * Copyright 2007(c) Genome Quebec. All rights reserved.
 * <p>
 * This file is part of GenoByte.
 * <p>
 * GenoByte is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * <p>
 * GenoByte is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.obiba.bitwise.dao;

import com.ibatis.dao.client.Dao;

/**
 * Base interface for DAOs that use a DaoKey to partition the data to be accessed.
 *
 * Keyed DAOs allows having many DAO implementations of the same DAO interface but that work on different sets of
 * data. The DaoKey is used to partition the different DAO instances. Its value can be anything that is 
 * meaningful to the Dao implementation.
 *
 * @author plaflamm
 */
public interface KeyedDao extends Dao {

  /**
   * Set the <code>DaoKey</code> of this DAO instance.
   * @param key the <code>DaoKey</code>
   */
  public void setDaoKey(DaoKey key);

}
