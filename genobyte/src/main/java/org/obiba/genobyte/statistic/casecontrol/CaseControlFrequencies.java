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
package org.obiba.genobyte.statistic.casecontrol;

import java.util.Map;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.genobyte.model.SnpCall;
import org.obiba.genobyte.statistic.AbstractStatistic;
import org.obiba.genobyte.statistic.RecordStatistic;
import org.obiba.genobyte.statistic.StatsPool;

public class CaseControlFrequencies extends AbstractStatistic implements RecordStatistic {

  public static final String CASE_FREQ_A = "caseFreqA";
  public static final String CASE_FREQ_B = "caseFreqB";
  public static final String CASE_FREQ_H = "caseFreqH";
  public static final String CASE_FREQ_U = "caseFreqU";
  public static final String CASE_TOTAL_CALL = "caseTotalCall";
  public static final String CASE_CALL_RATE = "caseCallRate";
  public static final String CASE_MAF = "caseMaf";
  public static final String CASE_HW = "caseHw";
  public static final String CASE_HET = "caseHet";

  public static final String CONTROL_FREQ_A = "controlFreqA";
  public static final String CONTROL_FREQ_B = "controlFreqB";
  public static final String CONTROL_FREQ_H = "controlFreqH";
  public static final String CONTROL_FREQ_U = "controlFreqU";
  public static final String CONTROL_TOTAL_CALL = "controlTotalCall";
  public static final String CONTROL_CALL_RATE = "controlCallRate";
  public static final String CONTROL_MAF = "controlMaf";
  public static final String CONTROL_HW = "controlHw";
  public static final String CONTROL_HET = "controlHet";

  public static final String[] PARAMETERS = { CASE_FREQ_A, CASE_FREQ_B,
      CASE_FREQ_H, CASE_FREQ_U, CASE_TOTAL_CALL, CASE_CALL_RATE, CASE_MAF,
      CASE_HW, CASE_HET, CONTROL_FREQ_A, CONTROL_FREQ_B, CONTROL_FREQ_H,
      CONTROL_FREQ_U, CONTROL_TOTAL_CALL, CONTROL_CALL_RATE, CONTROL_MAF,
      CONTROL_HW, CONTROL_HET };

  public CaseControlFrequencies() {
    super();

    super.inputFields_.add("calls");

    for (String param : PARAMETERS) {
      super.outputParams_.add(param);
    }
  }

  public void calculate(StatsPool<?, ?> pPool, Map<String, Object> pFields,
      QueryResult pFilter, int pIndex) {
    Field calls = (Field) pFields.get("calls");

    if (calls != null) {
      BitVector alleleA = calls.getDictionary().lookup(SnpCall.A);
      BitVector alleleB = calls.getDictionary().lookup(SnpCall.B);
      BitVector alleleH = calls.getDictionary().lookup(SnpCall.H);
      BitVector alleleU = calls.getDictionary().lookup(SnpCall.U);

      QueryResult cases = ((QueryResult) pPool.getPool().get("casesFilter")).copy().and(pFilter);
      QueryResult controls = ((QueryResult) pPool.getPool().get("controlsFilter")).copy().and(pFilter);

      QueryResult[] filters = { cases, controls };
      String[] prefixes = { "case", "control" };

      for (int i = 0; i < filters.length; i++) {
        QueryResult filter = filters[i];
        String paramPrefix = prefixes[i];

        int freqA = 0;
        int freqB = 0;
        int freqH = 0;
        int freqU = 0;
        int totalCalls = 0;
        double callRate = 0;

        // Frequency of each allele
        freqA = calls.query(alleleA).and(filter).count();
        freqB = calls.query(alleleB).and(filter).count();
        freqH = calls.query(alleleH).and(filter).count();
        freqU = calls.query(alleleU).and(filter).count();

        // Total Calls
        totalCalls = freqA + freqB + freqH;

        // Call Rate
        callRate = totalCalls / (double) (totalCalls + freqU);
        callRate = Double.isNaN(callRate) ? 0.0 : callRate;

        // MAF
        double maf = (2d * Math.min(freqA, freqB) + freqH) / (2d * totalCalls);
        maf = Double.isNaN(maf) ? 0.0 : maf;

        // Hardy-Weinberg Equilibrium
        double hw = 0.0;
        if (Double.compare(maf, 0.0) != 0) {
          double p = (2d * freqA + freqH) / (2d * totalCalls);
          double q = 1d - p;

          double e_aa = totalCalls * p * p;
          double e_bb = totalCalls * q * q;
          double e_ab = 2 * totalCalls * p * q;

          hw = ((freqA - e_aa) * (freqA - e_aa)) / e_aa
              + ((freqB - e_bb) * (freqB - e_bb)) / e_bb
              + ((freqH - e_ab) * (freqH - e_ab)) / e_ab;
          hw = Double.isNaN(hw) ? 0.0 : hw;
        }

        // Heterozygosity
        double heterozygosity = 0.0;
        if (totalCalls != 0) {
          heterozygosity = freqH / (double) totalCalls;
        }

        // Put results in the proper structure
        pPool.setPoolResult(paramPrefix + "FreqA", pIndex, freqA);
        pPool.setPoolResult(paramPrefix + "FreqB", pIndex, freqB);
        pPool.setPoolResult(paramPrefix + "FreqH", pIndex, freqH);
        pPool.setPoolResult(paramPrefix + "FreqU", pIndex, freqU);
        pPool.setPoolResult(paramPrefix + "TotalCalls", pIndex, totalCalls);
        pPool.setPoolResult(paramPrefix + "CallRate", pIndex, callRate);
        pPool.setPoolResult(paramPrefix + "Maf", pIndex, maf);
        pPool.setPoolResult(paramPrefix + "Hw", pIndex, hw);
        pPool.setPoolResult(paramPrefix + "Het", pIndex, heterozygosity);
      }
    }
  }

}
