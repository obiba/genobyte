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

import org.obiba.bitwise.Field;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.genobyte.GenotypingRecordStore;
import org.obiba.genobyte.cli.CliContext;
import org.obiba.genobyte.cli.CliContext.QueryExecution;
import org.obiba.genobyte.cli.ReportCommand.ReportProducer;
import org.obiba.genobyte.inconsistency.MendelianErrorCalculator;
import org.obiba.genobyte.inconsistency.MendelianErrorCountingStrategy;
import org.obiba.genobyte.inconsistency.MendelianErrors;
import org.obiba.genobyte.inconsistency.MendelianRecordTrioProvider;
import org.obiba.genobyte.inconsistency.util.MaskedRecordTrioProvider;
import org.obiba.illumina.bitwise.InfiniumGenotypingStore;
import org.obiba.illumina.bitwise.SampleStore;


public class MendelianErrorReportProducer implements ReportProducer {

  public String getReportType() {
    return "mendel";
  }


  public boolean requiresOpenStore() {
    return true;
  }


  public void generateReport(CliContext context, String[] parameters, PrintStream output) {
    SampleStore samples = ((InfiniumGenotypingStore)context.getStore()).getSampleRecordStore();

    try {
      QueryExecution sampleQuery = ReportProducerUtil.findSampleQuery(context, parameters);
      QueryExecution assayQuery = ReportProducerUtil.findAssayQuery(context, parameters);
      MendelianErrorReportingStrategy strategy = new MendelianErrorReportingStrategy(output, samples);
      if(assayQuery != null) {
        strategy.setAssayFilter(assayQuery.getResult());
      }

      context.getStore().startTransaction();
      MendelianErrorCalculator<String> mendelCalculator = new MendelianErrorCalculator<String>(samples);

      MendelianRecordTrioProvider provider = samples.getMendelianRecordTrioProvider();
      if(sampleQuery != null) {
        provider = new MaskedRecordTrioProvider(provider, sampleQuery.getResult());
      }
      mendelCalculator.setRecordProvider(provider);
      mendelCalculator.setCountingStrategy(strategy);
      mendelCalculator.calculate();
      context.getStore().commitTransaction();
    } finally {
      context.getStore().endTransaction();
    }
  }

  private static class MendelianErrorReportingStrategy implements MendelianErrorCountingStrategy<String> {
    private PrintStream output;
    private GenotypingRecordStore store;
    private Field nameField;

    private QueryResult assayFilter;

    MendelianErrorReportingStrategy(PrintStream output, GenotypingRecordStore store) {
      this.output = output;
      this.store = store;
      this.nameField = store.getStore().getField("id");
      printLine("Child ID,Parent 1 ID,Parent 2 ID,Errors,Tests");
    }

    public void countInconsistencies(MendelianErrors<String> errors) {
      QueryResult inconsistencies = errors.getInconsistencies();
      QueryResult tests = errors.getTests();
      if(assayFilter != null) {
        inconsistencies.and(assayFilter);
        tests.and(assayFilter);
      }
      printLine(getName(errors.getChildIndex()), getName(errors.getMotherIndex()), getName(errors.getFatherIndex()), errors.getInconsistencies().count(), errors.getTests().count());
    }

    private void printLine(Object ... values) {
      for (int i = 0; i < values.length; i++) {
        Object value = values[i];
        if(value != null) {
          if(i>0) this.output.print(',');
          this.output.print(value);
        }
      }
      this.output.println("");
    }

    protected Object getName(int recordIndex) {
      if(recordIndex != -1) {
        return this.nameField.getDictionary().reverseLookup(this.nameField.getValue(recordIndex));
      }
      return "";
    }
    
    public void setAssayFilter(QueryResult assayFilter) {
      this.assayFilter = assayFilter;
    }

  }

}
