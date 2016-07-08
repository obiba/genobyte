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
package org.obiba.bitwise;

import org.obiba.bitwise.query.QueryResult;

class ResultVector implements QueryResult {

  BitVector result_ = null;
  
  BitVector filter_ = null;
  boolean filtered_ = false;    //Have we already removed filtered records from result_ vector?
  
  BitVector deleted_ = null;
  boolean deletedClean_ = false;  //Have we already removed deleted records from result_ vector?
  
  int[] hits_ = null;

  ResultVector(BitVector result, BitVector filter, BitVector deleted) {
    super();
    result_ = result;
    filter_ = filter;
    deleted_ = deleted;
  }

  ResultVector(ResultVector template, BitVector result) {
    result_ = result;
    filter_ = template.filter_;
    filtered_ = template.filtered_;
    deleted_  = template.deleted_;
    deletedClean_ = template.deletedClean_;
  }

  ResultVector(ResultVector result) {
    result_ = new BitVector(result.result_);
    filter_ = result.filter_;
    filtered_ = result.filtered_;
    deleted_ = result.deleted_;
    deletedClean_ = result.deletedClean_;
  }

  public BitVector bits() {
    cleanDeleted();
    filter();
    return result_;
  }
  
  public BitVector getFilter() {
    return filter_;
  }

  public QueryResult copy() {
    return new ResultVector(this); 
  }

  public int hit(int index) {
    if(hits_ == null) {
      makeHits();
    }
    return hits_[index];
  }

  public int next(int index) {
    filter();
    return result_.nextSetBit(index);
  }

  public boolean get(int index) {
    filter();
    return result_.get(index);
  }

  public int count() {
    filter();
    return result_.count();
  }

  public QueryResult not() {
    filtered_ = false;
    result_.not();
    return this;
  }

  public QueryResult and(QueryResult r) {
    filter();
    filtered_ = false;
    deletedClean_ = false;
    
    //Filter is true in one of the following cases:
    //  1-One operand is true and the other is filtered
    //  2-Both operands are filtered    
    //newFilter = (F1 AND F2) OR (F1 AND R2) OR (R1 AND F2)
    //can be simplified to
    //newFilter = (F1 AND (F2 OR R2)) OR (R1 AND F2)
    BitVector part1 = new BitVector(r.getFilter()).or(r.bits()).and(filter_);   //F1 AND (F2 OR R2)
    BitVector part2 = new BitVector(result_).and(r.getFilter());    //R1 AND F2
    part1.or(part2);
    filter_ = part1;
    
    result_.and(r.bits());
    return this;
  }

  public QueryResult andNot(QueryResult r) {
    filter();
    filtered_ = false;
    deletedClean_ = false;
    
    //AndNot operator results differ depending on which vector is applied to the left or right of
    //the operator. There are two situations where we can have any filter bits set to one and have a
    //result filter that is not set to one:
    //1-The left operand is false.
    //2-The right operand is true.
    //Note: In both cases, the result bit will be a zero.
    
    //newFilter = (F1 OR F2) AND (F1 OR R1) AND NOT(R2)
    //can be simplified to
    //newFilter = F1 OR (F2 AND R1) AND NOT(R2)
    BitVector newFilter = new BitVector(r.getFilter()).and(result_).or(filter_).andNot(r.bits());
    filter_ = newFilter;
    
    result_.andNot(r.bits());
    return this;
  }

  public QueryResult or(QueryResult r) {
    filter();
    filtered_ = false;
    deletedClean_ = false;

    //Let's merge the results first, as we can use the OR result in the filter construction.
    result_.or(r.bits());
    
    //As soon as one operand is true, the result will be true.
    //For all other cases involving at least one filtered operand, result will be filtered.
    //newFilter = (F1 OR F2) AND NOT(R1 OR R2)
    filter_.or(r.getFilter()).andNot(result_);    //result_ is already the merged result vector
    return this;
  }

  public QueryResult xor(QueryResult r) {
    filter();
    filtered_ = false;
    deletedClean_ = false;
    
    result_.xor(r.bits());
    
    //As soon as one operand is filtered, the result will be filtered.
    //(We must known both sides of the operation to know the result.)
    //newFilter = (F1 OR F2)
    filter_.or(r.getFilter());
    return this;
  }

  protected void filter() {
    if(filtered_ == false) {
      filtered_ = true;
      result_.andNot(filter_);
    }
  }
  
  protected void cleanDeleted() {
    if(deletedClean_ == false) {
      deletedClean_ = true;
      result_.andNot(deleted_);
    }
  }
  
  /**
   * Creates the hits_ member variable 
   */
  synchronized private void makeHits() {
    cleanDeleted();
    filter();
    hits_ = new int[result_.count()];
    int hitIndex = 0;
    for(int i = next(0); i != -1 ; i = next(i+1)) {
      hits_[hitIndex++] = i;
    }
  }
}
