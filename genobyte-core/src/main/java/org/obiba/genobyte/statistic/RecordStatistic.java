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
package org.obiba.genobyte.statistic;

import org.obiba.bitwise.query.QueryResult;

import java.util.Map;

/**
 * Defines the <tt>Statistic</tt> type that calculates the results for one record at a time.
 */
public interface RecordStatistic extends Statistic {

  /**
   * Computes the statistic with the provided parameters, for one record.
   *
   * @param pPool   the <tt>StatsPool</tt> instance containing computed statistical data.
   * @param pFields the map of parameters required to compute the statistic, identified by name.
   * @param pFilter a field filter that says which records to consider for calculation.
   * @param pIndex  the index of the current record on which calculation is being made.
   */
  public void calculate(StatsPool<?, ?> pPool, Map<String, Object> pFields, QueryResult pFilter, int pIndex);
}
