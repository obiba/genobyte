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
package org.obiba.genobyte.inconsistency;

import org.obiba.bitwise.query.QueryResult;

/**
 * Identifies records that may be compared to compute reproducibility errors.
 * <p/>
 * Instances of this interface are used by {@link ReproducibilityErrorCalculator} and provide
 * all the combination of records for which the genotypes should be compared. The calculator
 * first calls the {@link ComparableRecordProvider#getComparableReferenceRecords()} method. Then,
 * for each index set in the returned vector a call to {@link ComparableRecordProvider#getComparableRecords(int)}
 * is made in order to obtain all records that may be compared.
 */
public interface ComparableRecordProvider {

  /**
   * Returns an instance of {@link QueryResult} with bits set for all records that
   * are considered reference.
   * <p/>
   * Reference records are compared with all of its comparable records. Comparable records are
   * not compared with each other.
   *
   * @return a {@link QueryResult} instance that holds reference records.
   */
  public QueryResult getComparableReferenceRecords();

  /**
   * Returns an instance of {@link QueryResult} with bits set for all records that
   * are considered comparable with the specified reference record index.
   *
   * @param referenceRecord the reference record index.
   * @return a {@link QueryResult} instance that holds comparable records.
   */
  public QueryResult getComparableRecords(int referenceRecord);

}
