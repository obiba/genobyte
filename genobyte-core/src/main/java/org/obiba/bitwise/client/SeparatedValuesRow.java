/*******************************************************************************
 * Copyright 2007(c) Génome Québec. All rights reserved.
 * 
 * This file is part of GenoByte.
 * 
 * GenoByte is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * GenoByte is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 *******************************************************************************/
package org.obiba.bitwise.client;

/**
 * A row in a Separated Value file
 * 
 * @author plaflamm
 *
 * <pre>
 * Date       Author      Changes
 * 21/02/2005 plaflamm    Creation
 * </pre>
 */
public interface SeparatedValuesRow {
  
  /**
   * The index of the row in the file (0 based)
   * @return the row's index in the file
   */
  public int getIndex();

  public int getColumnCount();

  /**
   * Return the value of a column of the row using the specified type. The
   * specified type must provide a constructor with a sole String argument.
   * 
   * @param index the 0 based column index
   * @param type the type of the object to instantiate.
   * @return the column's value
   */
  public <T> T getColumnValue(int index, Class<T> type);
  
}
