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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Wraps the data contained in a Manifest file provided by Illumina.
 */
public class ManifestFile {

  /** String that separates tokens on one line */
  private static final String TOKEN_SEPARATOR = ",";
  
  /** Marks the start of the header section */
  private static final String HEADER_START_TOKEN = "[Heading]".toLowerCase();
  /** Marks the start of the SNP data section */
  private static final String SNP_START_TOKEN = "[Assay]".toLowerCase();

  /** Number of SNPs on chip is provided by one of the two following header values */
  private static final String HEADER_LOCI_COUNT = "Loci Count";
  private static final String HEADER_SNP_COUNT = "SNP Count";

  private Map<String,String> header_ = new HashMap<String,String>();

  private Integer snpCount_ = null;

  private List<ManifestFileAssayEntry> manifestFileEntries_ = null;

  /**
   * @param manifestFile
   * @throws IOException
   */
  public ManifestFile(File manifestFile) throws IOException {
    this(new FileReader(manifestFile));
  }
  
  /**
   * Parses the <tt>InputStream</tt> as a manifest file. The stream is closed after being parsed.
   * 
   * @param is the stream to parse
   * @throws IOException when a parse exception occurs
   */
  public ManifestFile(InputStream is) throws IOException {
    this(new InputStreamReader(is));
  }
  
  /**
   * Parses the <tt>Reader</tt> as a manifest file. The reader is closed after being parsed.
   * @param r the data to parse
   * @throws IOException when a parse exception occurs
   */
  public ManifestFile(Reader r) throws IOException {
    read(new LineNumberReader(r));
  }
  
  public List<ManifestFileAssayEntry> getManifestEntries() {
    return Collections.unmodifiableList(this.manifestFileEntries_);
  }

  /**
   * Allows access to the collection of {@link ManifestFileAssayEntry} objects.
   * 
   * @return an unmodifiable Iterator instance on the {@link ManifestFileAssayEntry} objects
   */
  public Iterator<ManifestFileAssayEntry> entries() {
    return Collections.unmodifiableList(manifestFileEntries_).iterator();
  }

  /**
   * Returns the number of SNPs in this instance.
   * @return the number of {@link ManifestFileAssayEntry} objects in this instance.
   */
  public int getSnpCount() {
    return manifestFileEntries_.size();
  }

  /**
   * Returns the value of the specified header key or null if the header is not known.
   * 
   * @param headerKey the unique header key
   * @return the value of the header or null if no header entry exists for the specified key
   */
  public String getHeaderValue(String headerKey){
    return (String)header_.get(headerKey);
  }

  /**
   * Parses the reader to extract header values and {@link ManifestFileAssayEntry} objects.
   * 
   * @param lnr
   * @throws IOException
   */
  private void read(LineNumberReader lnr) throws IOException {
    try {
      parseHeader(lnr);
      
      if(snpCount_ == null) {
        throw new IOException("Invalid file format. Expected one of ["+HEADER_LOCI_COUNT+","+HEADER_SNP_COUNT+"] header values.");
      }
      this.manifestFileEntries_ = new ArrayList<ManifestFileAssayEntry>(snpCount_);
      parseSnps(lnr);
      if(manifestFileEntries_.size() != snpCount_.intValue()) {
        throw new IOException("Invalid file format. Manifest contains " + manifestFileEntries_.size() + " entries, but header reported " + snpCount_ + " snps.");
      }
    } catch(Exception e) {
      IOException p = new IOException("Error reading line " + lnr.getLineNumber() + ": " + e.getMessage());
      p.initCause(e);
      throw p;
    } finally {
      try {
        lnr.close();
      } catch(Exception e) {
        // ignored
      }
    }
  }
  
  /**
   * Parses the header portion of the manifest file.
   * 
   * @param lnr
   * @throws IOException when the header is invalid
   */
  private void parseHeader(LineNumberReader lnr) throws IOException {

    // Skip everything before HEADER_START_TOKEN
    String line = lnr.readLine();
    while(line != null && line.toLowerCase().startsWith(HEADER_START_TOKEN) == false) {
      line = lnr.readLine();
    }

    if(line == null) {
      // No header: probably an empty file.
      return;
    }
    
    // Skip HEADER_START_TOKEN line
    line = lnr.readLine();

    // Read and store header data
    // Unknown header lines are silently ignored 
    while(line != null && line.toLowerCase().startsWith(SNP_START_TOKEN) == false) {
      // Skip empty lines
      if(line.trim().length() == 0){
        line = lnr.readLine();
        continue;
      }

      String tokens[] = line.split(TOKEN_SEPARATOR);

      // Safety to prevent ArrayOutOfBoundsException
      if(tokens.length == 0){
        throw new IOException("Invalid file format. Can't parse header line \""+line+"\".");
      }

      String headerKey = tokens[0];
      
      StringBuilder sb = new StringBuilder();
      for(int i = 1; i < tokens.length; i++) {
        sb.append(tokens[i]);
      }
      String headerValue = sb.toString();

      // We successfully parsed a header value, let the parser handle it.
      handleHeader(headerKey, headerValue);
      line = lnr.readLine();
    }

    // We reached the EOF
    if(line == null) {
      throw new IOException("Expected "+SNP_START_TOKEN+" section missing.");
    }
  }
  
  /**
   * Executes any special handling required for specific headers
   * @param key the header's key
   * @param value the header's value
   * @throws IOException when the header format is invalid
   */
  private void handleHeader(String key, String value) throws IOException {
    header_.put(key, value);
    
    // Handle the HEADER_LOCI_COUNT value
    if(HEADER_LOCI_COUNT.equalsIgnoreCase(key) == true || HEADER_SNP_COUNT.equalsIgnoreCase(key) == true) {
      if(snpCount_ != null) {
        throw new IOException("Invalid header section: only one of \""+HEADER_LOCI_COUNT+"\" and \""+HEADER_SNP_COUNT+"\" should be present.");
      }
      // Extract the number of SNPs that should be present in the file
      try{
        snpCount_ = Integer.valueOf(value);
      } catch(NumberFormatException e) {
        throw new IOException("Invalid header value: the value ["+value+"] for the header \""+key+"\" should be a number.");      
      }
    }
  }
  
  /**
   * Parses the SNP portion of the manifest file
   * @param lnr
   * @throws IOException
   */
  private void parseSnps(LineNumberReader lnr) throws IOException {
    String columnLine = lnr.readLine();
    if(columnLine == null) {
      throw new IOException();
    }
    String columns[] = columnLine.split(TOKEN_SEPARATOR);
    for(int i=0; i < snpCount_; i++) {
      String entryLine = lnr.readLine();
      if(entryLine == null) {
        throw new IOException("Invalid file format. Expected " + snpCount_ +" SNPs in "+SNP_START_TOKEN+" section found only "+i+".");
      }
      manifestFileEntries_.add(ManifestFileAssayEntry.read(columns, entryLine, i+1));
    }
  }
}
