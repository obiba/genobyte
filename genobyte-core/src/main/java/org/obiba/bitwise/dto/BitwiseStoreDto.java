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
package org.obiba.bitwise.dto;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.schema.StoreSchema;

/**
 * Transfer object for a <tt>BitwiseStore</tt>.
 *
 * Allows persisting a <tt>BitwiseStore</tt> object through a BitwiseStoreDao implementation.
 */
public class BitwiseStoreDto {

  /** The BitwiseStore's unique name */
  private String name_ = null;

  /** The maximum number of records this instance may hold */
  private int capacity_ = -1;

  /** A vector of deleted records */
  private BitVector deleted_ = null;

  /** A vector of cleared records (deleted records for which the value of all of its fields have been re-initialized) */
  private BitVector cleared_ = null;

  /** The BitwiseStore's schema */
  private StoreSchema schema_ = null;

  /**
   * Contructs a transfer object with the specified values.
   *
   * @param name the unique name
   * @param capacity the capacity
   */
  public BitwiseStoreDto(String name, int capacity) {
    super();
    name_ = name;
    capacity_ = capacity;
  }

  /**
   * Constructs an empty transfer object.
   */
  public BitwiseStoreDto() {
    super();
  }

  /**
   * @return the capacity.
   */
  public int getCapacity() {
    return capacity_;
  }

  /**
   * @return the cleared.
   */
  public BitVector getCleared() {
    return cleared_;
  }

  /**
   * @return the deleted.
   */
  public BitVector getDeleted() {
    return deleted_;
  }

  /**
   * @return the name.
   */
  public String getName() {
    return name_;
  }

  /**
   * @return the schema.
   */
  public StoreSchema getSchema() {
    return schema_;
  }

  /**
   * @param capacity the capacity to set.
   */
  public void setCapacity(int capacity) {
    capacity_ = capacity;
  }

  /**
   * @param cleared the cleared to set.
   */
  public void setCleared(BitVector cleared) {
    cleared_ = cleared;
  }

  /**
   * @param deleted the deleted to set.
   */
  public void setDeleted(BitVector deleted) {
    deleted_ = deleted;
  }

  /**
   * @param name the name to set.
   */
  public void setName(String name) {
    name_ = name;
  }

  /**
   * @param schema the schema to set.
   */
  public void setSchema(StoreSchema schema) {
    schema_ = schema;
  }

}
