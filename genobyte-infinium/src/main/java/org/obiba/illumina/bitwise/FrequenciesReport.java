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
 * CsvReport specific implementation that prints a CSV report of genotypes frequencies.
 */
public class FrequenciesReport extends CsvReport {

  public FrequenciesReport() {
    addStoredField("snpName");
    addPooledField("freqA");
    addPooledField("freqB");
    addPooledField("freqH");
    addPooledField("freqU");
    addPooledField("callRate");
    addPooledField("maf");
    addPooledField("hw");
    addPooledField("heterozygosity");

    setColumnHeader("freqA", "Freq A");
    setColumnHeader("freqB", "Freq B");
    setColumnHeader("freqH", "Freq H");
    setColumnHeader("freqU", "Freq U");
    setColumnHeader("callRate", "Call Rate");
    setColumnHeader("maf", "MAF");
    setColumnHeader("hw", "HW");
    setColumnHeader("heterozygosity", "Heterozygosity");
  }

}
