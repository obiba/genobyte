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

import java.util.LinkedList;
import java.util.List;

import org.obiba.bitwise.annotation.BitwiseRecord;
import org.obiba.bitwise.annotation.DictionaryDef;
import org.obiba.bitwise.annotation.NotStored;
import org.obiba.bitwise.annotation.Stored;
import org.obiba.genobyte.model.SnpGenotype;


@BitwiseRecord(storeAll=true, dictionary= {
@DictionaryDef(name="diseaseStatus", dictionaryClass=DiseaseStatusDictionary.class)})
public class SimulatedSample {

  private Integer id = null;
  private String name_ = null;
  private String individualName_ = null;
  private DiseaseStatus diseaseStatus_ = null;
  private String motherName_ = null;
  private String fatherName_ = null;

  private List<SnpGenotype<Integer>> genotypes_ = null;

  private Report report_ = new Report();

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Sample{ name_=[").append(name_).append("]")
      .append(" individualName_=[").append(individualName_).append("]")
      .append(" diseaseStatus_=[").append(diseaseStatus_).append("]")
      .append(" motherName_=[").append(motherName_).append("]")
      .append(" fatherName_=[").append(fatherName_).append("]")
      .append(" report_={").append(report_).append("}")
      .append("}");
    return sb.toString();
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

  public SimulatedSample() {
    super();
  }

  public void addGenotype(SnpGenotype<Integer> g) {
    if(genotypes_ == null) {
      genotypes_ = new LinkedList<SnpGenotype<Integer>>();
    }
    genotypes_.add(g);
  }

  /**
   * @return Returns the genotypes.
   */
  @NotStored
  public List<SnpGenotype<Integer>> getGenotypes() {
    return genotypes_;
  }
  
  public void setGenotypes(List<SnpGenotype<Integer>> genotypes) {
    this.genotypes_ = genotypes;
  }

  /**
   * @return Returns the name.
   */
//  @Stored(unique=true)
  public String getName() {
    return name_;
  }

  public void setName(String name) {
    name_ = name;
  }

  /**
   * @return the fatherName
   */
  public String getFatherName() {
    return fatherName_;
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
   * @param fatherName the fatherName to set
   */
  public void setFatherName(String fatherName) {
    fatherName_ = fatherName;
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
   * @return the diseaseStatus
   */
  @Stored(dictionary="diseaseStatus")
  public DiseaseStatus getDiseaseStatus() {
    return diseaseStatus_;
  }

  /**
   * @param diseaseStatus the diseaseStatus to set
   */
  public void setDiseaseStatus(DiseaseStatus diseaseStatus) {
    diseaseStatus_ = diseaseStatus;
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

  
  @Stored(unique=true)
  public Integer getId() {
    return id;
  }
  
  
  public void setId(Integer id) {
    this.id = id;
  }
}
