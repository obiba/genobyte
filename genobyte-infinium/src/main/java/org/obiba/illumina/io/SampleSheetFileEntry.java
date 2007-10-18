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

public class SampleSheetFileEntry {

  public enum Gender {
    Female, Male, Unknown
  }

  private String sampleId_ = null;
  private String sampleName_ = null;
  private String samplePlate_ = null;
  private String sampleWell_ = null;
  private String sentrixBarcodeA_ = null;
  private String sentrixPositionA_ = null;
  private Gender gender_ = null;
  private String sampleGroup_ = null;
  private String replicates_ = null;
  private String parent1_ = null;
  private String parent2_ = null;
  private String path_ = null;
  private String reference_ = null;

  public String getSampleId() {
    return sampleId_;
  }
  public String getSampleName() {
    return sampleName_;
  }
  public String getSamplePlate() {
    return samplePlate_;
  }
  public String getSampleWell() {
    return sampleWell_;
  }
  public String getSentrixBarcodeA() {
    return sentrixBarcodeA_;
  }
  public String getSentrixPositionA() {
    return sentrixPositionA_;
  }
  public Gender getGender() {
    return gender_;
  }
  public String getSampleGroup() {
    return sampleGroup_;
  }
  public String getReplicates() {
    return replicates_;
  }
  public String getParent1() {
    return parent1_;
  }
  public String getParent2() {
    return parent2_;
  }
  public String getPath() {
    return path_;
  }
  public String getReference() {
    return reference_;
  }

  static SampleSheetFileEntry read(String columns[], String line) {
    SampleSheetFileEntry newEntry = new SampleSheetFileEntry();
    
    String tokens[] = line.split(",");
    for (int i = 0; i < tokens.length; i++) {
      String columnName = columns[i];
      String value = tokens[i];
      if(value != null && value.length() == 0) {
        value = null;
      }

      if ("Sample_Name".equalsIgnoreCase(columnName)) {
        newEntry.sampleName_ = value;
      } else if ("Sample_ID".equalsIgnoreCase(columnName)) {
        newEntry.sampleId_ = value;
      } else if ("Sample_Plate".equalsIgnoreCase(columnName)) {
        newEntry.samplePlate_ = value;
      } else if ("Sample_Well".equalsIgnoreCase(columnName)) {
        newEntry.sampleWell_ = value;
      } else if ("SentrixBarcode_A".equalsIgnoreCase(columnName)) {
        newEntry.sentrixBarcodeA_ = value;
      } else if ("SentrixPosition_A".equalsIgnoreCase(columnName)) {
        newEntry.sentrixPositionA_ = value;
      } else if ("Gender".equalsIgnoreCase(columnName)) {
        try {
          newEntry.gender_ = Gender.valueOf(value);
        } catch(RuntimeException e) {
          newEntry.gender_ = Gender.Unknown;
        }
      } else if ("Sample_Group".equalsIgnoreCase(columnName)) {
        newEntry.sampleGroup_ = value;
      } else if ("Replicates".equalsIgnoreCase(columnName)) { // Documentation says "Replicates"
        newEntry.replicates_ = value;
      } else if ("Replicate".equalsIgnoreCase(columnName)) { // I've seen lots of existing samples sheets with "Replicate"
        newEntry.replicates_ = value;
      } else if ("Parent1".equalsIgnoreCase(columnName)) {
        newEntry.parent1_ = value;
      } else if ("Parent2".equalsIgnoreCase(columnName)) {
        newEntry.parent2_ = value;
      } else if ("Path".equalsIgnoreCase(columnName)) {
        newEntry.path_ = value;
      } else if ("Reference".equalsIgnoreCase(columnName)) {
        newEntry.reference_ = value;
      }
    }
    return newEntry;
  }
}
