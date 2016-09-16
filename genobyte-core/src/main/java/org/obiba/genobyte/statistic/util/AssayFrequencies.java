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

import org.obiba.bitwise.query.QueryResult;
import org.obiba.genobyte.statistic.StatsPool;

import java.util.Map;

/**
 * Implements assay frequency statistics. This includes allele frequencies, MAF (Minor Allele Frequency),
 * the total number of calls, the rate of good calls, the Hardy-Weinberg Equilibrium and the heterozygosity.
 */
public class AssayFrequencies extends FrequencyStatistics {

  public AssayFrequencies() {
    super();
    outputParams_.add("maf");
    outputParams_.add("hw");
    outputParams_.add("heterozygosity");
  }

  //@see org.obiba.bitwise.genotyping.statistic.Statistic.calculate()
  public void calculate(StatsPool<?, ?> pPool, Map<String, Object> pFields, QueryResult pFilter, int pIndex) {
    calculateFrequencies(pPool, pFields, pFilter, pIndex);

    Integer freqA = pPool.getPoolResult("freqA", pIndex);
    Integer freqB = pPool.getPoolResult("freqB", pIndex);
    Integer freqH = pPool.getPoolResult("freqH", pIndex);
    Integer totalCalls = pPool.getPoolResult("totalCalls", pIndex);

    //MAF
    double maf = (2d * Math.min(freqA, freqB) + freqH) / (2d * totalCalls);
    maf = Double.isNaN(maf) ? 0.0 : maf;

    //Hardy-Weinberg Equilibrium
    double hw = 0.0;
    if (Double.compare(maf, 0.0) != 0) {
      double p = (2d * freqA + freqH) / (2d * totalCalls);
      double q = 1d - p;

      double e_aa = totalCalls * p * p;
      double e_bb = totalCalls * q * q;
      double e_ab = 2 * totalCalls * p * q;

      hw = ((freqA - e_aa) * (freqA - e_aa)) / e_aa + ((freqB - e_bb) * (freqB - e_bb)) / e_bb +
          ((freqH - e_ab) * (freqH - e_ab)) / e_ab;
      hw = Double.isNaN(hw) ? 0.0 : hw;
    }

    //Heterozygosity
    double heterozygosity = 0.0;
    if (totalCalls != 0) {
      heterozygosity = freqH / (double) totalCalls;
    }

    //Put results in the proper structure
    pPool.setPoolResult("maf", pIndex, maf);
    pPool.setPoolResult("hw", pIndex, hw);
    pPool.setPoolResult("heterozygosity", pIndex, heterozygosity);
  }

}
