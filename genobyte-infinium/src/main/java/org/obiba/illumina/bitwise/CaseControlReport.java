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
package org.obiba.illumina.bitwise;

import org.obiba.genobyte.statistic.CsvReport;

/** 
 * Implements CsvReport to output case-control computation.
 */
public class CaseControlReport extends CsvReport {

  public CaseControlReport() {
    addStoredField("snpName");
    addStoredField("chromosome");
    addStoredField("mapInfo");

    addPooledField("caseFreqA");
    addPooledField("caseFreqB");
    addPooledField("caseFreqH");
    addPooledField("caseFreqU");
    addPooledField("caseCallRate");
    addPooledField("caseMaf");
    addPooledField("caseHw");
    addPooledField("caseHet");

    addPooledField("controlFreqA");
    addPooledField("controlFreqB");
    addPooledField("controlFreqH");
    addPooledField("controlFreqU");
    addPooledField("controlCallRate");
    addPooledField("controlMaf");
    addPooledField("controlHw");
    addPooledField("controlHet");

    setColumnHeader("snpName", "SNP");
    setColumnHeader("chromosome", "Chromosome");
    setColumnHeader("mapInfo", "Location");

    setColumnHeader("caseFreqB", "Freq B");
    setColumnHeader("caseFreqH", "Freq H");
    setColumnHeader("caseFreqU", "Freq U");
    setColumnHeader("caseCallRate", "Call Rate");
    setColumnHeader("caseMaf", "MAF");
    setColumnHeader("caseHw", "HW");
    setColumnHeader("caseHet", "Heterozygosity");
    
    setColumnHeader("controlFreqA", "Freq A");
    setColumnHeader("controlFreqB", "Freq B");
    setColumnHeader("controlFreqH", "Freq H");
    setColumnHeader("controlFreqU", "Freq U");
    setColumnHeader("controlCallRate", "Call Rate");
    setColumnHeader("controlMaf", "MAF");
    setColumnHeader("controlHw", "HW");
    setColumnHeader("controlHet", "Heterozygosity");
  }

}
