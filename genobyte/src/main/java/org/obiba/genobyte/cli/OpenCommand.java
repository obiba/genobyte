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
import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.BitwiseStoreUtil;
import org.obiba.bitwise.util.BdbEnvUtil;
import org.obiba.genobyte.GenotypingStore;


/**
 * Base class for opening a GenotypingStore, extending classes should implement the {@link OpenCommand#openStore} method.
 */
public abstract class OpenCommand implements CliCommand {

  public boolean requiresOpenStore() {
    return false;
  }
  
  public boolean execute(Option opt, CliContext context) throws ParseException {
    String storeName = opt.getValue();
    if(context.getStore() != null) {
      context.getOutput().println("Closing current store.");
      context.getStore().close();
    }

    context.clear();
    context.getOutput().println("Opening store "+storeName+".");
    GenotypingStore<?, ?, ?, ?> store = open(storeName);
    if(store == null) {
      context.getOutput().println("Store "+storeName+" does not exist");
      return false;
    }
    context.setStore(store);
    context.setActiveRecordStore(store.getSampleRecordStore());

    return false;
  }

  public Option getOption() {
    return OptionBuilder.withDescription("open the store <name>").withLongOpt("open").hasArg().withArgName("name").create('o');
  }

  /**
   * Override this method and return an opened instance of the specified store name.
   *
   * @param samples the store holding the sample records
   * @param assays the store holding the assay records
   * @return an opened instance of the specified store 
   */
  abstract protected GenotypingStore<?, ?, ?, ?> openStore(BitwiseStore samples, BitwiseStore assays);

  /**
   * Opens both {@link BitwiseStore} instances and calls {@link OpenCommand#openStore(BitwiseStore, BitwiseStore)}.
   *
   * @param name the name of the store to open
   * @return an opened instance of the specified store 
   */
  protected GenotypingStore<?, ?, ?, ?> open(String name) {
    BitwiseStore sampleStore = null;
    BitwiseStore assayStore = null;
    String samplesName = name+"_samples";
    String assaysName = name+"_assays";
    if(BitwiseStoreUtil.getInstance().exists(samplesName)) {
      sampleStore = BitwiseStoreUtil.getInstance().open(samplesName);
      if(sampleStore == null) {
        throw new IllegalArgumentException("Cannot open store ["+samplesName+"]");
      }
    } else {
      // TODO: remove reference to Bdb. When a call to "exists" is made, the underlying directory is created. This will delete it.
      BdbEnvUtil.deleteStore(samplesName);
      return null;
    }
    if(BitwiseStoreUtil.getInstance().exists(assaysName)) {
      assayStore = BitwiseStoreUtil.getInstance().open(assaysName);
      if(assayStore == null) {
        throw new IllegalArgumentException("Cannot open store ["+assaysName+"]");
      }
    } else {
      BdbEnvUtil.deleteStore(assaysName);
      return null;
    }
    
    return openStore(sampleStore, assayStore); 

  }
}
