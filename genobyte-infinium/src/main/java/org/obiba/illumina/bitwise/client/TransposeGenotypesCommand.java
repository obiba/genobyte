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

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import org.obiba.genobyte.GenotypingStoreTransposer;
import org.obiba.genobyte.cli.CliCommand;
import org.obiba.genobyte.cli.CliContext;


public class TransposeGenotypesCommand implements CliCommand {
  
  public boolean requiresOpenStore() {
    return true;
  }
  
  public boolean execute(Option opt, CliContext context) throws ParseException {
    GenotypingStoreTransposer transposer = new GenotypingStoreTransposer(context.getStore().getSampleRecordStore());
    long start = System.currentTimeMillis();
    transposer.transpose();
    long end = System.currentTimeMillis();
    context.getOutput().println("Took " + ((end - start) / 1000d) + "s");
    return false;
  }

  public Option getOption() {
    return OptionBuilder.withDescription("transpose sample genotypes into the assay matrix").withLongOpt("transpose").create();
  }

}
