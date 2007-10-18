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
package ca.mcgill.genome.hapmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

public class HapMapGenotypeFileParser {

  private File file_ = null;
  private int assayCount_ = 0;
  private ArrayList<String> sampleNames_ = new ArrayList<String>();

  public HapMapGenotypeFileParser(File file) throws IOException, FileNotFoundException {
    super();
    file_ = file;
    parseHeader();
  }
  
  public List<String> getSamples() {
    return Collections.unmodifiableList(sampleNames_);
  }
  
  public int assayCount() {
    return assayCount_;
  }

  public int sampleCount() {
    return sampleNames_.size();
  }
  
  public Iterator<Assay> iterator() throws FileNotFoundException, IOException {
    return new AssayIterator();
  }

  private void parseHeader() throws IOException {
    String header[] = {"rs#","SNPalleles","chrom","pos","strand","genome_build","center","protLSID","assayLSID","panelLSID","QC_code"};
    LineNumberReader lnr = new LineNumberReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file_))));
    Scanner lineScanner = new Scanner(lnr.readLine());
    
    int h = 0;
    while (lineScanner.hasNext()) {
      String value = lineScanner.next();
      if(value.equals(header[h]) == false) {
        throw new IllegalStateException("Cannot parse HapMap file=["+file_.getName()+"]: invalid header. Expected ["+header[h]+"] parsed ["+value+"]");
      }
      if(++h == header.length) {
        break;
      }
    }
    // Parse sample names
    while (lineScanner.hasNext()) {
      sampleNames_.add(lineScanner.next());
    }

    while(lnr.readLine() != null) {
      assayCount_++;
    }
  }
  
  private class AssayIterator implements Iterator<Assay> {
    LineNumberReader lnr_ = null;
    Assay next_ = null;
    
    private AssayIterator() throws FileNotFoundException, IOException {
      lnr_ = new LineNumberReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file_))));
      // skip header
      lnr_.readLine();
      internalNext();
    }

    public boolean hasNext() {
      return next_ != null;
    }

    public Assay next() {
      Assay temp = next_;
      try {
        internalNext();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      return temp;
    }

    public void remove() {
      throw new UnsupportedOperationException("Iterator.remove");
    }

    private void internalNext() throws IOException {
      next_ = null;
      String nextLine = lnr_.readLine();
      if(nextLine == null) {
        return;
      }
      next_ = new Assay();
      Scanner lineScanner = new Scanner(nextLine);
      next_.rs_ = lineScanner.next();
      next_.alleles_ = lineScanner.next();
      next_.chrom_ = lineScanner.next();
      next_.pos_ = lineScanner.nextInt();
      next_.strand_ = lineScanner.next();
      next_.genome_build_ = lineScanner.next();
      next_.center_ = lineScanner.next();
      next_.protLsid_ = lineScanner.next();
      next_.assayLsid_ = lineScanner.next();
      next_.panelLsid_ = lineScanner.next();
      next_.qcCode_ = lineScanner.next();
      
      for(int i = 0; i < sampleNames_.size(); i++) {
        Genotype g = new Genotype();
        g.index_ = i;
        g.genotype_ = lineScanner.next();
        next_.genotypes_.add(g);
      }
    }
  }

  public class Assay {
    String rs_;
    String alleles_;
    String chrom_;
    Integer pos_;
    String strand_;
    String genome_build_;
    String center_;
    String protLsid_;
    String assayLsid_;
    String panelLsid_;
    String qcCode_;

    ArrayList<Genotype> genotypes_ = new ArrayList<Genotype>();

    /**
     * @return Returns the alleles.
     */
    public String getAlleles() {
      return alleles_;
    }

    /**
     * @return Returns the assayLsid.
     */
    public String getAssayLsid() {
      return assayLsid_;
    }

    /**
     * @return Returns the center.
     */
    public String getCenter() {
      return center_;
    }

    /**
     * @return Returns the chrom.
     */
    public String getChrom() {
      return chrom_;
    }

    /**
     * @return Returns the genome_build.
     */
    public String getGenome_build() {
      return genome_build_;
    }

    /**
     * @return Returns the genotypes.
     */
    public ArrayList<Genotype> getGenotypes() {
      return genotypes_;
    }

    /**
     * @return Returns the panelLsid.
     */
    public String getPanelLsid() {
      return panelLsid_;
    }

    /**
     * @return Returns the pos.
     */
    public Integer getPos() {
      return pos_;
    }

    /**
     * @return Returns the protLsid.
     */
    public String getProtLsid() {
      return protLsid_;
    }

    /**
     * @return Returns the qcCode.
     */
    public String getQcCode() {
      return qcCode_;
    }

    /**
     * @return Returns the rs.
     */
    public String getRs() {
      return rs_;
    }

    /**
     * @return Returns the strand.
     */
    public String getStrand() {
      return strand_;
    }
    
    
  }

  public class Genotype {
    int index_;
    String genotype_;
    
    public String getGenotype() {
      return genotype_;
    }
    
    public String getSampleName() {
      return sampleNames_.get(index_);
    }
  }
}
