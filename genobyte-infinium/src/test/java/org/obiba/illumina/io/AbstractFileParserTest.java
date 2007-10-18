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
import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

public abstract class AbstractFileParserTest extends TestCase {

  protected InputStream loadEmptyFileAsStream() {
    return loadResourceAsStream("emptyFile.csv");
  }

  protected File loadEmptyFileAsFile() {
    return loadResourceAsFile("emptyFile.csv");
  }
  
  protected InputStream loadResourceAsStream(String name) {
    InputStream is = this.getClass().getResourceAsStream(name);
    if(is == null) throw new IllegalStateException("Cannot open test resource \""+name+"\"");
    return is;
  }

  protected File loadResourceAsFile(String name) {
    URL resource = this.getClass().getResource(name);
    if(resource.getProtocol().equalsIgnoreCase("file") == false) {
      throw new IllegalStateException("Cannot load resource, returned URL is not a file://");
    }
    String filename = resource.getFile();
    return new File(filename);
  }

}
