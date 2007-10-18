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

import org.obiba.bitwise.annotation.BitwiseRecord;
import org.obiba.bitwise.annotation.DictionaryDef;
import org.obiba.bitwise.annotation.Stored;
import org.obiba.bitwise.dictionary.EnumDictionary;
import org.obiba.genobyte.model.Chromosome;
import org.obiba.genobyte.model.Orientation;
import org.obiba.genobyte.model.SnpAllele;


@BitwiseRecord(storeAll=true, dictionary={
    @DictionaryDef(name="snpAllele", dictionaryClass=EnumDictionary.class),
    @DictionaryDef(name="orientation", dictionaryClass=EnumDictionary.class),
    @DictionaryDef(name="chromosome", dictionaryClass=EnumDictionary.class)
})
public class SimulatedAssay {

  private Integer id = null;
  private String name = null;
  private String snp = null;

  @Stored(dictionary="snpAllele")
  private SnpAllele alleleA = null;
  @Stored(dictionary="snpAllele")
  private SnpAllele alleleB = null;

  @Stored(dictionary="orientation")
  private Orientation orientation = null;
  @Stored(dictionary="chromosome")
  private Chromosome chromosome = null;
  private Integer position;

  private String genes = null;

  private Report report_ = new Report();

  public SimulatedAssay() {
    super();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Assay{ snp_=[").append(snp).append("]")
      .append(" name_=[").append(name).append("]")
      .append(" alleleA_=[").append(alleleA).append("]")
      .append(" alleleB_=[").append(alleleB).append("]")
      .append(" orientation_=[").append(orientation).append("]")
      .append(" chromosome_=[").append(chromosome).append("]")
      .append(" position_=[").append(position).append("]")
      .append(" genes_=[").append(genes).append("]")
      .append(" report_={").append(report_).append("}")
      .append("}");
    return sb.toString();
  }

  @Stored(unique=true)
  public Integer getId() {
    return id;
  }

  /**
   * @return the alleleA
   */
  public SnpAllele getAlleleA() {
    return alleleA;
  }

  /**
   * @return the alleleB
   */
  public SnpAllele getAlleleB() {
    return alleleB;
  }

  /**
   * @return the chromosome
   */
  public Chromosome getChromosome() {
    return chromosome;
  }

  /**
   * @return the genes
   */
  public String getGenes() {
    return genes;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the orientation
   */
  public Orientation getOrientation() {
    return orientation;
  }

  /**
   * @return the position
   */
  public Integer getPosition() {
    return position;
  }

  /**
   * @return the snp
   */
  public String getSnp() {
    return snp;
  }

  /**
   * @param alleleA the alleleA to set
   */
  public void setAlleleA(SnpAllele alleleA) {
    this.alleleA = alleleA;
  }

  /**
   * @param alleleB the alleleB to set
   */
  public void setAlleleB(SnpAllele alleleB) {
    this.alleleB = alleleB;
  }

  /**
   * @param chromosome the chromosome to set
   */
  public void setChromosome(Chromosome chromosome) {
    this.chromosome = chromosome;
  }

  /**
   * @param genes the genes to set
   */
  public void setGenes(String genes) {
    this.genes = genes;
  }

  /**
   * @param id the id to set
   */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @param orientation the orientation to set
   */
  public void setOrientation(Orientation orientation) {
    this.orientation = orientation;
  }

  /**
   * @param position the position to set
   */
  public void setPosition(Integer position) {
    this.position = position;
  }

  /**
   * @param snp the snp to set
   */
  public void setSnp(String snp) {
    this.snp = snp;
  }

  /**
   * @return
   * @see org.obiba.bitwise.simulation.Report#getCallRate()
   */
  public Double getCallRate() {
    return report_.getCallRate();
  }

  /**
   * @return
   * @see org.obiba.bitwise.simulation.Report#getFreqA()
   */
  public Integer getFreqA() {
    return report_.getFreqA();
  }

  /**
   * @return
   * @see org.obiba.bitwise.simulation.Report#getFreqB()
   */
  public Integer getFreqB() {
    return report_.getFreqB();
  }

  /**
   * @return
   * @see org.obiba.bitwise.simulation.Report#getFreqH()
   */
  public Integer getFreqH() {
    return report_.getFreqH();
  }

  /**
   * @return
   * @see org.obiba.bitwise.simulation.Report#getFreqU()
   */
  public Integer getFreqU() {
    return report_.getFreqU();
  }
  
  
  /**
   * @return
   * @see org.obiba.bitwise.simulation.Report#getTotalCalls()
   */
  public Integer getTotalCalls() {
    return report_.getTotalCalls();
  }
  

  /**
   * @return
   * @see org.obiba.bitwise.simulation.Report#getMendel()
   */
  public Integer getMendel() {
    return report_.getMendel();
  }

  /**
   * @return
   * @see org.obiba.bitwise.simulation.Report#getReproAssay()
   */
  public Integer getReproAssay() {
    return report_.getReproAssay();
  }

  /**
   * @return
   * @see org.obiba.bitwise.simulation.Report#getReproDna()
   */
  public Integer getReproDna() {
    return report_.getReproDna();
  }

  /**
   * @param callRate
   * @see org.obiba.bitwise.simulation.Report#setCallRate(java.lang.Double)
   */
  public void setCallRate(Double callRate) {
    report_.setCallRate(callRate);
  }

  /**
   * @param freqA
   * @see org.obiba.bitwise.simulation.Report#setFreqA(java.lang.Integer)
   */
  public void setFreqA(Integer freqA) {
    report_.setFreqA(freqA);
  }

  /**
   * @param freqB
   * @see org.obiba.bitwise.simulation.Report#setFreqB(java.lang.Integer)
   */
  public void setFreqB(Integer freqB) {
    report_.setFreqB(freqB);
  }

  /**
   * @param freqH
   * @see org.obiba.bitwise.simulation.Report#setFreqH(java.lang.Integer)
   */
  public void setFreqH(Integer freqH) {
    report_.setFreqH(freqH);
  }

  /**
   * @param freqU
   * @see org.obiba.bitwise.simulation.Report#setFreqU(java.lang.Integer)
   */
  public void setFreqU(Integer freqU) {
    report_.setFreqU(freqU);
  }
  
  /**
   * @param totalCalls
   * @see org.obiba.bitwise.simulation.Report#setFreqU(java.lang.Integer)
   */
  public void setTotalCalls(Integer totalCalls) {
    report_.setTotalCalls(totalCalls);
  }

  /**
   * @param mendel
   * @see org.obiba.bitwise.simulation.Report#setMendel(java.lang.Integer)
   */
  public void setMendel(Integer mendel) {
    report_.setMendel(mendel);
  }

  /**
   * @param reproAssay
   * @see org.obiba.bitwise.simulation.Report#setReproAssay(java.lang.Integer)
   */
  public void setReproAssay(Integer reproAssay) {
    report_.setReproAssay(reproAssay);
  }

  /**
   * @param reproDna
   * @see org.obiba.bitwise.simulation.Report#setReproDna(java.lang.Integer)
   */
  public void setReproDna(Integer reproDna) {
    report_.setReproDna(reproDna);
  }

  public Integer getMendelTests() {
    return report_.getMendelTests();
  }

  public Integer getReproAssayTests() {
    return report_.getReproAssayTests();
  }

  public Integer getReproDnaTests() {
    return report_.getReproDnaTests();
  }

  public void setMendelTests(Integer mendelTests) {
    report_.setMendelTests(mendelTests);
  }

  public void setReproAssayTests(Integer reproAssayTests) {
    report_.setReproAssayTests(reproAssayTests);
  }

  public void setReproDnaTests(Integer reproDnaTests) {
    report_.setReproDnaTests(reproDnaTests);
  }

  
}
