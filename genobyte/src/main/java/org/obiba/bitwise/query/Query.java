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
package org.obiba.bitwise.query;

import org.obiba.bitwise.BitwiseStore;

/**
 * Provides a skeleton class to query types that can be run on a bitwise store.
 */
abstract public class Query {

  protected Query() {
    super();
  }


  /**
   * Runs this <tt>Query</tt> on the bitwise store.
   * @param store the store on which the query will be ran.
   * @return the query result.
   * @throws QueryExecutionException when something in the querying process went wrong.
   */
  abstract public QueryResult execute(BitwiseStore store) throws QueryExecutionException;


  /**
   * Gives information on this query in the context of a given bitwise store.
   * @param store the store on which the query explanation should be ran.
   * @return the <tt>String</tt> containing the information about this query in the context of the provided store.
   * @throws QueryExecutionException when some problem occured at the generation of the explanation string.
   */
  public String explain(BitwiseStore store) throws QueryExecutionException {
    return toString() + "{" + execute(store).count() + "}";
  }


  /**
   * Returns a query exception related to an error handling a field in the store. 
   * @param store the store on which the query was ran.
   * @param fieldName the field that is the source of the problem.
   * @return an exception that can be thrown by the calling method.
   */
  protected QueryExecutionException invalidField(BitwiseStore store, String fieldName) {
    return new UnknownFieldException(store.getName(), fieldName);
  }


  /**
   * Returns a query exception related to an error handling a field value in the store. 
   * @param store the store on which the query was ran.
   * @param fieldName the field where an invalid value was found.
   * @param value the field value that is the source of the problem.
   * @return an exception that can be thrown by the calling method.
   */
  protected QueryExecutionException invalidFieldValue(BitwiseStore store, String field, String value) {
    return new QueryExecutionException("Invalid value=["+value+"] for field name=["+field+"] in BitwiseStore=["+store.getName()+"]");
  }
}
