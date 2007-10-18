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
package org.obiba.illumina.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SampleSheetFile {
  
  private static final String SEPARATOR = ",";
  private static final String HEADER_TOKEN = "[Header]";
  private static final String MANIFESTS_TOKEN = "[Manifests]";
  private static final String DATA_TOKEN = "[Data]";

  private Map<String, String> headerValues_ = new HashMap<String, String>();
  
  private List<String> manifests_ = new LinkedList<String>();
  private Map<String, String> manifestFiles_ = new HashMap<String, String>();
  
  private List<SampleSheetFileEntry> samples_ = new LinkedList<SampleSheetFileEntry>();

  public SampleSheetFile(File file) throws IOException {
    this(new FileReader(file));
  }
  
  public SampleSheetFile(InputStream is) throws IOException {
    this(new InputStreamReader(is));
  }
  
  public SampleSheetFile(Reader r) throws IOException {
    read(new LineNumberReader(r));
  }

  public String getHeaderValue(String key) {
    return headerValues_.get(key);
  }

  public List<String> getManifests() {
    return Collections.unmodifiableList(manifests_);
  }

  public String getManifestFile(String manifest) {
    return manifestFiles_.get(manifest);
  }

  public List<SampleSheetFileEntry> getSampleData() {
    return Collections.unmodifiableList(samples_);
  }

  private void read(LineNumberReader lnr) throws IOException {
    // Skip everything before header token
    String line = lnr.readLine();
    while(line != null && line.toLowerCase().startsWith(HEADER_TOKEN.toLowerCase()) == false && line.toLowerCase().startsWith(MANIFESTS_TOKEN.toLowerCase()) == false) {
      line = lnr.readLine();
    }

    if(line == null) {
      throw new IOException("Invalid sample sheet. Cannot find "+MANIFESTS_TOKEN+" token.");
    }
    

    // Parse optional [Header] portion
    if(line.toLowerCase().startsWith(HEADER_TOKEN.toLowerCase())) {
      // Skip token
      line = lnr.readLine();
      while(line != null && line.toLowerCase().startsWith(MANIFESTS_TOKEN.toLowerCase()) == false) {
        String[] values = line.split(SEPARATOR);
        if(values.length >= 2) {
          headerValues_.put(values[0], values[1]);
        } else if(values.length == 1) {
          headerValues_.put(values[0], "");
        }
        line = lnr.readLine();
      }
    }

    // Skip token
    line = lnr.readLine();
    while(line != null && line.toLowerCase().startsWith(DATA_TOKEN.toLowerCase()) == false) {
      String[] values = line.split(SEPARATOR);
      String manifest = values[0];
      manifests_.add(manifest);
      manifestFiles_.put(manifest, values[1]);
      line = lnr.readLine();
    }

    // Skip token
    line = lnr.readLine();
    String columns[] = line.split(SEPARATOR);
    line = lnr.readLine();
    while(line != null) {
      if(line.length() > 0) samples_.add(SampleSheetFileEntry.read(columns, line));
      line = lnr.readLine();
    }
  }

}