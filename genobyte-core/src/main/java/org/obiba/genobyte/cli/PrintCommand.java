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
import org.obiba.bitwise.query.QueryResult;
import org.obiba.genobyte.cli.CliContext.QueryExecution;

/**
 * Prints the record indexes part of the last query result. 
 */
public class PrintCommand implements CliCommand {

  public boolean requiresOpenStore() {
    return true;
  }

  public boolean execute(Option opt, CliContext context) {
    String str = opt.getValue();
    QueryExecution qe = context.getHistory().resolveQuery(str);
    if(qe != null) {
      context.getOutput().println("Store: " + qe.getStore().getStore().getName());
      context.getOutput().println("Query: " + qe.getQuery());
      context.getOutput().println("Count: " + qe.count());
      context.getOutput().println("Results: ");
      if(qe.count() > 0) {
        int hits = qe.count();
        QueryResult qr = qe.getResult();
        for(int i = 0; i < hits; i++) {
          int hit = qr.hit(i);
          context.getOutput().print(hit + " ");
        }
        context.getOutput().println("");
      } else {
        context.getOutput().println("Query [" + str + "] produced no result. Nothing to print.");
      }
    } else {
      context.getOutput().println("No query result to print. Execute a query first.");
    }
    return false;
  }

  public Option getOption() {
    return OptionBuilder.withDescription("print the record indexes of query <q#>").hasArgs(1).withArgName("q#")
        .withLongOpt("print").create('p');
  }

}
