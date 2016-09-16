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

import org.obiba.bitwise.dao.KeyedDao;

/**
 * Defines Create, Read (load), Update (save) and Delete DAO methods for any type.
 *
 * @param <T> the type of the transfer object to be persisted
 * @param <K> the type of the primary key of the persisted transfer object
 */
public interface CrudDao<T, K> extends KeyedDao {
  /**
   * Persists a new instance of T. Calling this method will set the
   * transfer objects primary if it is auto-generated. Otherwise
   * it will use the value specified in the transfer object itself.
   *
   * @param value the transfer object to persist.
   * @throws IllegalArgumentException when the transfer object's unique key already exists
   */
  void create(T value);

  /**
   * Loads a transfer object based on its unique key. If the object does not
   * exist, null is returned.
   *
   * @param key the unique key of the transfer object to load.
   * @return the transfer object or null if no entry exists for the specified key.
   */
  T load(K key);

  /**
   * Persits an existing transfer object. Calling this method overrides the value associated with
   * the current unique key.
   *
   * @param value the transfer object to persist.
   */
  void save(T value);

  /**
   * Deletes an existing transfer object.
   *
   * @param key the unique key of the transfer object to remove.
   */
  void delete(K key);
}
