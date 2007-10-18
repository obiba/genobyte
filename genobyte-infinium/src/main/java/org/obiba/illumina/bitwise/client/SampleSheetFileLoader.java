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

import java.io.File;
import java.io.IOException;

import org.obiba.genobyte.cli.CliContext;
import org.obiba.genobyte.cli.LoadFileCommand.FileTypeLoader;
import org.obiba.illumina.bitwise.InfiniumGenotypingStore;
import org.obiba.illumina.io.SampleSheetFile;


public class SampleSheetFileLoader implements FileTypeLoader {

  public String getFileType() {
    return "samples";
  }

  public char getShortFileType() {
    return 's';
  }

  public boolean allowsMultipleFile() {
    return true;
  }
  
  public boolean requiresOpenStore() {
    return true;
  }

  public void loadFiles(CliContext context, File ... files) {
    int total = 0;
    InfiniumGenotypingStore store = (InfiniumGenotypingStore)context.getStore();
    for (int i = 0; i < files.length; i++) {
      File file = files[i];
      context.getOutput().println("Loading sample sheet ["+file.getName()+"]");
      SampleSheetFile ssf;
      try {
        ssf = new SampleSheetFile(file);
      } catch (IOException e) {
        context.getOutput().println("Error parsing sample sheet: " + e.getMessage());
        return;
      }
      try {
        store.startTransaction();
        store.getSampleRecordStore().loadSamples(ssf);
        context.getOutput().println("Loaded " + ssf.getSampleData().size() + " samples from sample sheet.");
        total += ssf.getSampleData().size();
        store.commitTransaction();
      } finally {
        store.endTransaction();
      }
    }
    if(files.length > 1) {
      context.getOutput().println("Loaded " + total + " samples from "+files.length+" sample sheets.");
    }
  }

}
