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

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.Dictionary;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.dictionary.WildcardDictionary;
import org.obiba.bitwise.util.BitVectorQueryResult;

/**
 * Runs query on a store's field value, allowing the use of a wildcard to the left, middle, or right of the searched value.
 */
public class WildcardQuery extends Query {

  private String field_ = null;

  private String valuePre_ = null;    //Value found before the wildcard

  private String valuePost_ = null;   //Value found after wildcard

  public WildcardQuery(String pField, String pValuePre, String pValuePost) {
    super();
    field_ = pField;
    valuePre_ = pValuePre;
    valuePost_ = pValuePost;
  }

  @Override
  public QueryResult execute(BitwiseStore store) throws QueryExecutionException {
    Field f = store.getField(field_);
    if(f == null) {
      throw invalidField(store, field_);
    }

    //Prepare the dictionary for value conversion
    Dictionary<Object> myDict = f.getDictionary();
    if(myDict instanceof WildcardDictionary == false) {
      // TODO
      throw new QueryExecutionException(
          "Invalid Dictionary type. Expected WildcardDictionary instance for dictionary [" + myDict.getName() + "]");
    }
    WildcardDictionary<Object> wildcardDict = (WildcardDictionary<Object>) myDict;

    //The vector containing all valid records to be returned
    BitVector resultVector = new BitVector(store.getCapacity());
    resultVector.setAll();
    resultVector.andNot(f.getNulls());
    BitVectorQueryResult result = new BitVectorQueryResult(resultVector);

    //Search on the value found before the wildcard
    if(valuePre_ != null) {
      Object valuePre = null;
      if(valuePre_ != null && valuePre_.equals("null") == false) {
        valuePre = f.getDictionary().convert(valuePre_);
      }
      BitVector encodedValuePre = wildcardDict
          .partialLookupLeft(valuePre);   //We don't want the partial query term to have an "End of string" character
      QueryResult preResult = f.query(encodedValuePre);
      result.and(preResult);
    }

    //Search on the value found after the wildcard
    if(valuePost_ != null) {
      Object valuePost = null;
      if(valuePost_ != null && valuePost_.equals("null") == false) {
        valuePost = f.getDictionary().convert(valuePost_);
      }
      BitVector encodedValuePost = wildcardDict.partialLookupRight(valuePost);
      QueryResult postResult = f.queryRight(encodedValuePost, wildcardDict.isVariableLength());
      result.and(postResult);
    }

    return result;
  }

  /**
   * Creates a <tt>String</tt> representation of this query.
   */
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(field_).append("=[").append(valuePre_).append("*").append(valuePost_).append("]");
    return sb.toString();
  }

}
