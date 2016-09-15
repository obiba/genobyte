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

import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.Dictionary;
import org.obiba.bitwise.Field;

import java.util.*;

/**
 * Stores the data matrix to be displayed in the console screen.
 * An instance will contain several DisplayField objects, one for each field
 * with data to be displayed.
 * A record data will be splited over the DisplayField objects, using the same
 * index in each DisplayField.
 */

public class ResultDisplay {
  private Map<String, DisplayField> fields_ = new LinkedHashMap<String, DisplayField>();

  private int recordCount_ = 0;

  //Allow different kinds of outputs in the console
  public enum DisplayType {
    TABLE, PLAIN;
  }

  ;

  private DisplayType dt_ = DisplayType.TABLE;

  public ResultDisplay() {
    super();
  }

  /**
   * Constructor taking a list of field names, and preparing the data structure
   * to store results.
   *
   * @param pFields is a list of field names.
   */
  public ResultDisplay(List<String> pFields) {
    super();
    for (String fieldName : pFields) {
      addField(fieldName);
    }
  }

  /**
   * Sets the way to display resultset in the console.
   *
   * @param pDt the type of display.
   */
  public void setDisplayType(DisplayType pDt) {
    dt_ = pDt;
  }

  /**
   * Appends one extra field to the data structure.
   *
   * @param pName is the name of the field to be appended.
   */
  public void addField(String pName) {
    fields_.put(pName, new DisplayField(pName));
  }

  /**
   * Adds a new record in the data structure.
   *
   * @param pRecordData is a map where the key is a string of the field name, and
   *                    the value is the field value.
   */
  public void putRecord(Map<String, String> pRecordData) {
    Iterator<String> it = pRecordData.keySet().iterator();
    while (it.hasNext()) {
      String key = it.next();
      fields_.get(key).put(pRecordData.get(key));
    }
    recordCount_++;
  }

  /**
   * Adds a record to be displayed by this object, by first extracting its data from the store at a given
   * record index.
   *
   * @param bs    the <tt>BitwiseStore</tt>.
   * @param index the record index.
   */
  public void putRecord(BitwiseStore bs, int index) {
    Map<String, String> record = new HashMap<String, String>();
    for (String field : fields_.keySet()) {
      Field f = bs.getField(field);
      Dictionary dict = f.getDictionary();
      Object value = dict.reverseLookup(f.getValue(index));
      record.put(f.getName(), (value != null) ? value.toString() : "NULL");
    }
    putRecord(record);
  }

  /**
   * Gets the results outputed in the required type.
   *
   * @return A String with the whole result string.
   */
  public String getOutput() {
    if (dt_ == DisplayType.TABLE) {
      return getTableOutput();
    } else if (dt_ == DisplayType.PLAIN) {
      return getPlainOutput();
    } else {
      throw new RuntimeException("No display type was defined.");
    }
  }

  /**
   * Generates the search results as a plain output <tt>String</tt>.
   *
   * @return
   */
  private String getPlainOutput() {
    StringBuilder result = new StringBuilder();

    for (int i = 0; i < recordCount_; i++) {
      result.append("\nRecord ").append(i).append(": ");
      for (String key : fields_.keySet()) {
        String fieldValue = fields_.get(key).get(i);
        result.append("[").append(key).append(": ").append(fieldValue).append("] ");
      }
      result.append("\n");
    }

    return result.toString();
  }

  /**
   * Generates the search result <tt>String</tt> as a table.
   */
  private String getTableOutput() {
    StringBuilder output = new StringBuilder();
    StringBuilder boxLine = getBoxLine();

    output.append(boxLine).append("\n");
    output.append(getHeader());
    output.append(boxLine);

    //Print values. If there aren't any, don't print the last box line.
    StringBuilder valueLines = getValues();

    if (valueLines.length() > 0) {
      output.append("\n");
      output.append(getValues());
      output.append(boxLine);
    }

    return output.toString();
  }

  /**
   * Iterates throught all fields and compute how many characters there are in
   * total.
   *
   * @return The total of the longest character sequence for each field.
   */
  public int getTotalFieldSize() {
    int totalSize = 0;

    Iterator<String> it = fields_.keySet().iterator();
    while (it.hasNext()) {
      String key = it.next();
      totalSize += fields_.get(key).getSize();
    }

    return totalSize;
  }

  /**
   * Gets a horizontal line that will delimit the results box.
   *
   * @return The line as a StringBuilder.
   */
  private StringBuilder getBoxLine() {
    StringBuilder boxLine = new StringBuilder();
    int totalSize = getTotalFieldSize() + fields_.size() + 1;

    //Add whitespace before and after each value
    totalSize += (2 * fields_.size());

    boxLine.append(repeatChar("-", totalSize));
    return boxLine;
  }

  /**
   * Gets the results box header, which is the field name for each column.
   *
   * @return A StringBuilder of the line with the header information.
   */
  private StringBuilder getHeader() {
    StringBuilder header = new StringBuilder();

    Iterator<String> it = fields_.keySet().iterator();
    while (it.hasNext()) {
      String key = it.next();
      header.append("| ").append(key);
      header.append(
          repeatChar(" ", fields_.get(key).getSize() - key.length() + 1));  //+1 for the extra whitespace at the end
    }
    header.append("|\n");
    return header;
  }

  /**
   * Prepares a StringBuilder table with one record per row.
   *
   * @return A StringBuilder of all the lines representing the matching values.
   */
  private StringBuilder getValues() {
    StringBuilder result = new StringBuilder();

    for (int i = 0; i < recordCount_; i++) {
      Iterator<String> it = fields_.keySet().iterator();
      while (it.hasNext()) {
        String key = it.next();
        String fieldValue = fields_.get(key).get(i);
        result.append("| ").append(fieldValue);

        int extraSpace = fields_.get(key).getSize() - fieldValue.length() + 1; //+1 for the extra whitespace at the end
        result.append(repeatChar(" ", extraSpace));
      }
      result.append("|\n");
    }

    return result;
  }

  /**
   * Gets a StringBuilder containing the same character sequence repeated a
   * certain amount of times.
   *
   * @param pChar  is the character sequence to be repeated.
   * @param pCount is how many times it must be repeated.
   * @return The resulting StringBuilder.
   */
  private StringBuilder repeatChar(String pChar, int pCount) {
    StringBuilder result = new StringBuilder();
    for (int j = 0; j < pCount; j++) {
      result.append(pChar);
    }

    return result;
  }

  /**
   * Structure to pile up multiple records for one field, to be outputed in
   * a table.
   */
  private class DisplayField {
    private int maxSize_;

    private String displayName_;

    private List<String> data_;

    public DisplayField(String pDisplayName) {
      super();
      displayName_ = pDisplayName;
      data_ = new Vector<String>();
      maxSize_ = displayName_.length();
    }

    public void put(String pData) {
      data_.add(pData);
      if (pData.length() > maxSize_) {
        maxSize_ = pData.length();
      }
    }

    public String get(int pIndex) {
      return data_.get(pIndex);
    }

    public int getSize() {
      return maxSize_;
    }
  }
}
