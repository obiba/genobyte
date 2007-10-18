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
package org.obiba.genobyte.inconsistency;

import org.obiba.bitwise.query.QueryResult;

/**
 * Base class for reported inconsistencies.
 * <p/>
 * Holds a vector of errors and a vector of comparisons made. The vector of comparisons 
 * has its bit set at every index for which the calls were compared. The vector of inconsistencies
 * has its bit set at every index for which their is an error.
 */
abstract public class Inconsistencies {

  /** Vector of all compared values. The bit is set for every compared value. */
  private QueryResult tests_ = null;
  /** Vector of all errors. The bit is set for every error. */
  private QueryResult inconsistencies_ = null;

  /**
   * Returns the vector of inconsistencies (errors) which has its bit set at every index for which their is an error.
   * @return the vector of inconsistencies
   */
  public QueryResult getInconsistencies() {
    return inconsistencies_;
  }

  /**
   * Returns the vector of comparisons (tests) which has its bit set at every index for which the calls were compared.
   * @return the vector of comparisons (tests)
   */
  public QueryResult getTests() {
    return tests_;
  }

  /**
   * @param inconsistencies the vector of inconsistencies
   */
  public void setInconsistencies(QueryResult inconsistencies) {
    inconsistencies_ = inconsistencies;
  }

  /**
   * @param tests the vector of comparisons
   */
  public void setTests(QueryResult tests) {
    tests_ = tests;
  }

  
}
