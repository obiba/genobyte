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
package org.obiba.genobyte.statistic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.obiba.bitwise.AbstractField;

/**
 * Provides methods to generate CSV reports for a <tt>StatsPool</tt>. For any column included in the report,
 * values will be included by using the toString() method.
 */
public class CsvReport implements StatsDigester {

  public static final String SEPARATOR = ",";

  public static final char SEPARATOR_CHAR = ',';

  /** Contains the field names that will be displayed on top of the CSV report. */
  private Map<String, String> headers_ = null;

  /** Contains the name of the fields which content will be displayed on the report. */
  private List<ReportField> includedFields_ = null;

  private PrintStream output_ = null;

  public CsvReport() {
    this(System.out);
  }

  public CsvReport(File outFile) throws FileNotFoundException {
    this(new PrintStream(new FileOutputStream(outFile)));
  }

  public CsvReport(PrintStream ps) {
    headers_ = new HashMap<String, String>();
    includedFields_ = new LinkedList<ReportField>();
    output_ = ps;
  }

  public void setOutput(PrintStream output) {
    this.output_ = output;
  }

  public PrintStream getOutput() {
    return this.output_;
  }

  //TODO: Add header, a method to add to the head of the file some data that isn't organized by columns (such
  //as a sample count, etc.)

  /**
   * Sets the name of a Field column header to something else than its default name.
   * @param pFieldName the name of the field.
   * @param pHeader the new header to put on top of the column.
   */
  public void setColumnHeader(String pFieldName, String pHeader) {
    headers_.put(pFieldName, pHeader);
  }

  /**
   * Adds a field to be displayed in the report. The Field display order will be the one in which they are added with this method.
   * @param pField the field to be included in the report.
   */
  public void addPooledField(String pField) {
    if(pField == null) throw new IllegalArgumentException("Field name cannot be null.");
    includedFields_.add(new ReportField(pField, "pool"));
  }

  public void addStoredField(String pField) {
    if(pField == null) throw new IllegalArgumentException("Field name cannot be null.");
    includedFields_.add(new ReportField(pField, "store"));
  }

  private AbstractField getField(StatsPool<?, ?> pPool, ReportField pRf) {
    AbstractField field;
    if(pRf.getType().equals("pool")) {
      field = pPool.getPooledField(pRf.getName());
      if(field == null) {
        throw new IllegalArgumentException(
            "The field [" + pRf.getName() + "] could not be found in the pool of statistics.");
      }
    } else {
      field = pPool.getGenotypingRecordStore().getStore().getField(pRf.getName());
      if(field == null) {
        throw new IllegalArgumentException("The field [" + pRf.getName() + "] could not be found in the store.");
      }
    }

    return field;
  }

  //@see org.obiba.bitwise.genotyping.statistic.StatsDigester.digest()
  public void digest(StatsPool<?, ?> pPool) {
    //Print report headers
    Iterator<ReportField> i = includedFields_.iterator();
    Map<String, AbstractField> fieldMap = new LinkedHashMap<String, AbstractField>();

    while(i.hasNext()) {
      ReportField rf = i.next();
      AbstractField field = getField(pPool, rf);
      fieldMap.put(rf.getName(), field);

      String name = field.getName();
      if(headers_.containsKey(name)) {
        name = headers_.get(name);
      }
      output_.print(escapeSeparator(name));
      if(i.hasNext()) {
        output_.print(SEPARATOR_CHAR);
      }
    }

    output_.println();

    //Iterating on all requested records
    for(int currentRecord = pPool.getRecordMask().next(0);
        currentRecord != -1; currentRecord = pPool.getRecordMask().next(currentRecord + 1)) {
      Iterator<String> j = fieldMap.keySet().iterator();

      while(j.hasNext()) {
        String fieldName = j.next();
        AbstractField field = fieldMap.get(fieldName);

        //Output value if there is one
        Object value = field.getDictionary().reverseLookup(field.getValue(currentRecord));
        if(value != null) {
          output_.print(escapeSeparator(value.toString()));
        }

        if(j.hasNext()) {
          output_.print(SEPARATOR_CHAR);
        }
      }

      output_.println();
    }
    output_.flush();
  }

  /**
   * Encloses csv field values in double quotes if a column separating character in found in the value. 
   * @param value the orginal value.
   * @return the value, enclosed in double quotes if needed.
   */
  private String escapeSeparator(String value) {
    if(value != null && value.indexOf(SEPARATOR_CHAR) != -1) {
      return "\"" + value + "\"";
    }
    return value;
  }

  private class ReportField {
    private String fieldName = null;

    private String fieldType = null;

    public ReportField(String pName, String pType) {
      fieldName = pName;
      fieldType = pType;
    }

    public String getName() {
      return fieldName;
    }

    public String getType() {
      return fieldType;
    }
  }

}
