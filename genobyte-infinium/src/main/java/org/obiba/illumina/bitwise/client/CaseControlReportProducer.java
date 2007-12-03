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
import java.util.HashMap;
import java.util.Map;

import org.obiba.genobyte.cli.CliContext;
import org.obiba.genobyte.cli.CliContext.QueryExecution;
import org.obiba.genobyte.cli.ReportCommand.ReportProducer;
import org.obiba.genobyte.statistic.CsvReport;
import org.obiba.genobyte.statistic.StatsPool;
import org.obiba.genobyte.statistic.casecontrol.CaseControlFrequencies;
import org.obiba.genobyte.statistic.casecontrol.DefaultCaseControlStatsRunDefinition;
import org.obiba.illumina.bitwise.AssayStore;
import org.obiba.illumina.bitwise.CaseControlReport;
import org.obiba.illumina.bitwise.InfiniumGenotypingStore;


public class CaseControlReportProducer implements ReportProducer {

  public boolean requiresOpenStore() {
    return true;
  }

  public void generateReport(CliContext context, String[] parameters, PrintStream output) {
    //Prepare output file containing the report.
    DefaultCaseControlStatsRunDefinition def = new DefaultCaseControlStatsRunDefinition();
    CsvReport report = new CaseControlReport();
    for(String outputField : def.getCcStat().getOutputParams()) {
      report.addPooledField(outputField);
    }  
    report.setOutput(output);

    if(parameters == null || parameters.length < 2) {
      context.getOutput().println("Missing cases and controls query reference.");
      help(context);
      return;
    }

    QueryExecution casesQuery = ReportProducerUtil.resolveSampleQuery(context, parameters, 0);
    QueryExecution controlsQuery = ReportProducerUtil.resolveSampleQuery(context, parameters, 1);
    if(casesQuery == null) {
      context.getOutput().println("Invalid cases population query reference.");
      help(context);
      return;
    }
    if(controlsQuery == null) {
      context.getOutput().println("Invalid controls population query reference.");
      help(context);
      return;
    }

    QueryExecution assaysQuery = ReportProducerUtil.resolveAssayQuery(context, parameters, 2);

    AssayStore assays = ((InfiniumGenotypingStore)context.getStore()).getAssayRecordStore();

    StatsPool<Integer,String> ccStatsPool = new StatsPool<Integer, String>(assays, def);

    Map<String, Object> params = new HashMap<String, Object>();
    params.put(CaseControlFrequencies.CASES_MASK_PARAMETER, casesQuery.getResult());
    params.put(CaseControlFrequencies.CONTROLS_MASK_PARAMETER, controlsQuery.getResult());
    ccStatsPool.setPredefinedValues(params);

    if(assaysQuery != null) {
      ccStatsPool.setRecordMask(assaysQuery.getResult());
    }

    context.getOutput().println("Calculating Case-Control report on " + casesQuery.count() +" cases and " + controlsQuery.count() + " controls for " + ccStatsPool.getRecordMask().count() + " SNPs");
    long start = System.currentTimeMillis();
    ccStatsPool.calculate();
    long end = System.currentTimeMillis();
    context.getOutput().println("Computation took " + (end - start) / 1000d + ".");
    context.getOutput().println("Producing report.");
    report.digest(ccStatsPool);
  }
  
  public String getReportType() {
    return "case-control";
  }
  
  private void help(CliContext context) {
    context.getOutput().println("You must provide at least two query references to produce the case-control report: one for each sub-population. ie: \"--report case-control out.csv q4 q8\" where q4 is the query reference for the cases population and q8 is the controls population. You may also specify a third query reference to produce the report on a subset of SNPs.");
  }
}
