/*******************************************************************************
 * Copyright 2007(c) Genome Quebec. All rights reserved.
 * <p>
 * This file is part of GenoByte.
 * <p>
 * GenoByte is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * <p>
 * GenoByte is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.obiba.bitwise.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

/**
 * {@link HuffmanSeedProvider} implementation that returns one <tt>String</tt> 
 * per line of the specified input. 
 */
public class FileLineIterator implements Iterator<String>, HuffmanSeedProvider {

  private BufferedReader reader_ = null;

  private String next_ = null;

  /**
   * Uses the specified filename as the input for reading lines.
   *
   * @param filename the name of the the file to read 
   * @throws IOException when an error occurs while reading the file
   */
  public FileLineIterator(String filename) throws IOException {
    this(new File(filename));
  }

  /**
   * Uses the specified file as the input for reading lines.
   * @param file the actual file to read
   * @throws IOException when an error occurs while reading the file
   */
  public FileLineIterator(File file) throws IOException {
    this(new FileReader(file));
  }

  /**
   * Uses the specified <tt>Reader</tt> instance as the input for reading lines.
   * @param r the input of lines
   * @throws IOException when an error occurs while reading the input
   */
  public FileLineIterator(Reader r) throws IOException {
    reader_ = new BufferedReader(r);
    next_ = reader_.readLine();
  }

  /**
   * Indicates whether there are still lines to read in the file.
   * @return <tt>true</tt> if there are remaining lines, <tt>false</tt> if there aren't.
   */
  public boolean hasNext() {
    return next_ != null;
  }

  /**
   * Gets the next unread line in the file.
   * @return The <tt>String</tt> with content of the next line.
   */
  public String next() {
    String temp = next_;
    try {
      next_ = reader_.readLine();
      if(next_ == null) {
        reader_.close();
      }
    } catch(IOException e) {
      throw new RuntimeException();
    }
    return temp;
  }

  /**
   * This method is not supported by this implementation of <tt>Iterator</tt>.
   *
   * @throws UnsupportedOperationException always. 
   */
  public void remove() {
    throw new UnsupportedOperationException();
  }

  public Iterator<String> getSeed() {
    return this;
  }

}
