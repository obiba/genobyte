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
package org.obiba.illumina.bitwise.client;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.BitwiseStoreUtil;
import org.obiba.bitwise.annotation.AnnotationStoreSchemaBuilder;
import org.obiba.genobyte.cli.CliCommand;
import org.obiba.genobyte.cli.CliContext;
import org.obiba.illumina.bitwise.InfiniumGenotypingStore;
import org.obiba.illumina.bitwise.model.Assay;
import org.obiba.illumina.bitwise.model.Sample;


public class CreateStoreCommand implements CliCommand {

  public boolean requiresOpenStore() {
    return false;
  }
  
  public boolean execute(Option opt, CliContext context) throws ParseException {

    if(context.getStore() != null) {
      context.getOutput().println("Closing current store.");
      context.getStore().close();
      context.clear();
    }
    
    String name = opt.getValue();
    AnnotationStoreSchemaBuilder schemaBuilder = new AnnotationStoreSchemaBuilder();
    BitwiseStore samples;
    BitwiseStore assays;
    try {
      String samplesName = name+"_samples";
      if(BitwiseStoreUtil.getInstance().exists(samplesName) == true) {
        context.getOutput().println("A store named "+name+" already exists. Either use the existing store or delete it to create a new store with that name.");
        return false;
      }
      context.getOutput().println("Creating store "+name+".");
      samples = BitwiseStoreUtil.getInstance().create(name+"_samples", schemaBuilder.createSchema(Sample.class), 0);
      assays = BitwiseStoreUtil.getInstance().create(name+"_assays", schemaBuilder.createSchema(Assay.class), 0);
    } catch (Exception e) {
      context.getOutput().println("An unexpected error occured while creating store ["+name+"]: " + e.getMessage());
      e.printStackTrace(context.getOutput());
      return false;
    }

    context.setStore(new InfiniumGenotypingStore(samples, assays));
    context.setActiveRecordStore(context.getStore().getSampleRecordStore());
    return false;
  }

  public Option getOption() {
    return OptionBuilder.withDescription("create a new store for infinium data").withLongOpt("create").hasArg().withArgName("name").create('n');
  }
}
