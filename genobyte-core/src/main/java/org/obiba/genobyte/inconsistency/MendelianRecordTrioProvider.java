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
 * Identifies trios that may be analysed to compute mendelian errors.
 * <p/>
 * Instances of this interface are used by {@link MendelianErrorCalculator} and provide
 * all the related records (mother, father, child) that should be analysed. The calculator
 * first calls the {@link MendelianRecordTrioProvider#getChildRecords()} method. Then, for each
 * index set in the returned vector, a call to {@link MendelianRecordTrioProvider#getFatherRecords(int)}
 * and {@link MendelianRecordTrioProvider#getMotherRecords(int)} is made.
 * 
 */
public interface MendelianRecordTrioProvider {

  /**
   * Returns an instance of {@link QueryResult} with bits set for all records that
   * are considered as a child.
   * <p/>
   * Every unique combinations of child-mother-father is analysed in order to compute mendelian errors.
   * 
   * @return a {@link QueryResult} instance that holds child records.
   */
  public QueryResult getChildRecords();

  /**
   * Returns an instance of {@link QueryResult} with bits set for all records that
   * are considered the mother with the specified child record index.
   * 
   * @param childRecord the child record index
   * @return a {@link QueryResult} instance that holds all records that are considered the mother of the specified child.
   */
  public QueryResult getMotherRecords(int childRecord);

  /**
   * Returns an instance of {@link QueryResult} with bits set for all records that
   * are considered the father with the specified child record index.
   * 
   * @param childRecord the child record index
   * @return a {@link QueryResult} instance that holds all records that are considered the father of the specified child.
   */
  public QueryResult getFatherRecords(int childRecord);

}
