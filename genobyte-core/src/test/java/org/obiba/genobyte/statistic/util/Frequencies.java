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
package org.obiba.genobyte.statistic.util;

import java.util.Map;

import org.obiba.bitwise.Field;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.genobyte.model.SnpCall;
import org.obiba.genobyte.statistic.AbstractStatistic;
import org.obiba.genobyte.statistic.RecordStatistic;
import org.obiba.genobyte.statistic.StatsPool;


/**
 * Implements assay frequency statistics. This includes allele frequencies, MAF (Minor Allele Frequency),
 * the total number of calls, the rate of good calls, the Hardy-Weinberg Equilibrium and the heterozygosity.
 */
public class Frequencies extends AbstractStatistic implements RecordStatistic {

  public Frequencies() {
    inputFields_.add("calls");

    outputParams_.add("freqA");
    outputParams_.add("freqB");
    outputParams_.add("freqH");
    outputParams_.add("freqU");
  }


  //@see org.obiba.bitwise.genotyping.statistic.Statistic.calculate()
  public void calculate(StatsPool<?,?> pPool, Map<String, Object> pFields, QueryResult pFilter, int pIndex) {
    SnpCall alleleA = SnpCall.A;
    SnpCall alleleB = SnpCall.B;
    SnpCall alleleH = SnpCall.H;
    SnpCall alleleU = SnpCall.U;
    Field calls = (Field)pFields.get("calls");
   
    //Frequency of each allele
    int freqA = calls.query(calls.getDictionary().lookup(alleleA)).and(pFilter).count();
    int freqB = calls.query(calls.getDictionary().lookup(alleleB)).and(pFilter).count();
    int freqH = calls.query(calls.getDictionary().lookup(alleleH)).and(pFilter).count();
    int freqU = calls.query(calls.getDictionary().lookup(alleleU)).and(pFilter).count();
    
    //Put results in the proper structure
    pPool.setPoolResult("freqA", pIndex, freqA);
    pPool.setPoolResult("freqB", pIndex, freqB);
    pPool.setPoolResult("freqH", pIndex, freqH);
    pPool.setPoolResult("freqU", pIndex, freqU);
  }

}
