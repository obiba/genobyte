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
package org.obiba.illumina.bitwise.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.obiba.bitwise.annotation.BitwiseRecord;
import org.obiba.bitwise.annotation.DictionaryDef;
import org.obiba.bitwise.annotation.DictionaryProperty;
import org.obiba.bitwise.annotation.FieldTemplate;
import org.obiba.bitwise.annotation.Stored;
import org.obiba.bitwise.dictionary.EnumDictionary;
import org.obiba.genobyte.model.Chromosome;
import org.obiba.genobyte.model.DefaultAssayCallsStats;
import org.obiba.genobyte.model.SnpAllele;


@BitwiseRecord(storeAll=true,
    dictionary={@DictionaryDef(name="calls", dictionaryClass=EnumDictionary.class, property={@DictionaryProperty(name="enumClassName", value="org.obiba.genobyte.model.SnpCall")})},
    templates={@FieldTemplate(prefix="calls", dictionary="calls")}
)
public class Assay extends DefaultAssayCallsStats {

  public enum Strand {
    TOP, BOT;
  }

  @Stored(unique=true)
  private Integer locusId = null;
  private String ilmnId = null;
  private String snpName = null;
  private Strand ilmnStrand = null;
  private SnpAllele alleleA = null;
  private SnpAllele alleleB = null;
  
  private String addressAid = null;
  private String alleleAProbeSeq = null;
  private String addressBid = null;
  private String alleleBProbeSeq = null;
  private Chromosome chromosome = null;
  private Integer mapInfo = null;
  private String ploidy = null;
  private String species = null;
  private Strand customerStrand = null;
  private String illumiCodeSeq = null;
  private String topGenomicSeq = null;
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  public Integer getLocusId() {
    return locusId;
  }

  public void setLocusId(Integer locusId) {
    this.locusId = locusId;
  }

  public String getIlmnId() {
    return ilmnId;
  }

  public void setIlmnId(String ilmnId) {
    this.ilmnId = ilmnId;
  }

  public String getSnpName() {
    return snpName;
  }

  public void setSnpName(String snpName) {
    this.snpName = snpName;
  }

  public Strand getIlmnStrand() {
    return ilmnStrand;
  }

  public void setIlmnStrand(Strand ilmnStrand) {
    this.ilmnStrand = ilmnStrand;
  }

  public SnpAllele getAlleleA() {
    return alleleA;
  }

  public void setAlleleA(SnpAllele alleleA) {
    this.alleleA = alleleA;
  }

  public SnpAllele getAlleleB() {
    return alleleB;
  }

  public void setAlleleB(SnpAllele alleleB) {
    this.alleleB = alleleB;
  }

  public String getAddressAid() {
    return addressAid;
  }

  public void setAddressAid(String addressAid) {
    this.addressAid = addressAid;
  }

  public String getAlleleAProbeSeq() {
    return alleleAProbeSeq;
  }

  public void setAlleleAProbeSeq(String alleleAProbeSeq) {
    this.alleleAProbeSeq = alleleAProbeSeq;
  }

  public String getAddressBid() {
    return addressBid;
  }

  public void setAddressBid(String addressBid) {
    this.addressBid = addressBid;
  }

  public String getAlleleBProbeSeq() {
    return alleleBProbeSeq;
  }

  public void setAlleleBProbeSeq(String alleleBProbeSeq) {
    this.alleleBProbeSeq = alleleBProbeSeq;
  }

  public Chromosome getChromosome() {
    return chromosome;
  }

  public void setChromosome(Chromosome chromosome) {
    this.chromosome = chromosome;
  }

  public Integer getMapInfo() {
    return mapInfo;
  }

  public void setMapInfo(Integer mapInfo) {
    this.mapInfo = mapInfo;
  }

  public String getPloidy() {
    return ploidy;
  }

  public void setPloidy(String ploidy) {
    this.ploidy = ploidy;
  }

  public String getSpecies() {
    return species;
  }

  public void setSpecies(String species) {
    this.species = species;
  }

  public Strand getCustomerStrand() {
    return customerStrand;
  }

  public void setCustomerStrand(Strand customerStrand) {
    this.customerStrand = customerStrand;
  }

  public String getIllumiCodeSeq() {
    return illumiCodeSeq;
  }

  public void setIllumiCodeSeq(String illumiCodeSeq) {
    this.illumiCodeSeq = illumiCodeSeq;
  }

  public String getTopGenomicSeq() {
    return topGenomicSeq;
  }

  public void setTopGenomicSeq(String topGenomicSeq) {
    this.topGenomicSeq = topGenomicSeq;
  }
}
