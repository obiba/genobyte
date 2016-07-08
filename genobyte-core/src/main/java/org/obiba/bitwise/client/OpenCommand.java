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
import org.obiba.bitwise.BitwiseStoreUtil;


/**
 * This command will close a currently opened store if there is, and open a new
 * one. It will take care of modifying the client context.
 */
class OpenCommand implements CliCommand {
  public boolean execute(Option opt, ClientContext context) {
    BitwiseStoreUtil bwsUtil = BitwiseStoreUtil.getInstance();
    
    //Extract name of store to be opened
    String newStoreName = opt.getValue();
    
    //Make sure the store name exists
    if (!(bwsUtil.exists(newStoreName))) {
      System.out.println("Specified store name doesn't exist.\n");
      return false;
    }
    
    //Close the currently opened store
    BitwiseStore currentStore = context.getStore();
    if (!(currentStore == null)) {
      currentStore.endTransaction();
      currentStore.close();
    }
    
    //Open the new store
    BitwiseStore newStore = bwsUtil.open(newStoreName);
    newStore.startTransaction();
    context.setStore(newStore);
    
    System.out.println("Switching to store " + newStoreName + "\n");
    return false;
  }

  public Option getOption() {
    return OptionBuilder.withDescription("change the current active store").withLongOpt("open").hasArg().withArgName("storeName").create('o');
  }
}
