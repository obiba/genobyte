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

import org.obiba.bitwise.query.BooleanQuery.Operator;
import org.obiba.bitwise.query.sort.Sort;


class DefaultQueryFactory implements QueryFactory {

  public DefaultQueryFactory() {
    super();
  }

  public Query getFieldValueQuery(String field, String value) {
    return new FieldValueQuery(field, value);
  }
  
  public Query getWildcardQuery(String field, String valuePre, String valuePost) {
    return new WildcardQuery(field, valuePre, valuePost);
  }

  public Query getRangeQuery(String field, String from, String to) {
    return new RangeQuery(field, from, to);
  }

  public Query getBooleanQuery(Operator op, Query left, Query right) {
    return new BooleanQuery(op, left, right);
  }

  public Query getNotQuery(Query q) {
    return new NotQuery(q);
  }

  public Query getDiffQuery(String field1, String field2) {
    return new DiffQuery(field1, field2);
  }

  public Query getSortClause(Query q, Sort sort) {
    return new SortClause(q, sort);
  }
}
