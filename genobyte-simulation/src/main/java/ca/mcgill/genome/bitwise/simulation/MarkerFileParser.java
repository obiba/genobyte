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
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import ca.mcgill.genome.io.SeparatedValuesParser;
import ca.mcgill.genome.io.SeparatedValuesRow;
import ca.mcgill.genome.util.LazyLoadList;
import ca.mcgill.genome.util.LazyLoadListCallback;

public class MarkerFileParser implements LazyLoadListCallback<MarkerFileParser.MarkerFileEntry>{

  private int size_ = 0;
  private SeparatedValuesParser sepParser_ = null;

  private MarkerFileParser(File linkageFile) throws IOException {
    sepParser_ = new SeparatedValuesParser(linkageFile, SeparatedValuesParser.TAB);
    while(sepParser_.nextRow() != null) size_++;
    size_--;
    sepParser_.reset();
    sepParser_.nextRow();
  }

  static public MarkerFileParser parse(File linkageFile) throws IOException {
    return new MarkerFileParser(linkageFile);
  }

  public int getSize() { 
    return size_;
  }
  
  public LazyLoadList<MarkerFileParser.MarkerFileEntry> entries() {
    return new LazyLoadList<MarkerFileParser.MarkerFileEntry>(size_, this);
  }

  public List<MarkerFileEntry> load(int start, int size) {
    List<MarkerFileEntry> l = new LinkedList<MarkerFileEntry>();
    try {
      int index = sepParser_.getLineNumber() - 1;
      if(index > start) {
        sepParser_.reset();
        sepParser_.nextRow();
        index = sepParser_.getLineNumber() - 1;
      }
      while(index < start) {
        sepParser_.nextRow();
        index = sepParser_.getLineNumber() - 1;
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

  private MarkerFileEntry parseRow(SeparatedValuesRow row) {
    if(row == null) {
      return null;
    }
    MarkerFileEntry entry = new MarkerFileEntry();
    int col = 0;
    entry.setIndex(row.getIndex() - 1);
    entry.setName(row.getColumnValue(col++, String.class));
    entry.setMarkerName(row.getColumnValue(col++, String.class));
    entry.setAlleleA(row.getColumnValue(col++, String.class));
    entry.setAlleleB(row.getColumnValue(col++, String.class));
    entry.setStrand(row.getColumnValue(col++, String.class));
    entry.setChromosome(row.getColumnValue(col++, String.class));
    entry.setPosition(row.getColumnValue(col++, Integer.class));
    entry.setGenes(row.getColumnValue(col++, String.class));
    return entry;
  }

  public static class MarkerFileEntry {
    private int index_ = 0;
    private String name_ = null;
    private String markerName_ = null;
    private String alleleA_ = null;
    private String alleleB_ = null;
    private String strand_ = null;
    private String chromosome_ = null;
    private Integer position_ = null;
    private String genes_ = null;
    /**
     * @return the alleleA
     */
    public String getAlleleA() {
      return alleleA_;
    }
    /**
     * @return the alleleB
     */
    public String getAlleleB() {
      return alleleB_;
    }
    /**
     * @return the chromosome
     */
    public String getChromosome() {
      return chromosome_;
    }
    /**
     * @return the genes
     */
    public String getGenes() {
      return genes_;
    }
    /**
     * @return the index
     */
    public int getIndex() {
      return index_;
    }
    /**
     * @return the markerName
     */
    public String getMarkerName() {
      return markerName_;
    }
    /**
     * @return the name
     */
    public String getName() {
      return name_;
    }
    /**
     * @return the position
     */
    public Integer getPosition() {
      return position_;
    }
    /**
     * @return the strand
     */
    public String getStrand() {
      return strand_;
    }
    /**
     * @param alleleA the alleleA to set
     */
    public void setAlleleA(String alleleA) {
      alleleA_ = alleleA;
    }
    /**
     * @param alleleB the alleleB to set
     */
    public void setAlleleB(String alleleB) {
      alleleB_ = alleleB;
    }
    /**
     * @param chromosome the chromosome to set
     */
    public void setChromosome(String chromosome) {
      chromosome_ = chromosome;
    }
    /**
     * @param genes the genes to set
     */
    public void setGenes(String genes) {
      genes_ = genes;
    }
    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
      index_ = index;
    }
    /**
     * @param markerName the markerName to set
     */
    public void setMarkerName(String markerName) {
      markerName_ = markerName;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
      name_ = name;
    }
    /**
     * @param position the position to set
     */
    public void setPosition(Integer position) {
      position_ = position;
    }
    /**
     * @param strand the strand to set
     */
    public void setStrand(String strand) {
      strand_ = strand;
    }
    
  }
}
