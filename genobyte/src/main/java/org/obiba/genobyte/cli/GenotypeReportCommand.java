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
package org.obiba.genobyte.cli;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.bitwise.util.BitVectorQueryResult;
import org.obiba.genobyte.GenotypingRecordStore;
import org.obiba.genobyte.report.GenotypeReport;


/**
 * A CliCommand for generating genotype reports (formated reports that contain genotypes and associated information).
 * <p/>
 * Instances of this command uses a list of {@link GenotypeReport} as the possible output formats. Report implementations may
 * be registered through the {@link GenotypeReportCommand#addGenotypeReport(GenotypeReport)} method.
 */
public class GenotypeReportCommand implements CliCommand {

  /** Registered GenotypeReport instances */
  private List<GenotypeReport> reports_ = new LinkedList<GenotypeReport>();

  public boolean requiresOpenStore() {
    return true;
  }

  /**
   * Executes the file loading procedure. The type and filename are extracted from the <tt>Option</tt> instance.
   * The method {@link GenotypeReport#loadFile(CliContext, File)} is called on the appropriate instance. If no such instance
   * exists, an error message is printed on the {@link CliContext#getOutput()} stream.
   */
  public boolean execute(Option opt, CliContext context) throws ParseException {
    String args[] = opt.getValues();
    if(args == null || args.length == 0) {
      context.getOutput().println("Missing argument to genotype report command. Please specify the type of report to obtain.");
      return false;
    }
    String type = args[0];

    GenotypeReport r = null;
    for(GenotypeReport report : reports_) {
      if(report.getReportType().equalsIgnoreCase(type) || (type.length() == 1 && report.getShortReportType() == type.charAt(0))) {
        r = report;
        break;
      }
    }

    if(r == null) {
      context.getOutput().println("There is no genotype report registered for the type ["+type+"] specified.");
      return false;
    }
    
    int filesToOutput = r.getNumberOfOutputFiles();
    File[] outputFiles = new File[filesToOutput];
    Set<String> filenames = new TreeSet<String>();
    for(int i = 0; i < filesToOutput; i++) {
      String filename = r.getDefaultFileName(i);
      // User specified filename
      if(i < args.length - 1) {
        filename = args[i+1];
      }

      if(filename == null) {
        if(i < args.length - 1) {
        } else {
          filename = r.getReportType() + "-file"+i+".txt";
        }
      }
      if(filenames.add(filename) == false) {
        context.getOutput().println("The filename ["+filename+"] is already used as an output for this report. Please specify disctinct filenames for each output file.");
        return false;
      }
      outputFiles[i] = new File(filename);

      if(outputFiles[i].exists() && outputFiles[i].canWrite() == false) {
        context.getOutput().println("Cannot write to file ["+filename+"].");
        return false;
      }
    }

    GenotypingRecordStore assays = context.getStore().getAssayRecordStore();
    GenotypingRecordStore samples = context.getStore().getSampleRecordStore();
    
    QueryResult assayMask = context.getStoreLastResult(assays);
    if(assayMask == null) {
      assayMask = new BitVectorQueryResult(new BitVector(assays.getStore().getCapacity()).setAll().andNot(assays.getStore().getDeleted()));
    }
    QueryResult sampleMask = context.getStoreLastResult(samples);
    if(sampleMask == null) {
      sampleMask = new BitVectorQueryResult(new BitVector(samples.getStore().getCapacity()).setAll().andNot(samples.getStore().getDeleted()));
    }

    context.getOutput().println("Creating ["+r.getReportType()+"] report on "+sampleMask.count()+" samples and "+assayMask.count()+" assays. Outputing report to file(s) "+Arrays.toString(outputFiles));
    try {
      r.generateReport(context.getStore(), sampleMask, assayMask, outputFiles);
    } catch(RuntimeException e) {
      context.getOutput().println("An unexpected error occured while generating the report. The reported error message was: "+e.getMessage());
      e.printStackTrace();
    }

    return false;
  }

  public Option getOption() {
    StringBuilder sb = new StringBuilder();
    for(GenotypeReport report : reports_) {
      if(sb.length() > 0) sb.append(", ");
      sb.append(report.getReportType()).append(" (").append(report.getShortReportType()).append(")");
    }
    return OptionBuilder.withDescription("generates a genotype report. Available types are ["+sb.toString()+"]").withLongOpt("genotypes").hasArgs().withArgName("type> <filename(s)").create('g');
  }
 
  /**
   * Registers an instance of {@link GenotypeReport} in this command so it may be invoked through the {@link BitwiseCli}.
   * 
   * @param report the instance to be registered
   */
  public void addGenotypeReport(GenotypeReport report) {
    reports_.add(report);
  }
  
  /**
   * Returns true if the command has an instance of {@link GenotypeReport} that uses the specified short name.
   * <p/>
   * If any registered report's method {@link GenotypeReport#getShortReportType()} returns <tt>s</tt>, this method returns true. Otherwise, it returns false. 
   * 
   * @param s the short name to check.
   * @return true if a registered report instance uses the <tt>s</tt> as its short name. 
   */
  public boolean hasReportType(char s) {
    for(GenotypeReport report : reports_) {
      if(report.getShortReportType() == s) return true;
    }
    return false;
  }
  
}
