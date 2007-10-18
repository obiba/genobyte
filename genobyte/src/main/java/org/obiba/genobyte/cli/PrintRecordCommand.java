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
import org.obiba.genobyte.GenotypingRecordStore;


/**
 * Allows extracting a record from a store and outputing its values to the user.
 */
public class PrintRecordCommand implements CliCommand {

  public boolean requiresOpenStore() {
    return true;
  }
  
  public boolean execute(Option opt, CliContext context) throws ParseException {
    String str = opt.getValue();
    try {
      int index = Integer.valueOf(str);
      GenotypingRecordStore<?, ?, ?> store = context.getActiveRecordStore();
      if(store.getStore().getSize() == 0) {
        context.getOutput().println("Store is empty. To load records, use the --load command.");
      } else if(index < 0  || index >= store.getStore().getSize()) {
        context.getOutput().println("Record index invalid. Value must be between 0 and " + (store.getStore().getSize() - 1));
      } else {
        context.getOutput().println(store.getRecordManager().load(index));
      }
    } catch (NumberFormatException e) {
      context.getOutput().println("Record index invalid. The argument to this command should be an integer.");
    }
    return false;
  }

  public Option getOption() {
    return OptionBuilder.withDescription("load and print the record <i>.").withLongOpt("record").hasArg().withArgName("i").create('r');
  }

}
