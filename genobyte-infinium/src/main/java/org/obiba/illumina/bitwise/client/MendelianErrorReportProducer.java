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
import org.obiba.genobyte.inconsistency.MendelianErrorCalculator;
import org.obiba.genobyte.inconsistency.MendelianErrorCountingStrategy;
import org.obiba.genobyte.inconsistency.MendelianErrors;
import org.obiba.illumina.bitwise.InfiniumGenotypingStore;
import org.obiba.illumina.bitwise.SampleStore;


public class MendelianErrorReportProducer implements ReportProducer {

  public String getReportType() {
    return "mendel";
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
    
    SampleStore samples = ((InfiniumGenotypingStore)pContext.getStore()).getSampleRecordStore();

    try {
      pContext.getStore().startTransaction();
      MendelianErrorCalculator<String> mendelCalculator = new MendelianErrorCalculator<String>(samples);
      mendelCalculator.setRecordProvider(samples.getMendelianRecordTrioProvider());
      mendelCalculator.setCountingStrategy(new MendelianErrorReportingStrategy(output, samples));
      mendelCalculator.calculate();
      pContext.getStore().commitTransaction();
    } finally {
      pContext.getStore().endTransaction();
      if(closeStream) {
        try {
          output.close();
        } catch (RuntimeException e) {
        }
      }
    }
  }
  

  private static class MendelianErrorReportingStrategy implements MendelianErrorCountingStrategy<String> {
    protected PrintStream output;
    protected GenotypingRecordStore store;
    protected Field nameField;

    MendelianErrorReportingStrategy(PrintStream output, GenotypingRecordStore store) {
      this.output = output;
      this.store = store;
      this.nameField = store.getStore().getField("id");
      printLine("Child ID,Parent 1 ID,Parent 2 ID,Errors,Tests");
    }

    public void countInconsistencies(MendelianErrors<String> errors) {
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
      return this.nameField.getDictionary().reverseLookup(this.nameField.getValue(recordIndex));
    }
  }

}
