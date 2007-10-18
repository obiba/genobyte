/*******************************************************************************
 * Copyright 2007(c) G�nome Qu�bec. All rights reserved.
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
import org.obiba.illumina.io.LocusXDnaReportFile;


public class LocusXDnaFileLoader implements FileTypeLoader {

  public String getFileType() {
    return "genotypes";
  }

  public char getShortFileType() {
    return 'g';
  }
  
  public boolean allowsMultipleFile() {
    return true;
  }
  
  public boolean requiresOpenStore() {
    return true;
  }

  public void loadFiles(CliContext context, File ... files) {
    for (int i = 0; i < files.length; i++) {
      File file = files[i];
      context.getOutput().println("Loading LocusXDna report ["+file.getName()+"]");
      LocusXDnaReportFile lxd = null; 
      try {
        lxd = new LocusXDnaReportFile(file);
      } catch (IOException e) {
        context.getOutput().println("Error parsing LocusXDna Report: " + e.getMessage());
        return;
      }
      long start = System.currentTimeMillis();
      InfiniumGenotypingStore store = (InfiniumGenotypingStore)context.getStore();
      try {
        store.startTransaction();
        store.getSampleRecordStore().loadGenotypes(context.getOutput(), lxd);
        store.commitTransaction();
      } catch (IOException e) {
        context.getOutput().println("Error loading genotypes: " + e.getMessage());

        // Skip other files...
        return;
      } finally {
        store.endTransaction();
      }
      long end = System.currentTimeMillis();
      context.getOutput().println("Loaded genotypes in " + ((end - start) / 1000d) + " seconds");
    }
  }
  
}
