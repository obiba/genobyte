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

  public static final String DAO_MANAGER_KEY = "_daoMgrKey_";

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
