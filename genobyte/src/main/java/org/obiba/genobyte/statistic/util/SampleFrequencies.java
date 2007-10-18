/*******************************************************************************
 * Copyright 2007(c) G�nome Qu�bec. All rights reserved.
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
package org.obiba.genobyte.statistic.util;

import java.util.Map;

import org.obiba.bitwise.query.QueryResult;
import org.obiba.genobyte.statistic.StatsPool;


/**
 * Implements sample frequency statistics. This includes allele frequencies, the total number of calls
 * and the rate of good calls.
 */
public class SampleFrequencies extends FrequencyStatistics {

  //@see org.obiba.bitwise.genotyping.statistic.Statistic.calculate()
  public void calculate(StatsPool<?,?> pPool, Map<String, Object> pFields, QueryResult pFilter, int pIndex) {
    calculateFrequencies(pPool, pFields, pFilter, pIndex);
  }

}
