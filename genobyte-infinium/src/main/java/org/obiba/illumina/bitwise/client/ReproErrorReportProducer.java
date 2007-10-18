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

import org.obiba.bitwise.Field;
import org.obiba.genobyte.GenotypingRecordStore;
import org.obiba.genobyte.cli.CliContext;
import org.obiba.genobyte.cli.ReportCommand.ReportProducer;
import org.obiba.genobyte.inconsistency.ComparableRecordProvider;
import org.obiba.genobyte.inconsistency.ReproducibilityErrorCalculator;
import org.obiba.genobyte.inconsistency.ReproducibilityErrorCountingStrategy;
import org.obiba.genobyte.inconsistency.ReproducibilityErrors;


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

  public void generateReport(CliContext pContext, String pFilename) {
    //Prepare output file containing the report.
    PrintStream output = pContext.getOutput();
    boolean closeStream = false;
    if(pFilename != null) {
      closeStream = true;
      try {
        output = new PrintStream(new FileOutputStream(pFilename));
      } catch (FileNotFoundException e) {
        pContext.getOutput().println("Cannot output to file ["+pFilename+"]: " + e.getMessage());
        return;
      }
    }

    GenotypingRecordStore store = null;
    ReproducibilityErrorCountingStrategy report = null;
    switch(reproType) {
      case DNA: 
        store = pContext.getStore().getSampleRecordStore();
        report = new ReproDnaErrorReportingStrategy(output, store);
        break;
      case ASSAY: 
        store = pContext.getStore().getAssayRecordStore();
        report = new ReproAssayErrorReportingStrategy(output, store);
        break;
      default:
        throw new IllegalStateException("unknown ReproType ["+this.reproType+"]");
    }
    
    try {
      pContext.getStore().startTransaction();
      ReproducibilityErrorCalculator calc = new ReproducibilityErrorCalculator(store);
      ComparableRecordProvider c = store.getComparableRecordProvider();
      c.getComparableReferenceRecords();
      calc.setComparableRecordProvider(c);
      calc.setCountingStrategy(report);
      calc.calculate();
      pContext.getStore().commitTransaction();
    } finally {
      pContext.getStore().endTransaction();
      if(closeStream) {
        try {
          output.close();
        } catch (RuntimeException e) {
          // Ignore
        }
      }
      
    }
  }

  private static abstract class ReproErrorReportingStrategy<K> implements ReproducibilityErrorCountingStrategy<K> {
    protected PrintStream output;
    protected GenotypingRecordStore store;
    protected Field nameField;
    
    ReproErrorReportingStrategy(PrintStream output, GenotypingRecordStore store, String nameFieldName) {
      this.output = output;
      this.store = store;
      this.nameField = store.getStore().getField(nameFieldName);
      printLine("Reference,Replicate,Errors,Tests");
    }

    public void countInconsistencies(ReproducibilityErrors<K> errors) {
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

    public ReproDnaErrorReportingStrategy(PrintStream output, GenotypingRecordStore store) {
      super(output, store, "id");
    }
  }

  private static class ReproAssayErrorReportingStrategy extends ReproErrorReportingStrategy<Integer> {

    public ReproAssayErrorReportingStrategy(PrintStream output, GenotypingRecordStore store) {
      super(output, store, "ilmnId");
    }
  }
}
