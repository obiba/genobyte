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
package org.obiba.genobyte.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.FieldValueIterator;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.genobyte.GenotypingRecordStore;
import org.obiba.genobyte.GenotypingStore;
import org.obiba.genobyte.model.DefaultGenotypingField;
import org.obiba.genobyte.model.SnpAllele;
import org.obiba.genobyte.model.SnpCall;


/**
 * Base class for "Ped file" report formats. The format is described here http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml.
 * <p/>
 * Extending classes may link a report's Column to a field in the {@link BitwiseStore}. Some links may be optional, but if any
 * required link is not satisfied, an <tt>IllegalStateException</tt> is thrown upon generating a report.
 * <p/>
 * Extending classes must implement the {@link AbstractPedFileReport#convertColumnValue(Column, Object)} method to convert stored values
 * into the proper column type. For example, the GENDER column's valid values are 1, 2, Unknown or 0 while a stored gender value
 * may have the form "Male", "Female".
 */
public abstract class AbstractPedFileReport implements GenotypeReport {

  /** The order of this enum must match the order of the columns in the output file */
  public enum Column {
    FAMILY_ID, INDIVIDUAL_ID(true), PATERNAL_ID, MATERNAL_ID, GENDER, AFFECTION_STATUS(false, "-9");

    /** When true, the column must absolutely be linked to a field in the bitwise store, otherwise the report cannot be generated */
    private boolean linkRequired;
    
    /** When the value is missing for the column, this value will be used instead */
    private String missingValueToken = "0";

    private Column() {
      this(false);
    }

    private Column(boolean required) {
      this(required, "0");
    }

    private Column(boolean required, String token) {
      this.linkRequired = required;
      this.missingValueToken = token;
    }

    public boolean isLinkRequired() {
      return linkRequired;
    }
    
    public String getMissingValueToken() {
      return missingValueToken;
    }
  }

  private Map<Column, String> fieldNameMap_ = new HashMap<Column, String>();
  
  public int getNumberOfOutputFiles() {
    return 1;
  }

  public String getReportType() {
    return "pedfile";
  }

  public char getShortReportType() {
    return 'p';
  }

  public String getDefaultFileName(int fileNumber) {
    if(fileNumber == 0) {
      return "pedfile.ped";
    }
    return null;
  }

  /**
   * Links the {@link Column} c to the {@link BitwiseStore} field named <tt>fieldName</tt>.
   * When outputing values for the column, the field is used as the source. Each value is passed
   * to the {@link AbstractPedFileReport#convertColumnValue(Column, Object)} method.
   * @param c the columnd to link
   * @param fieldName the linked field's name
   */
  public void linkColumnToField(Column c, String fieldName) {
    fieldNameMap_.put(c, fieldName);
  }

  public void generateReport(GenotypingStore gs, QueryResult sampleMask, QueryResult assayMask, File ... outputFiles) {
    
    if(outputFiles == null || outputFiles.length != 1) {
      return;
    }
    
    for (Column column : Column.values()) {
      if(column.isLinkRequired() == true && this.fieldNameMap_.get(column) == null) {
        throw new IllegalStateException("The report column ["+column+"] must be linked to a field name. Make sure the method linkColumnToField is called when setting up the report object.");
      }
    }
    
    PrintStream ps;
    try {
      ps = new PrintStream(new FileOutputStream(outputFiles[0]));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }

    GenotypingRecordStore assays = gs.getAssayRecordStore();

    SnpAllele[][] assayAlleles = new SnpAllele[gs.getAssayCount()][];
    for (int i = 0; i < assayAlleles.length; i++) {
      assayAlleles[i] = getAssayAlleles(assays, i);
    }

    GenotypingRecordStore samples = gs.getSampleRecordStore();
    StringBuilder sb = new StringBuilder();
    for(int s = sampleMask.next(0); s != -1; s = sampleMask.next(s+1)) {
      Field sampleCalls = samples.getGenotypingField(DefaultGenotypingField.CALLS.getName(), samples.getRecordManager().getKey(s));
      if(sampleCalls == null) {
        continue;
      }

      for (Column column : Column.values()) {
        Object value = getColumnValue(column, gs.getSampleRecordStore().getStore(), s);
        sb.append(value).append(" ");
      }

      sb.ensureCapacity(sb.length() + assayMask.count() * 2);
      FieldValueIterator<SnpCall> genotypes = new FieldValueIterator<SnpCall>(sampleCalls, assayMask);
      while(genotypes.hasNext()) {
        FieldValueIterator<SnpCall>.FieldValue fv = genotypes.next();
        SnpCall call = fv.getValue();
        sb.append(convertCall(call, assayAlleles[fv.getIndex()]));
        if(genotypes.hasNext()) sb.append(" ");
      }
      ps.println(sb.toString());
      sb.setLength(0);
    }
    ps.close();
  }

  /** 
   * Converts the column value into the appropriate format/value for the PedFile report.
   * For example, the Gender column's allowed values are "1", "2", "Unknown" and "0".
   */
  protected abstract Object convertColumnValue(Column c, Object value);

  protected abstract SnpAllele[] getAssayAlleles(GenotypingRecordStore assays, int assayIndex);
  
  private String convertCall(SnpCall call, SnpAllele[] alleles) {
    if(call != null) {
      switch(call) {
        case A: return alleles[0].toString() + " " + alleles[0].toString();
        case B: return alleles[1].toString() + " " + alleles[1].toString();
        case H: return alleles[0].toString() + " " + alleles[1].toString();
        default:
      }
    }
    return "0 0";
  }

  /**
   * Utility method to get a field's value for the specified record index
   *  
   * @param c the column for which to get the value
   * @param bs the store from which to get the linked field (if any)
   * @param index the record's index
   * @return the converted value of the specified record for the specified column.
   */
  private Object getColumnValue(Column c, BitwiseStore bs, int index) {
    String fieldName = this.fieldNameMap_.get(c);
    Field f = null;
    if(fieldName != null) {
      f = bs.getField(fieldName);
      if(f != null) {
        Object value = f.getDictionary().reverseLookup(f.getValue(index));
        if(value != null) {
          try {
            value = convertColumnValue(c, value);
          } catch(RuntimeException e) {
            System.out.println("Cannot convert value ["+value+"] of type ["+value.getClass().getName()+"] for column ["+c+"]");
            throw e;
          }
        }
        return value != null ? value : c.getMissingValueToken();
      }
    }
    return c.getMissingValueToken();
  }

}
