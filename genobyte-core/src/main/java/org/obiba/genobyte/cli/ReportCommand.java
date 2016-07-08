/*******************************************************************************
 * Copyright 2007(c) Genome Quebec. All rights reserved.
 * <p>
 * This file is part of GenoByte.
 * <p>
 * GenoByte is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * <p>
 * GenoByte is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.obiba.genobyte.cli;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import org.obiba.genobyte.cli.LoadFileCommand.FileTypeLoader;

/**
 * Helps create commands that produces a single report from store data.
 * <p/>
 * Only one instance of this command may be registered to a {@link BitwiseCli}. For every report to be
 * produced, an implementation of {@link ReportProducer} should be added to the <tt>ReportCommand</tt>
 * instance using
 * {@link ReportCommand#addReportProducer(org.obiba.genobyte.cli.ReportCommand.ReportProducer)}.
 *
 * {@link ReportProducer} instances are identified using a keyword. 
 * <p/>
 * The command format is "<tt>report &lt;report_name&gt; &lt;output_filename&gt; &lt;report_parameters&gt;</tt>" where 
 * <ul>
 *  <li><tt>report_name</tt> is the name of the report to be produced.</li>
 *  <li><tt>output_filename</tt> is the name of the output file for the report. If none is use or "-" is specified,
 *  the report will be output to the {@link CliContext#getOutput()} stream.</li>
 *  <li><tt>report_parameters</tt> is an optional list of report parameters.
 * </ul>
 */
public class ReportCommand implements CliCommand {

  /** Registered ReportProducer instances */
  private List<ReportProducer> reports_ = new LinkedList<ReportProducer>();

  public boolean requiresOpenStore() {
    return true;
  }

  /**
   * Launches the report generation. The type of report to be produced and the output filename are extracted
   * from the <tt>Option</tt> instance.
   * The method {@link ReportProducer#generateReport(CliContext, File)} is called on the appropriate instance.
   * If no such instance exists, an error message is printed on the {@link CliContext#getOutput()} stream.
   */
  public boolean execute(Option opt, CliContext context) throws ParseException {
    String args[] = opt.getValues();
    if(args == null || args.length == 0) {
      context.getOutput().println("Missing argument to report command. Please specify the type of report.");
      return false;
    }

    String reportType = args[0];

    String reportFilename = null;
    if(args.length > 1) {
      reportFilename = args[1];
    }
    String[] parameters = null;
    if(args.length > 2) {
      parameters = new String[args.length - 2];
      for(int i = 2; i < args.length; i++) {
        parameters[i - 2] = args[i];
      }
    }

    ReportProducer r = null;
    for(ReportProducer report : reports_) {
      if(report.getReportType().equalsIgnoreCase(reportType)) {
        r = report;
        break;
      }
    }

    // Error if no report with such name exists
    if(r == null) {
      context.getOutput().println("There is no producer registered for the report type [" + reportType + "].");
      return false;
    }

    // Error if store is not opened yet but is required (we could want to generate report on non store-related information)
    if(r.requiresOpenStore() && context.getStore() == null) {
      context.getOutput().println("Open a store before loading a file of type [" + r.getReportType() + "]");
      return false;
    }

    boolean closeStream = false;
    PrintStream output = context.getOutput();
    if(reportFilename != null && reportFilename.equals("-") == false) {
      try {
        output = new PrintStream(new FileOutputStream(reportFilename));
        closeStream = true;
      } catch(IOException e) {
        context.getOutput().println("Cannot output to file [" + reportFilename + "]: " + e.getMessage());
        return false;
      }
    } else {
      reportFilename = "console output";
    }

    context.getOutput()
        .println("Producing report type [" + r.getReportType() + "]. Outputing report to [" + reportFilename + "].");
    try {
      r.generateReport(context, parameters, output);
    } catch(Exception e) {
      context.getOutput().println("An error occured during the report generation: " + e.getMessage());
      e.printStackTrace();
    } finally {
      if(closeStream) {
        try {
          output.close();
        } catch(Throwable t) {
          //ignore
        }
      }
    }
    return false;
  }

  public Option getOption() {
    StringBuilder sb = new StringBuilder();
    for(ReportProducer report : reports_) {
      if(sb.length() > 0) sb.append(", ");
      sb.append(report.getReportType());
    }
    return OptionBuilder.withDescription(
        "generates a report of type <type> and output to <filename> (use - for console output). Report parameters depend on its type. Available types are [" +
            sb.toString() + "]").withLongOpt("report").hasArgs().withArgName("type> <filename> <parameters").create();
  }

  /**
   * Adds an instance of {@link FileTypeLoader} that will may load a certain file type.
   *
   * @param loader the instance to be registered
   */
  public void addReport(ReportProducer report) {
    reports_.add(report);
  }

  /**
   * Generates a type of reports for a <tt>GenotypingStore</tt>. Implementations of this interface should
   * provide a unique name.
   */
  public interface ReportProducer {

    /**
     * Returns the name of this report implementation. The <tt>String</tt> returned by this method will be
     * used by the CLI user to invoke the proper instance to use for generating a report. For example, if this
     * method returns "locus", the user will invoke this loader by typing "report locus <filename>" on the CLI
     * prompt. 
     * @return the report type name.
     */
    public String getReportType();

    /**
     * Launches the report generation.
     * @param context the context of the CLI.
     * @param files the name of the output file, or <tt>null</tt> if result is outputed to client shell.
     */
    public void generateReport(CliContext context, String[] parameters, PrintStream output);

    /**
     * Returns true if the report generator requires that a store is opened before processing the file(s).
     * @return true if a store is required
     */
    public boolean requiresOpenStore();

  }

}
