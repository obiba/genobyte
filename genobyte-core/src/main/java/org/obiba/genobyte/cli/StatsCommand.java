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

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import org.obiba.genobyte.GenotypingStore;


/**
 * Launches the default statistics calculation run.
 */
public class StatsCommand implements CliCommand {

  public boolean requiresOpenStore() {
    return true;
  }
  
  public boolean execute(Option opt, CliContext context) throws ParseException {
    context.getOutput().println("Computing statistics...");
    GenotypingStore<?, ?, ?, ?> store = context.getStore();
    try {
      store.startTransaction();
      context.getOutput().println("Computing statistics for all samples.");
      store.getSampleRecordStore().updateStats();
      context.getOutput().println("Computing statistics for all assays.");
      store.getAssayRecordStore().updateStats();
      store.commitTransaction();
    } catch (RuntimeException e) {
      context.getOutput().println("An error occured during the statistics computation. Error reported : " + e.getMessage());
    } finally {
      store.endTransaction();
    }
    return false;
  }

  public Option getOption() {
    return OptionBuilder.withDescription("compute the store's statistics").withLongOpt("stats").create('t');
  }
}
