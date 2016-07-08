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
package org.obiba.bitwise.query;

/**
 * When a given field does not exist in the bitwise store being queried, this exception will be thrown. 
 */
public class UnknownFieldException extends QueryExecutionException {

  private static final long serialVersionUID = -6528504672303818999L;

  private String storeName_ = null;

  private String fieldName_ = null;

  public UnknownFieldException(String storeName, String name) {
    super("Field [" + name + "] does not exist in store [" + storeName + "]");
    storeName_ = storeName;
    fieldName_ = name;
  }

  public UnknownFieldException(String storeName, String name, String info) {
    super("Field [" + name + "] does not exist in store [" + storeName + "]: " + info);
    storeName_ = storeName;
    fieldName_ = name;
  }

  public String getFieldName() {
    return fieldName_;
  }

  public String getStoreName() {
    return storeName_;
  }

}
