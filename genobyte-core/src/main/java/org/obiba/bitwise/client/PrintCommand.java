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
package org.obiba.bitwise.client;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.obiba.bitwise.query.QueryResult;


/**
 * This command will print the index of all records matching the last query that
 * has been ran.
 *
 */
class PrintCommand implements CliCommand {

  /*
   * @see org.obiba.bitwise.genotyping.cli.CliCommand#execute(org.apache.commons.cli.Option, org.obiba.bitwise.genotyping.GenotypingRecordStore)
   */
  public boolean execute(Option opt, ClientContext context) {
    QueryResult lastResult = context.getLastResult();
    if(lastResult != null) {
      int hits = lastResult.count();
      for(int i = 0; i < hits; i++) {
        int hit = lastResult.hit(i);
        System.out.print(hit + " ");
      }
      System.out.println("");
    } else {
      System.out.println("No query result to print");
    }
    return false;
  }

  /*
   * @see org.obiba.bitwise.genotyping.cli.CliCommand#getOption()
   */
  public Option getOption() {
    return OptionBuilder.withDescription("print the record indexes of the last query result").withLongOpt("print").create('p');
  }

}
