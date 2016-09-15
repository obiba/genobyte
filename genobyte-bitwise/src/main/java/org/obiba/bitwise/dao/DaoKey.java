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

/**
 * Placeholder class used to key different DAO implementations.
 * <p>
 * Keyed DAOs allows having many DAO implementations of the same DAO interface but that work on different sets of
 * data. The DaoKey is used to partition the different DAO instances. Its value can be anything that is
 * meaningful to the Dao implementation.
 *
 * @author plaflamm
 */
public class DaoKey {

  private String key_ = null;

  public DaoKey(String key) {
    key_ = key.intern();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof DaoKey) {
      return key_ == ((DaoKey) o).key_;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return key_.hashCode();
  }

  public String toString() {
    return key_;
  }
}
