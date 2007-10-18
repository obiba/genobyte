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
package org.obiba.bitwise.mock.query;

import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.query.Query;
import org.obiba.bitwise.query.QueryExecutionException;
import org.obiba.bitwise.query.QueryResult;


public class MockWildcardQuery extends Query implements MockQuery {

  private String field_ = null;
  private String valuePre_ = null;
  private String valuePost_ = null;

  public MockWildcardQuery(String field, String valuePre, String valuePost) {
    super();
    field_ = field;
    valuePre_ = valuePre;
    valuePost_ = valuePost;
  }

  @Override
  public QueryResult execute(BitwiseStore store) throws QueryExecutionException {
    return null;
  }
  
  public String parsable() {
    return field_ +":"+valuePre_+"*"+valuePost_;
  }
  
  public String toString() {
    return parsable();
  }

  @Override
  /**
   * For the two objects to be equal:
   *    1- The field name must be the same
   *    2- The valuePre of both must be null or equal
   *    3- The valuePost of both must be null or equal
   */
  public boolean equals(Object o) {
    if(o instanceof MockWildcardQuery) {
      MockWildcardQuery rhs = (MockWildcardQuery)o;
      
      boolean preEqual = false;
      if (valuePre_ == null) {
        preEqual = (rhs.valuePre_ == null);
      }
      else {
        preEqual = valuePre_.equals(rhs.valuePre_);
      }
      
      boolean postEqual = false;
      if (valuePost_ == null) {
        postEqual = (rhs.valuePost_ == null);
      }
      else {
        postEqual = valuePost_.equals(rhs.valuePost_);
      }
      
      return field_.equals(rhs.field_)
        && preEqual
        && postEqual;
    }
    return super.equals(o);
  }
}
