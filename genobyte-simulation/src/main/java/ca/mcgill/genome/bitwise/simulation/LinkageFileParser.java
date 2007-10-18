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
package ca.mcgill.genome.bitwise.simulation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.LinkedList;
import java.util.List;

import ca.mcgill.genome.io.SeparatedValuesParser;
import ca.mcgill.genome.io.SeparatedValuesRow;
import ca.mcgill.genome.util.LazyLoadList;
import ca.mcgill.genome.util.LazyLoadListCallback;

public class LinkageFileParser implements LazyLoadListCallback<LinkageFileParser.LinkageFileEntry>{

  private File linkageFile_ = null;
  private Genomes genomes_ = null;
  private Genomes replicateGenomes_ = null;
  private int size_ = 30000;
  private SeparatedValuesParser sepParser_ = null;

  private LinkageFileParser(File linkageFile) throws IOException {
    linkageFile_ = linkageFile;
    sepParser_ = new SeparatedValuesParser(linkageFile_, SeparatedValuesParser.COMMA);
    LineNumberReader lnr = new LineNumberReader(new FileReader(linkageFile));
    size_ = 0;
    while(lnr.readLine() != null) size_++;
  }

  static public LinkageFileParser parse(File linkageFile) throws IOException {
    return new LinkageFileParser(linkageFile);
  }

  public List<LinkageFileParser.LinkageFileEntry> entries() {
    return new LazyLoadList<LinkageFileParser.LinkageFileEntry>(size_, this);
  }
  
  public void setGenomes(Genomes genomes) {
    this.genomes_ = genomes;
  }
  
  public void setReplicateGenomes(Genomes genomes) {
    this.replicateGenomes_ = genomes;
  }
  
  public int getSize() { 
    return size_;
  }

  public List<LinkageFileEntry> load(int start, int size) {
    List<LinkageFileEntry> l = new LinkedList<LinkageFileEntry>();
    try {
      if(sepParser_ == null) {
        sepParser_ = new SeparatedValuesParser(linkageFile_, SeparatedValuesParser.COMMA);
      }
      int index = sepParser_.getLineNumber();
      if(index > start) {
        sepParser_.reset();
        index = sepParser_.getLineNumber();
      }
      while(index < start) {
        sepParser_.nextRow();
        index = sepParser_.getLineNumber();
      }
      while(l.size() < size) {
        SeparatedValuesRow row = sepParser_.nextRow();
        if(row == null) {
          sepParser_.close();
          sepParser_ = null;
          break;
        }
        l.add(parseRow(row));
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return l;
  }

  private LinkageFileEntry parseRow(SeparatedValuesRow row) {
    LinkageFileEntry entry = new LinkageFileEntry();
    int col = 0;
    entry.setIndex(row.getIndex());
    entry.setSampleName(row.getColumnValue(col++, String.class));
    entry.setIndividualName(row.getColumnValue(col++, String.class));
    entry.setCase(row.getColumnValue(col++, Integer.class).intValue() == 1);
    entry.setMotherName(row.getColumnValue(col++, String.class));
    entry.setFatherName(row.getColumnValue(col++, String.class));
    String role = row.getColumnValue(col++, String.class);
    int genomeIndex = row.getColumnValue(col++, Integer.class);
    if(role.equalsIgnoreCase("S"))
      entry.setGenotypes(this.genomes_.get(genomeIndex));
    else 
      entry.setGenotypes(this.replicateGenomes_.get(genomeIndex));
    return entry;
  }

  public static class LinkageFileEntry {
    private int index_ = 0;
    private String sampleName_ = null;
    private String individualName_ = null;
    private boolean case_ = false;
    private String motherName_ = null;
    private String fatherName_ = null;

    private String genotypes_ = null;

    public int getIndex() {
      return index_;
    }
    public void setIndex(int index) {
      index_ = index;
    }
    /**
     * @return the case
     */
    public boolean isCase() {
      return case_;
    }

    /**
     * @return the fatherName
     */
    public String getFatherName() {
      return fatherName_;
    }

    /**
     * @return the genotypes
     */
    public String getGenotypes() {
      return genotypes_;
    }

    /**
     * @return the individualName
     */
    public String getIndividualName() {
      return individualName_;
    }

    /**
     * @return the motherName
     */
    public String getMotherName() {
      return motherName_;
    }

    /**
     * @return the sampleName
     */
    public String getSampleName() {
      return sampleName_;
    }

    /**
     * @param case1 the case to set
     */
    public void setCase(boolean case1) {
      case_ = case1;
    }

    /**
     * @param fatherName the fatherName to set
     */
    public void setFatherName(String fatherName) {
      fatherName_ = fatherName;
    }

    /**
     * @param genotypes the genotypes to set
     */
    public void setGenotypes(String genotypes) {
      genotypes_ = genotypes;
    }

    /**
     * @param individualName the individualName to set
     */
    public void setIndividualName(String individualName) {
      individualName_ = individualName;
    }

    /**
     * @param motherName the motherName to set
     */
    public void setMotherName(String motherName) {
      motherName_ = motherName;
    }

    /**
     * @param sampleName the sampleName to set
     */
    public void setSampleName(String sampleName) {
      sampleName_ = sampleName;
    }
    
    
  }
}
