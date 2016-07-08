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

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;

/**
 * Launches the inconsistencies calculation.
 */
public class InconsistenciesCommand implements CliCommand {

  public boolean requiresOpenStore() {
    return true;
  }

  public boolean execute(Option opt, CliContext context) throws ParseException {
    context.getOutput().println("Computing inconsistencies...");
    try {
      context.getStore().startTransaction();
      context.getOutput().println("Computing sample reproducibility errors.");
      context.getStore().reproDna();
      context.getOutput().println("Computing assay reproducibility errors.");
      context.getStore().reproAssay();
      context.getOutput().println("Computing mendelian errors.");
      context.getStore().mendel();
      context.getStore().commitTransaction();
    } catch(RuntimeException e) {
      context.getOutput().println("An error occured during the inconsistencies computation.");
    } finally {
      context.getStore().endTransaction();
    }
    context.getOutput().println("All done.");
    return false;
  }

  public Option getOption() {
    return OptionBuilder.withDescription("compute the store's inconsistencies").withLongOpt("inconsistencies")
        .create('i');
  }
}
