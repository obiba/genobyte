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
import org.obiba.illumina.io.ManifestFile;


public class ManifestFileLoader implements FileTypeLoader {

  public String getFileType() {
    return "assays";
  }

  public char getShortFileType() {
    return 'a';
  }
  
  public boolean allowsMultipleFile() {
    return false;
  }
  
  public boolean requiresOpenStore() {
    return true;
  }

  public void loadFiles(CliContext context, File ... files) {
    File manifestFile = files[0];
    context.getOutput().println("Loading manifest ["+manifestFile.getName()+"]");
    ManifestFile manifest;
    InfiniumGenotypingStore store = (InfiniumGenotypingStore)context.getStore();
    try {
      manifest = new ManifestFile(manifestFile);
    } catch (IOException e) {
      context.getOutput().println("Error parsing manifest: " + e.getMessage());
      return;
    }
    
    try {
      store.startTransaction();
      store.getAssayRecordStore().loadAssays(manifest);
      store.commitTransaction();
      context.getOutput().println("Loaded " + manifest.getSnpCount() + " assays from manifest.");
    } finally {
      store.endTransaction();
    }
  }

}
