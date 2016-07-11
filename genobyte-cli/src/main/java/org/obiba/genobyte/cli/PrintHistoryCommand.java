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
import org.obiba.bitwise.util.FixedWidthPrinter;
import org.obiba.genobyte.cli.CliContext.QueryExecution;
import org.obiba.genobyte.cli.CliContext.QueryHistory;

public class PrintHistoryCommand implements CliCommand {

  public boolean execute(Option opt, CliContext context) throws ParseException {
    FixedWidthPrinter printer = new FixedWidthPrinter(context.getOutput(), 4);

    QueryHistory qh = context.getHistory();
    int indexWidth = (int) Math.ceil(Math.log10(qh.size() + 1));
    int storeNameWidth = Math.max(context.getStore().getAssayRecordStore().getStore().getName().length(),
        context.getStore().getSampleRecordStore().getStore().getName().length());
    int countWidth = Math.max((int) Math.ceil(Math.log10(qh.getMaxCount() + 1)), "Count".length());
    printer.setWidths(indexWidth, storeNameWidth, countWidth);

    // Print header
    printer.printLine("#", "Store", "Count", "Query");
    for(int i = 0; i < qh.size(); i++) {
      QueryExecution qe = qh.get(i);
      printer.printLine(Integer.toString(i + 1), qe.getStore().getStore().getName(), Integer.toString(qe.count()),
          qe.getQuery());
    }
    return false;
  }

  public Option getOption() {
    return OptionBuilder.withDescription("print the history of executed queries.").withLongOpt("history").create('y');
  }

  public boolean requiresOpenStore() {
    return true;
  }

}
