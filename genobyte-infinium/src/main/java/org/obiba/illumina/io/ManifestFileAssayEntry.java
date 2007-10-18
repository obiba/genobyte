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

import java.io.IOException;

public class ManifestFileAssayEntry {
  /** String that separates tokens on one line */
  private static final String TOKEN_SEPARATOR = ",";

  /** Entry's index in the file. The first entry in the file has value 1, the second has 2... */
  private Integer index_ = null;

  /** Unique name of this Illumina probe ie: rs10000092-124_T_R_1136459723 */
  private String ilmnId_ = null;
  /** The name of the SNP (usually its rs number from dbSNP) */
  private String snpName_ = null;
  /** Illumina Strand: Top or Bot */
  private String ilmnStrand_ = null;
  /** The variation: [A/G] */
  private String snp_ = null;
  /** The Address A ID (?) */
  private String addressAid_ = null;
  /** The Allele A Probe sequence */
  private String alleleAProbeSeq_ = null;
  /** The Address B ID (?) */
  private String addressBid_ = null;
  /** The Allele B Probe sequence */
  private String alleleBProbeSeq_ = null;
  /** The Genome's build number used to design the probe */
  private String genomeBuild_ = null;
  /** The chromosome where the SNP is located */
  private String chromosome_ = null;
  /** The physical position of the SNP */
  private String mapInfo_ = null;
  /** The ploidy SNP (number of alleles) */
  private String ploidy_ = null;
  /** The taxon */
  private String species_ = null;
  /** The source (dbSNP) */
  private String source_ = null;
  /** The source version (dbSNP build) */
  private String sourceVersion_ = null;
  /** The source strand (dbSNP strand TOP/BOT) */
  private String sourceStrand_ = null;
  /** The source sequence (dbSNP flanks?) */
  private String sourceSeq_ = null;
  /** The strand submitted by the customer when ordering an OPA */
  private String customerStrand_ = null;
  /** A code sequence from Illumina (?) */
  private String illumiCodeSeq_ = null;
  /** The genomic sequence on the Top strand */
  private String topGenomicSeq_ = null;
  /** The BeadStudioSet ID (?) */
  private String beadSetId_ = null;

  /**
   * @param br
   * @return
   * @throws IOException
   */
  static ManifestFileAssayEntry read(String columns[], String line, int index) {
    ManifestFileAssayEntry newEntry = new ManifestFileAssayEntry();
    newEntry.index_ = index;
    String tokens[] = line.split(ManifestFileAssayEntry.TOKEN_SEPARATOR);
    for (int i = 0; i < tokens.length; i++) {
      String columnName = columns[i];
      String value = tokens[i];

      if ("IlmnID".equalsIgnoreCase(columnName)) {
        newEntry.ilmnId_ = value;
      } else if ("Name".equalsIgnoreCase(columnName)) {
        newEntry.snpName_ = value;
      } else if ("IlmnStrand".equalsIgnoreCase(columnName)) {
        newEntry.ilmnStrand_ = value;
      } else if ("SNP".equalsIgnoreCase(columnName)) {
        newEntry.snp_ = value;
      } else if ("AddressA_ID".equalsIgnoreCase(columnName)) {
        newEntry.addressAid_ = value;
      } else if ("AlleleA_ProbeSeq".equalsIgnoreCase(columnName)) {
        newEntry.alleleAProbeSeq_ = value;
      } else if ("AddressB_ID".equalsIgnoreCase(columnName)) {
        newEntry.addressBid_ = value;
      } else if ("AlleleB_ProbeSeq".equalsIgnoreCase(columnName)) {
        newEntry.alleleBProbeSeq_ = value;
      } else if ("Chr".equalsIgnoreCase(columnName)) {
        newEntry.chromosome_ = value;
      } else if ("MapInfo".equalsIgnoreCase(columnName)) {
        newEntry.mapInfo_ = value;
      } else if ("Ploidy".equalsIgnoreCase(columnName)) {
        newEntry.ploidy_ = value;
      } else if ("Species".equalsIgnoreCase(columnName)) {
        newEntry.species_ = value;
      } else if ("CustomerStrand".equalsIgnoreCase(columnName)) {
        newEntry.customerStrand_ = value;
      } else if ("IlmnStrand".equalsIgnoreCase(columnName)) {
        newEntry.ilmnStrand_ = value;
      } else if ("IllumicodeSeq".equalsIgnoreCase(columnName)) {
        newEntry.illumiCodeSeq_ = value;
      } else if ("TopGenomicSeq".equalsIgnoreCase(columnName)) {
        newEntry.topGenomicSeq_ = value;
      } else if ("GenomeBuild".equalsIgnoreCase(columnName)) {
        newEntry.genomeBuild_ = value;
      } else if ("Source".equalsIgnoreCase(columnName)) {
        newEntry.source_ = value;
      } else if ("SourceVersion".equalsIgnoreCase(columnName)) {
        newEntry.sourceVersion_ = value;
      } else if ("SourceStrand".equalsIgnoreCase(columnName)) {
        newEntry.sourceStrand_ = value;
      } else if ("SourceSeq".equalsIgnoreCase(columnName)) {
        newEntry.sourceSeq_ = value;
      } else if ("BeadSetId".equalsIgnoreCase(columnName)) {
        newEntry.beadSetId_ = value;
      }
    }
    return newEntry;
  }

  public Integer getIndex() {
    return index_;
  }

  public void setIndex(Integer index) {
    index_ = index;
  }

  public String getIlmnId() {
    return ilmnId_;
  }

  public void setIlmnId(String ilmnId) {
    ilmnId_ = ilmnId;
  }

  public String getSnpName() {
    return snpName_;
  }

  public void setSnpName(String snpName) {
    snpName_ = snpName;
  }

  public String getIlmnStrand() {
    return ilmnStrand_;
  }

  public void setIlmnStrand(String ilmnStrand) {
    ilmnStrand_ = ilmnStrand;
  }

  public String getSnp() {
    return snp_;
  }

  public void setSnp(String snp) {
    snp_ = snp;
  }

  public String getAddressAid() {
    return addressAid_;
  }

  public void setAddressAid(String addressAid) {
    addressAid_ = addressAid;
  }

  public String getAlleleAProbeSeq() {
    return alleleAProbeSeq_;
  }

  public void setAlleleAProbeSeq(String alleleAProbeSeq) {
    alleleAProbeSeq_ = alleleAProbeSeq;
  }

  public String getAddressBid() {
    return addressBid_;
  }

  public void setAddressBid(String addressBid) {
    addressBid_ = addressBid;
  }

  public String getAlleleBProbeSeq() {
    return alleleBProbeSeq_;
  }

  public void setAlleleBProbeSeq(String alleleBProbeSeq) {
    alleleBProbeSeq_ = alleleBProbeSeq;
  }

  public String getGenomeBuild() {
    return genomeBuild_;
  }

  public void setGenomeBuild(String genomeBuild) {
    genomeBuild_ = genomeBuild;
  }

  public String getChromosome() {
    return chromosome_;
  }

  public void setChromosome(String chromosome) {
    chromosome_ = chromosome;
  }

  public String getMapInfo() {
    return mapInfo_;
  }

  public void setMapInfo(String mapInfo) {
    mapInfo_ = mapInfo;
  }

  public String getPloidy() {
    return ploidy_;
  }

  public void setPloidy(String ploidy) {
    ploidy_ = ploidy;
  }

  public String getSpecies() {
    return species_;
  }

  public void setSpecies(String species) {
    species_ = species;
  }

  public String getSource() {
    return source_;
  }

  public void setSource(String source) {
    source_ = source;
  }

  public String getSourceVersion() {
    return sourceVersion_;
  }

  public void setSourceVersion(String sourceVersion) {
    sourceVersion_ = sourceVersion;
  }

  public String getSourceStrand() {
    return sourceStrand_;
  }

  public void setSourceStrand(String sourceStrand) {
    sourceStrand_ = sourceStrand;
  }

  public String getSourceSeq() {
    return sourceSeq_;
  }

  public void setSourceSeq(String sourceSeq) {
    sourceSeq_ = sourceSeq;
  }

  public String getCustomerStrand() {
    return customerStrand_;
  }

  public void setCustomerStrand(String customerStrand) {
    customerStrand_ = customerStrand;
  }

  public String getIllumiCodeSeq() {
    return illumiCodeSeq_;
  }

  public void setIllumiCodeSeq(String illumiCodeSeq) {
    illumiCodeSeq_ = illumiCodeSeq;
  }

  public String getTopGenomicSeq() {
    return topGenomicSeq_;
  }

  public void setTopGenomicSeq(String topGenomicSeq) {
    topGenomicSeq_ = topGenomicSeq;
  }

  public String getBeadSetId() {
    return beadSetId_;
  }

  public void setBeadSetId(String beadSetId) {
    beadSetId_ = beadSetId;
  }
 
}