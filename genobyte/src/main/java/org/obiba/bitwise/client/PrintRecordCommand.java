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
import org.apache.commons.cli.ParseException;
import org.obiba.bitwise.BitwiseStore;


//TODO: Fix PrintRecordCommand to make it work in the shell.
class PrintRecordCommand implements CliCommand {

  /*
   * @see org.obiba.bitwise.genotyping.cli.CliCommand#execute(org.apache.commons.cli.Option, org.obiba.bitwise.genotyping.GenotypingRecordStore)
   */
  public boolean execute(Option opt, ClientContext context) throws ParseException {
    String str = opt.getValue();
    try {
      int index = Integer.valueOf(str);
      BitwiseStore store = context.getStore();
      ResultDisplay rd = new ResultDisplay();
      rd.setDisplayType(ResultDisplay.DisplayType.PLAIN);
      
      //Filter out template fields
      for (String field : store.getFieldList()) {
//        if (field.matches(".*_\\d+")) {
//          continue;
//        }
        rd.addField(field);
      }
      
      rd.putRecord(store, index);
      System.out.println(rd.getOutput());
    } catch (NumberFormatException e) {
      throw new ParseException(e.getMessage());
    }
    return false;
  }

  /*
   * @see org.obiba.bitwise.genotyping.cli.CliCommand#getOption()
   */
  public Option getOption() {
    return OptionBuilder.withDescription("load and print the record <i>.").withLongOpt("record").hasArg().withArgName("i").create('r');
  }

}
