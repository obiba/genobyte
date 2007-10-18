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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class LocusXDnaReportFile {

  private static final String SEPARATOR = ",";
  private static final char SEPARATOR_CHAR = ',';
  
  private static final String PROJECT_ID_HEADER = "ProjectId";
  private static final String NUMBER_DNA_HEADER = "Number DNA";
  private static final String NUMBER_LOCI_HEADER = "Number Loci";

  private static final String RECORD_TYPE_SECTION_PREFIX = "oligoPoolId,recordType,data";
  private static final String LOCUS_ID_SECTION_PREFIX = "oligoPoolId,GTS LocusId,data";
  private static final String DNA_DATA_SECTION_PREFIX = "instituteLabel,plateWell";
  
  private String oligoPoolId_ = null;
  private Integer projectId_ = null;
  private String projectName_ = null;
  private Integer numberDna_ = null;
  private Integer numberLoci_ = null;
  
  private int[] gtsLocusId_ = null;

  private Map<String, Long> recordTypeFileOffset_ = new HashMap<String, Long>();

  private long firstSampleEntryOffset_ = -1;

  private RandomAccessFile raf_ = null; 

  public LocusXDnaReportFile(File file) throws IOException {
    raf_ = new RandomAccessFile(file, "r");
    preprocess();
  }

  public void close() {
    try {
      raf_.close();
    } catch (IOException e) {
      // ignore
    }
  }

  public String getOligoPoolId() {
    return oligoPoolId_;
  }
  
  public Integer getProjectId() {
    return projectId_;
  }
  
  public String getProjectName() {
    return projectName_;    
  }
  
  public Integer getNumberDna() {
    return numberDna_;
  }
  
  public Integer getNumberLoci() {
    return numberLoci_;
  }

  public String[] getOligoPoolRecordData(String recordType) throws IOException {
    Long position = this.recordTypeFileOffset_.get(recordType);
    if(position == null) {
      return null;
    }

    raf_.seek(position);
    String line = raf_.readLine();
    return getRecordTypeData(line); 
  }
  
  public int[] getGtsLocusId() {
    return gtsLocusId_;
  }
  
  public Iterator<LocusXDnaReportSampleDataEntry> getEntries() throws IOException {
    return new EntryIterator(this.firstSampleEntryOffset_);
  }

  private void preprocess() throws IOException {
    
    String line = raf_.readLine();
    int lineNumber = 1;
    while(line != null && line.startsWith(RECORD_TYPE_SECTION_PREFIX) == false) {
      if(lineNumber++ == 2) {
        this.oligoPoolId_ = line.split(SEPARATOR)[0];
      } else if(line.contains(PROJECT_ID_HEADER)) {
        String values[] = line.split(SEPARATOR);
        this.projectId_ = new Integer(values[0]);
        this.projectName_ = values[2];
      } else if(line.contains(NUMBER_DNA_HEADER)) {
        String values[] = line.split(SEPARATOR);
        this.numberDna_ = new Integer(values[0]);
      } else if(line.contains(NUMBER_LOCI_HEADER)) {
        String values[] = line.split(SEPARATOR);
        this.numberLoci_ = new Integer(values[0]);
      }

      line = raf_.readLine();
    }

    if(line == null) {
      throw new IOException("Invalid file format. OligoPool record type section is missing.");
    }

    // Skip header line
    long position = raf_.getFilePointer();
    line = raf_.readLine();
    while(line != null && line.startsWith(LOCUS_ID_SECTION_PREFIX) == false) {
      // The reader is at the start of the record data line, get its position so we can go back later
      String recordType = getRecordType(line);
      this.recordTypeFileOffset_.put(recordType, position);

      position = raf_.getFilePointer();
      line = raf_.readLine();
    }

    if(line == null) {
      throw new IOException("Invalid file format. GTS Locus Id section is missing.");
    }

    // Skip header line
    position = raf_.getFilePointer();
    line = raf_.readLine();
    while(line != null && line.startsWith(DNA_DATA_SECTION_PREFIX) == false) {
      // Parse the GTS Locus Ids
      String[] values = line.split(SEPARATOR);
      gtsLocusId_ = new int[values.length-3];
      for (int i = 3; i < values.length; i++) {
        gtsLocusId_[i-3] = Integer.parseInt(values[i]);
      }
      position = raf_.getFilePointer();
      line = raf_.readLine();
    }

    if(line == null) {
      throw new IOException("Invalid file format. DNA data section is missing.");
    }

    // The reader is at the start of the first sample data line, get its position so we can go back later
    firstSampleEntryOffset_ = raf_.getFilePointer();
  }

  /**
   * Extracts the value of the column "recordType" from the string.
   *
   * @param line the string to parse
   * @return the value of the column "recordType" in the specified line.
   */
  private String getRecordType(String line) {
    int start = line.indexOf(SEPARATOR_CHAR) + 1;
    int end = line.indexOf(SEPARATOR_CHAR, start);
    return line.substring(start, end);
  }
  
  /**
   * Extracts the "data" column from the string.
   * 
   * @param line the string to parse
   * @return the value of the column "data" in the specified line.
   */
  private String[] getRecordTypeData(String line) {
    // Find the third comma
    int start = line.indexOf(SEPARATOR_CHAR);
    start = line.indexOf(SEPARATOR_CHAR, start+1);
    start = line.indexOf(SEPARATOR_CHAR, start+1) + 1;
    // The rest of the line is the data portion
    return line.substring(start).split(SEPARATOR);
  }
  

  private class EntryIterator implements Iterator<LocusXDnaReportSampleDataEntry> {
    
    private LocusXDnaReportSampleDataEntry next = null;
    private long nextPosition = -1;
    
    EntryIterator(long firstIndex) throws IOException {
      nextPosition = firstIndex;
      setNext();
    }
    
    public boolean hasNext() {
      return next != null;
    }

    public LocusXDnaReportSampleDataEntry next() {
      LocusXDnaReportSampleDataEntry temp = next;
      try {
        setNext();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      return temp;
    }

    public void remove() {
      throw new UnsupportedOperationException("cannot remove() entries");
    }
    
    private void setNext()throws IOException {
      raf_.seek(nextPosition);
      String line1 = raf_.readLine();
      if(line1 == null) {
        next = null;
        nextPosition = -1;
        return;
      }
      String line2 = raf_.readLine();
      nextPosition = raf_.getFilePointer();

      next = new LocusXDnaReportSampleDataEntry(line1, line2);
      if(next.getCalls().length != getNumberLoci()) {
        System.out.println("calls=["+Arrays.toString(next.getCalls())+"]");
        throw new IOException("Sample ["+next.getInstituteLabel()+"] has "+next.getCalls().length+" genotypes. Expected "+getNumberLoci());
      }
    }
  }
}
