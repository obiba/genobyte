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
import org.obiba.bitwise.util.BitwiseDiskUtil;

/**
 * Deletes the specified store.
 */
public class DropCommand implements CliCommand {

  public boolean requiresOpenStore() {
    return false;
  }

  public boolean execute(Option opt, CliContext context) throws ParseException {
    String storeName = opt.getValue();
    if (context.getStore() != null) {
      if (context.getStore().getSampleRecordStore().getStore().getName().equals(storeName + "_samples")) {
        context.getOutput().println("Closing current store.");
        context.getStore().close();
        context.clear();
      }
    }

    context.getOutput().println("Deleting store " + storeName + ".");
    BitwiseDiskUtil.deleteStores(storeName + "_samples", storeName + "_assays");
    return false;
  }

  public Option getOption() {
    return OptionBuilder.withDescription("delete the store <name>").withLongOpt("delete").hasArg().withArgName("name")
        .create('d');
  }

}
