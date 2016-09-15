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
  public void create(T value);

  /**
   * Loads a transfer object based on its unique key. If the object does not
   * exist, null is returned.
   *
   * @param key the unique key of the transfer object to load.
   * @return the transfer object or null if no entry exists for the specified key.
   */
  public T load(K key);

  /**
   * Persits an existing transfer object. Calling this method overrides the value associated with
   * the current unique key.
   *
   * @param value the transfer object to persist.
   */
  public void save(T value);

  /**
   * Deletes an existing transfer object.
   *
   * @param key the unique key of the transfer object to remove.
   */
  public void delete(K key);
}
