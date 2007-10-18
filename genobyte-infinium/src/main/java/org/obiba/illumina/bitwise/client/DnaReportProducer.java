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
package org.obiba.illumina.bitwise.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.obiba.genobyte.cli.CliContext;
import org.obiba.genobyte.cli.ReportCommand.ReportProducer;
import org.obiba.genobyte.statistic.CsvReport;
import org.obiba.genobyte.statistic.DefaultSampleStatsRunDefinition;
import org.obiba.genobyte.statistic.StatsPool;
import org.obiba.illumina.bitwise.AssayStore;
import org.obiba.illumina.bitwise.DnaReport;
import org.obiba.illumina.bitwise.InfiniumGenotypingStore;
import org.obiba.illumina.bitwise.SampleStore;


public class DnaReportProducer implements ReportProducer {

  public String getReportType() {
    return "dna";
  }


  public boolean requiresOpenStore() {
    return true;
  }

  public void generateReport(CliContext pContext, String pFilename) {
    //Prepare output file containing the report.
    CsvReport report = new DnaReport();
    report.setOutput(pContext.getOutput());
    boolean closeStream = false;
    if(pFilename != null) {
      closeStream = true;
      try {
        report.setOutput(new PrintStream(new FileOutputStream(pFilename)));
      } catch (FileNotFoundException e) {
        pContext.getOutput().println("Cannot output to file ["+pFilename+"]: " + e.getMessage());
        return;
      }
    }

    SampleStore store = ((InfiniumGenotypingStore)pContext.getStore()).getSampleRecordStore();
    AssayStore assays = ((InfiniumGenotypingStore)pContext.getStore()).getAssayRecordStore();

    StatsPool<String,Integer> sampleStatsPool = new StatsPool<String,Integer>(store, new DefaultSampleStatsRunDefinition());
    if(pContext.getStoreLastResult(store) != null) {
      sampleStatsPool.setRecordMask(pContext.getStoreLastResult(store));
    }
    if(pContext.getStoreLastResult(assays) != null) {
      sampleStatsPool.setTransposedMask(pContext.getStoreLastResult(assays));
    }


    pContext.getOutput().println("Calculating DNA report for "+sampleStatsPool.getRecordMask().count()+" samples on "+sampleStatsPool.getTransposedMask().count()+" assays.");
    sampleStatsPool.calculate();
    pContext.getOutput().println("Producing DNA report.");
    report.digest(sampleStatsPool);

    if(closeStream) {
      report.getOutput().close();
    }
  }

}
