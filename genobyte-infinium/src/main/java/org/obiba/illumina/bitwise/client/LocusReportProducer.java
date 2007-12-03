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

import java.io.PrintStream;

import org.obiba.genobyte.cli.CliContext;
import org.obiba.genobyte.cli.CliContext.QueryExecution;
import org.obiba.genobyte.cli.ReportCommand.ReportProducer;
import org.obiba.genobyte.statistic.CsvReport;
import org.obiba.genobyte.statistic.DefaultAssayStatsRunDefinition;
import org.obiba.genobyte.statistic.StatsPool;
import org.obiba.illumina.bitwise.AssayStore;
import org.obiba.illumina.bitwise.FrequenciesReport;
import org.obiba.illumina.bitwise.InfiniumGenotypingStore;


public class LocusReportProducer implements ReportProducer {

  public String getReportType() {
    return "locus";
  }

  public boolean requiresOpenStore() {
    return true;
  }

  public void generateReport(CliContext context, String[] parameters, PrintStream output) {
    //Prepare output file containing the report.
    CsvReport report = new FrequenciesReport();
    report.setOutput(output);

    AssayStore store = ((InfiniumGenotypingStore)context.getStore()).getAssayRecordStore();
    StatsPool<Integer,String> assayStatsPool = new StatsPool<Integer,String>(store, new DefaultAssayStatsRunDefinition());
    QueryExecution qe = ReportProducerUtil.findAssayQuery(context, parameters);
    if(qe != null) {
      assayStatsPool.setRecordMask(qe.getResult());
    }
    qe = ReportProducerUtil.findSampleQuery(context, parameters);
    if(qe != null) {
      assayStatsPool.setTransposedMask(qe.getResult());
    }

    context.getOutput().println("Calculating locus report for "+assayStatsPool.getRecordMask().count()+" assays on "+assayStatsPool.getTransposedMask().count()+" samples.");
    assayStatsPool.calculate();
    context.getOutput().println("Producing locus report.");
    report.digest(assayStatsPool);
  }

}
