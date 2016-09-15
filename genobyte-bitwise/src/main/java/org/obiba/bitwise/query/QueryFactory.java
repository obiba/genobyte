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

import org.obiba.bitwise.query.BooleanQuery.Operator;
import org.obiba.bitwise.query.sort.Sort;

/**
 * Prepares query objects based on a parsed query <tt>String</tt>.
 */
public interface QueryFactory {

  /**
   * Prepares a query to search on an exact field value.
   *
   * @param field the field on which the search will be ran.
   * @param value the value to look for.
   * @return the <tt>Query</tt> object that will be able to run that query.
   */
  public Query getFieldValueQuery(String field, String value);

  /**
   * Prepares a query to search on a partial field value, allowing the use of a wildcard in the search.
   *
   * @param field     the field on which the search will be ran.
   * @param valuePre  the part of the field value coming before the wildcard.
   * @param valuePost the part of the field value coming after the wildcard.
   * @return the <tt>Query</tt> object that will be able to run that query.
   */
  public Query getWildcardQuery(String field, String valuePre, String valuePost);

  /**
   * Prepares a query to search on a range of values. Bounds are inclusive in the search.
   *
   * @param field the field on which the search will be ran.
   * @param from  the lower bound of the search range.
   * @param to    the upper bound of the search range.
   * @return the <tt>Query</tt> object that will be able to run that query.
   */
  public Query getRangeQuery(String field, String from, String to);

  /**
   * Prepares a query that will run two subqueries and join their result with a logical operator.
   *
   * @param op    the logical operator to join the result of the two subqueries.
   * @param left  the subquery to the left of the logical operator.
   * @param right the subquery to the right of the logical operator.
   * @return the <tt>Query</tt> object that will be able to run that query.
   */
  public Query getBooleanQuery(Operator op, Query left, Query right);

  /**
   * Prepares a query whose result will be the records that do not fit the condition of the subquery given in parameter.
   *
   * @param q the subquery that identifies records not wanted in the result set.
   * @return the <tt>Query</tt> object that will be able to run that query.
   */
  public Query getNotQuery(Query q);

  /**
   * Prepares a query that will compare the content of two fields and find the records for which the value of these
   * two fields differ.
   *
   * @param field1 the first field in the comparison.
   * @param field2 the second field in the comparison.
   * @return the <tt>Query</tt> object that will be able to run that query.
   */
  public Query getDiffQuery(String field1, String field2);

  /**
   * Prepares a query whose result will be sorted by a given set of criteria.
   *
   * @param q    the query whose resultset will be sorted.
   * @param sort the sort criteria to apply to the resultset.
   * @return the <tt>Query</tt> object that will be able to run that query.
   */
  public Query getSortClause(Query q, Sort sort);

}
