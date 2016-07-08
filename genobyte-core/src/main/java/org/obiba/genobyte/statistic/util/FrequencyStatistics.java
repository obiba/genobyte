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
package org.obiba.genobyte.statistic.util;

import java.util.Map;

import org.obiba.bitwise.Field;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.genobyte.model.SnpCall;
import org.obiba.genobyte.statistic.AbstractStatistic;
import org.obiba.genobyte.statistic.RecordStatistic;
import org.obiba.genobyte.statistic.StatsPool;

/**
 * Defines the basic operations to be done by frequency statistics on genotypes. Assays and Samples statistics
 * will be based on this class.
 */
public abstract class FrequencyStatistics extends AbstractStatistic implements RecordStatistic {

  public FrequencyStatistics() {
    inputFields_.add("calls");

    outputParams_.add("freqA");
    outputParams_.add("freqB");
    outputParams_.add("freqH");
    outputParams_.add("freqU");
    outputParams_.add("totalCall");
    outputParams_.add("callRate");
  }

  //@see org.obiba.bitwise.genotyping.statistic.Statistic.calculate()
  protected void calculateFrequencies(StatsPool<?, ?> pPool, Map<String, Object> pFields, QueryResult pFilter,
      int pIndex) {
    SnpCall alleleA = SnpCall.A;
    SnpCall alleleB = SnpCall.B;
    SnpCall alleleH = SnpCall.H;
    SnpCall alleleU = SnpCall.U;
    Field calls = (Field) pFields.get("calls");

    int freqA = 0;
    int freqB = 0;
    int freqH = 0;
    int freqU = 0;
    int totalCalls = 0;
    double callRate = 0;
    if(calls != null) {
      //Frequency of each allele
      freqA = calls.query(calls.getDictionary().lookup(alleleA)).and(pFilter).count();
      freqB = calls.query(calls.getDictionary().lookup(alleleB)).and(pFilter).count();
      freqH = calls.query(calls.getDictionary().lookup(alleleH)).and(pFilter).count();
      freqU = calls.query(calls.getDictionary().lookup(alleleU)).and(pFilter).count();

      //Total Calls
      totalCalls = freqA + freqB + freqH;

      //Call Rate
      callRate = totalCalls / (double) (totalCalls + freqU);
      callRate = Double.isNaN(callRate) ? 0.0 : callRate;
    }
    //Put results in the proper structure
    pPool.setPoolResult("freqA", pIndex, freqA);
    pPool.setPoolResult("freqB", pIndex, freqB);
    pPool.setPoolResult("freqH", pIndex, freqH);
    pPool.setPoolResult("freqU", pIndex, freqU);
    pPool.setPoolResult("totalCalls", pIndex, totalCalls);
    pPool.setPoolResult("callRate", pIndex, callRate);
  }

}
