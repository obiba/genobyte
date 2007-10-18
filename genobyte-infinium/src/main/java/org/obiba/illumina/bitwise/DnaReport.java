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
public class DnaReport extends CsvReport {

  public DnaReport() {
    addStoredField("id");
    addStoredField("gender");
    addStoredField("plate");
    addStoredField("well");
    addStoredField("sentrixBarcodeA");
    addStoredField("sentrixPositionA");
    addStoredField("group");
    addStoredField("replicates");
    addStoredField("parent1");
    addStoredField("parent2");
    addStoredField("path");
    addStoredField("reference");
    addPooledField("freqA");
    addPooledField("freqB");
    addPooledField("freqH");
    addPooledField("freqU");
    addPooledField("callRate");

    setColumnHeader("id", "Sample Name");
    setColumnHeader("gender", "Gender");
    setColumnHeader("plate", "Plate");
    setColumnHeader("well", "Well");
    setColumnHeader("sentrixBarcode", "Sentrix Barcord");
    setColumnHeader("sentrixPositionA", "Sentrix Position A");
    setColumnHeader("group", "Group");
    setColumnHeader("replicates", "Replicates");
    setColumnHeader("parent1", "Parent 1");
    setColumnHeader("parent2", "Parent 2");
    setColumnHeader("path", "Path");
    setColumnHeader("reference", "Reference");
    setColumnHeader("freqA", "Freq A");
    setColumnHeader("freqB", "Freq B");
    setColumnHeader("freqH", "Freq H");
    setColumnHeader("freqU", "Freq U");
    setColumnHeader("callRate", "Call Rate");
  }

}
