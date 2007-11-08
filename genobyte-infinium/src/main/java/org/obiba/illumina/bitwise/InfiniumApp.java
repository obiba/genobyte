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

import java.io.IOException;

import org.obiba.genobyte.cli.BitwiseCli;
import org.obiba.genobyte.cli.GenotypeReportCommand;
import org.obiba.genobyte.cli.LoadFileCommand;
import org.obiba.genobyte.cli.ReportCommand;
import org.obiba.illumina.bitwise.client.CaseControlReportProducer;
import org.obiba.illumina.bitwise.client.CreateStoreCommand;
import org.obiba.illumina.bitwise.client.DnaReportProducer;
import org.obiba.illumina.bitwise.client.LocusReportProducer;
import org.obiba.illumina.bitwise.client.LocusXDnaFileLoader;
import org.obiba.illumina.bitwise.client.ManifestFileLoader;
import org.obiba.illumina.bitwise.client.MapFileReportProducer;
import org.obiba.illumina.bitwise.client.MendelianErrorReportProducer;
import org.obiba.illumina.bitwise.client.OpenStoreCommand;
import org.obiba.illumina.bitwise.client.PedFileReport;
import org.obiba.illumina.bitwise.client.ReproErrorReportProducer;
import org.obiba.illumina.bitwise.client.SampleSheetFileLoader;
import org.obiba.illumina.bitwise.client.TransposeGenotypesCommand;



public class InfiniumApp {

  /**
   * @param args
   * @throws IOException 
   */
  public static void main(String[] args) throws IOException {
    BitwiseCli cli = new BitwiseCli();
    cli.registerCommand(new CreateStoreCommand());
    cli.registerCommand(new OpenStoreCommand());
    cli.registerCommand(new TransposeGenotypesCommand());

    GenotypeReportCommand gReportCommand = new GenotypeReportCommand();
    gReportCommand.addGenotypeReport(new PedFileReport());
    cli.registerCommand(gReportCommand);

    LoadFileCommand loadCommand = new LoadFileCommand();
    loadCommand.addFileTypeLoader(new SampleSheetFileLoader());
    loadCommand.addFileTypeLoader(new ManifestFileLoader());
    loadCommand.addFileTypeLoader(new LocusXDnaFileLoader());
    cli.registerCommand(loadCommand);
    
    ReportCommand reportCommand = new ReportCommand();
    reportCommand.addReport(new LocusReportProducer());
    reportCommand.addReport(new DnaReportProducer());
    reportCommand.addReport(new MapFileReportProducer());
    reportCommand.addReport(new MendelianErrorReportProducer());
    reportCommand.addReport(new ReproErrorReportProducer(ReproErrorReportProducer.ReproType.DNA));
    reportCommand.addReport(new ReproErrorReportProducer(ReproErrorReportProducer.ReproType.ASSAY));
    reportCommand.addReport(new CaseControlReportProducer());
    cli.registerCommand(reportCommand);
    cli.execute();
  }

}
