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
import org.obiba.genobyte.inconsistency.ComparableRecordProvider;
import org.obiba.genobyte.inconsistency.ReproducibilityErrorCalculator;
import org.obiba.genobyte.inconsistency.ReproducibilityErrorCountingStrategy;
import org.obiba.genobyte.inconsistency.ReproducibilityErrors;
import org.obiba.genobyte.inconsistency.util.MaskedComparableRecordProvider;


public class ReproErrorReportProducer implements ReportProducer {

  public enum ReproType {
    DNA,
    ASSAY
  }
  
  private ReproType reproType;
  
  public ReproErrorReportProducer(ReproType type) {
    this.reproType = type;
  }

  public String getReportType() {
    switch(reproType) {
      case DNA: return "reproDna";
      case ASSAY: return "reproAssay";
    }
    throw new IllegalStateException("unknown ReproType ["+this.reproType+"]");
  }

  public boolean requiresOpenStore() {
    return true;
  }

  public void generateReport(CliContext context, String[] parameters, PrintStream output) {
    GenotypingRecordStore<?, ?, ?> store = null;
    ReproErrorReportingStrategy<?> report = null;

    QueryExecution sampleQuery = ReportProducerUtil.findSampleQuery(context, parameters);
    QueryExecution assayQuery = ReportProducerUtil.findAssayQuery(context, parameters);

    ComparableRecordProvider provider;
    switch(reproType) {
      case DNA:
        store = context.getStore().getSampleRecordStore();
        provider = store.getComparableRecordProvider();
        report = new ReproDnaErrorReportingStrategy(output, store);
        if(sampleQuery != null) {
          provider = new MaskedComparableRecordProvider(provider, sampleQuery.getResult());
        }
        if(assayQuery != null) {
          report.setMask(assayQuery.getResult());
        }
        break;
      case ASSAY: 
        store = context.getStore().getAssayRecordStore();
        provider = store.getComparableRecordProvider();
        report = new ReproAssayErrorReportingStrategy(output, store);
        if(assayQuery != null) {
          provider = new MaskedComparableRecordProvider(provider, assayQuery.getResult());
        }
        if(sampleQuery != null) {
          report.setMask(sampleQuery.getResult());
        }
        break;
      default:
        throw new IllegalStateException("unknown ReproType ["+this.reproType+"]");
    }

    try {
      context.getStore().startTransaction();
      ReproducibilityErrorCalculator calc = new ReproducibilityErrorCalculator(store);
      calc.setComparableRecordProvider(provider);
      calc.setCountingStrategy(report);
      calc.calculate();
      context.getStore().commitTransaction();
    } finally {
      context.getStore().endTransaction();
    }
  }

  private static abstract class ReproErrorReportingStrategy<K> implements ReproducibilityErrorCountingStrategy<K> {
    protected PrintStream output;
    protected GenotypingRecordStore<?, ?, ?> store;
    protected Field nameField;

    protected QueryResult mask;

    ReproErrorReportingStrategy(PrintStream output, GenotypingRecordStore<?, ?, ?> store, String nameFieldName) {
      this.output = output;
      this.store = store;
      this.nameField = store.getStore().getField(nameFieldName);
      printLine("Reference,Replicate,Errors,Tests");
    }

    public void setMask(QueryResult mask) {
      this.mask = mask;
    }

    public void countInconsistencies(ReproducibilityErrors<K> errors) {
      if(mask != null) {
        errors.getInconsistencies().and(mask);
        errors.getTests().and(mask);
      }
      printLine(getName(errors.getReferenceIndex()), getName(errors.getReplicateIndex()), errors.getInconsistencies().count(), errors.getTests().count());
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
      return this.nameField.getDictionary().reverseLookup(this.nameField.getValue(recordIndex));
    }
  }

  private static class ReproDnaErrorReportingStrategy extends ReproErrorReportingStrategy<String> {

    public ReproDnaErrorReportingStrategy(PrintStream output, GenotypingRecordStore<?, ?, ?> store) {
      super(output, store, "id");
    }

  }

  private static class ReproAssayErrorReportingStrategy extends ReproErrorReportingStrategy<Integer> {

    public ReproAssayErrorReportingStrategy(PrintStream output, GenotypingRecordStore<?, ?, ?> store) {
      super(output, store, "ilmnId");
    }

  }
}
