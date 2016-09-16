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
 * Error that occurs when the execution of a query went wrong at some point.
 */
public class QueryExecutionException extends RuntimeException {

  private static final long serialVersionUID = 6810531392100211297L;

  public QueryExecutionException() {
    super();
  }

  public QueryExecutionException(String arg0) {
    super(arg0);
  }

  public QueryExecutionException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

  public QueryExecutionException(Throwable arg0) {
    super(arg0);
  }

}
