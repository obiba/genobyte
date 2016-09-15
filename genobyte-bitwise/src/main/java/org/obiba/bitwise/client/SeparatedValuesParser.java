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
package org.obiba.bitwise.client;

import org.obiba.bitwise.util.StringUtil;

import java.io.*;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Utility class to help parse a separated value file (ie: csv, tsv, etc.)
 *
 * @author plaflamm
 *
 * <pre>
 * Date       Author      Changes
 * 21/02/2005 plaflamm    Creation
 * </pre>
 */
public class SeparatedValuesParser {

  public static final String COMMA = ",";

  public static final String TAB = "\t";

  public static final String SPACE = " ";

  private String separator_ = null;

  private File file_ = null;

  private String enclosedBy_ = null;

  private int currentLineIndex_ = 0;

  private String currentLine_ = null;

  private String[] currentRowValues_ = null;

  private LineNumberReader lnr_ = null;

  private SeparatedValuesRow row_ = new SeparatedValuesRowImpl();

  public SeparatedValuesParser(File file) throws IOException {
    this(file, COMMA);
  }

  public SeparatedValuesParser(File file, String separator) throws IOException {
    this(file, separator, null);
  }

  public SeparatedValuesParser(File file, String separator, String enclosedBy) throws IOException {
    enclosedBy_ = enclosedBy;
    separator_ = separator;
    file_ = file;
    reset();
  }

  public SeparatedValuesRow nextRow() throws IOException {
    if(setNextRow() == false) {
      return null;
    }
    return row_;
  }

  public int getLineNumber() {
    return lnr_ != null ? lnr_.getLineNumber() : -1;
  }

  public void reset() throws IOException {
    if(lnr_ != null) {
      lnr_.close();
    }

    lnr_ = new LineNumberReader(new InputStreamReader(getInputStream(file_)));
  }

  public void close() throws IOException {
    if(lnr_ != null) {
      lnr_.close();
    }
  }

  private boolean setNextRow() throws IOException {

    currentLineIndex_ = lnr_.getLineNumber();
    currentLine_ = lnr_.readLine();
    // Skip empty lines
    while(currentLine_ != null && StringUtil.isEmptyString(currentLine_) == true) {
      currentLine_ = lnr_.readLine();
    }

    if(currentLine_ == null) {
      return false;
    }

    currentRowValues_ = extractValues();
    return true;
  }

  private String[] extractValues() throws IOException {
    String rowValues[] = currentLine_.split(separator_, -1);
    if(enclosedBy_ == null) {
      return rowValues;
    }

    List values = new ArrayList();
    StringBuffer buffer = null;

    for(int i = 0; i < rowValues.length; i++) {
      String token = rowValues[i];

      int firstIndex = token.indexOf(enclosedBy_);
      int lastIndex = token.lastIndexOf(enclosedBy_);

      // Explicitly test both index even though it is redundant.
      if(firstIndex == -1 && lastIndex == -1) {
        if(buffer != null) {
          // Part of an enclosed value that also contains a separator
          // Put the separator back into the buffer
          buffer.append(separator_).append(token);
        } else {
          values.add(token);
        }
      } else if(firstIndex == lastIndex) {
        if(buffer == null) {
          // Opening enclosing string without closing.
          buffer = new StringBuffer(token);
        } else {
          // Token is the closing part
          buffer.append(separator_).append(token);
          values.add(buffer.toString().replaceAll(enclosedBy_, ""));
          buffer = null;
        }
      } else if(firstIndex != lastIndex) {
        if(buffer != null) {
          // We cannot have two enclosing characters in one token when an unterminated token is being processed. 
          throw new IOException("Error on line " + lnr_.getLineNumber() + ": unterminated enclosing string.");
        }
        values.add(token.replaceAll(enclosedBy_, ""));
      }
    }

    if(buffer != null) {
      // We cannot have two enclosing characters in obnw token when an unterminated token is being processed. 
      throw new IOException("Error on line " + lnr_.getLineNumber() + ": unterminated enclosing string.");
    }

    String[] valueArray = (String[]) values.toArray(new String[0]);
    values.clear();
    values = null;
    return valueArray;
  }

  private InputStream getInputStream(File f) throws IOException {
    InputStream is = new FileInputStream(f);

    // Magic number is 2 bytes long, but the GZIPInputStream.GZIP_MAGIC constant is an int (4 bytes)
    byte magicBytes[] = new byte[4];
    // Read 2 bytes
    is.read(magicBytes, 0, 2);
    is.close();

    // Make an int out of the 4 bytes
    ByteBuffer bb = ByteBuffer.wrap(magicBytes);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    int magicNumber = bb.getInt();
    if(magicNumber == GZIPInputStream.GZIP_MAGIC) {
      return new LargeGZIPInputStream(new FileInputStream(f));
    }
    return new FileInputStream(f);
  }

  private class SeparatedValuesRowImpl implements SeparatedValuesRow {

    /*
     * @see ca.mcgill.genome.io.Row#getIndex()
     */
    public int getIndex() {
      return currentLineIndex_;
    }

    /*
     * @see ca.mcgill.genome.io.SeparatedValuesRow#getColumnCount()
     */
    public int getColumnCount() {
      return currentRowValues_.length;
    }

    /*
     * @see ca.mcgill.genome.io.Row#getColumnValue(int, java.lang.Class)
     */
    public <T> T getColumnValue(int index, Class<T> type) throws IllegalArgumentException {

      if(index < 0 || index >= currentRowValues_.length) {
        throw new IllegalArgumentException("Column index=[" + index + "] does not exist.");
      }

      String value = currentRowValues_[index];
      if(StringUtil.isEmptyString(value)) {
        return null;
      }

      if(String.class == type) {
        // Make a copy of value, otherwise the VM may keep a reference to the currentRowValues_ array
        return (T) String.copyValueOf(value.toCharArray());
      }

      try {
        Class parameterTypes[] = new Class[] { String.class };
        Constructor ctor = type.getConstructor(parameterTypes);
        if(ctor != null) {
          Object param[] = new Object[] { value };
          return (T) ctor.newInstance(param);
        }
      } catch(Exception e) {
        throw new IllegalArgumentException(e);
      }

      throw new IllegalArgumentException("Cannot create an object of type=[" + type.getName() + "].");
    }
  }
}
