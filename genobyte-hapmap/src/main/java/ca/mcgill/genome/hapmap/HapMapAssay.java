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

import java.util.LinkedList;
import java.util.List;

import org.obiba.bitwise.annotation.BitwiseRecord;
import org.obiba.bitwise.annotation.Stored;
import org.obiba.genobyte.model.Chromosome;
import org.obiba.genobyte.model.Orientation;
import org.obiba.genobyte.model.SnpAllele;
import org.obiba.genobyte.model.SnpGenotype;


@BitwiseRecord
public class HapMapAssay {

  private String snp_ = null;

  private SnpAllele alleleA_ = null;
  private SnpAllele alleleB_ = null;

  private Chromosome chromosome_ = null;
  private int position_;
  private Orientation orientation_ = null;
  private Map map_ = null;
  private String center_ = null;

  private String protLsid_ = null;
  private String assayLsid_ = null;
  private String panelLsid_ = null;
  private QcCode qcCode_ = null;

  private Double maf_ = null;
  private Double chi2_ = null;
  private Double callRate_ = null;

  private List<SnpGenotype<String>> genotypes_ = null;

  public HapMapAssay() {
    super();
  }

  public List<SnpGenotype<String>> getGenotypes() {
    return genotypes_;
  }

  public void addGenotype(SnpGenotype<String> g) {
    if(genotypes_ == null) {
      genotypes_ = new LinkedList<SnpGenotype<String>>();
    }
    genotypes_.add(g);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Assay{ snp_=[").append(snp_).append("]")
      .append(" alleleA_=[").append(alleleA_).append("]")
      .append(" alleleB_=[").append(alleleB_).append("]")
      .append(" orientation_=[").append(orientation_).append("]")
      .append(" chromosome_=[").append(chromosome_).append("]")
      .append(" position_=[").append(position_).append("]")
      .append(" map_=[").append(map_).append("]")
      .append(" center_=[").append(center_).append("]")
      .append(" protLsid_=[").append(protLsid_).append("]")
      .append(" assayLsid_=[").append(assayLsid_).append("]")
      .append(" panelLsid_=[").append(panelLsid_).append("]")
      .append(" qcCode_=[").append(qcCode_).append("]")
      .append(" maf_=[").append(maf_).append("]")
      .append(" chi2_=[").append(chi2_).append("]")
      .append(" callRate_=[").append(callRate_).append("]")
      .append("}");
    return sb.toString();
  }

  @Stored(unique=true)
  public Integer getId() {
    return Integer.valueOf(snp_.substring(2));
  }

  /**
   * @return Returns the alleleA.
   */
  @Stored
  public SnpAllele getAlleleA() {
    return alleleA_;
  }

  /**
   * @return Returns the alleleB.
   */
  @Stored
  public SnpAllele getAlleleB() {
    return alleleB_;
  }

  /**
   * @return Returns the assayLsid.
   */
  @Stored
  public String getAssayLsid() {
    return assayLsid_;
  }

  /**
   * @return Returns the callRate.
   */
  @Stored
  public Double getCallRate() {
    return callRate_;
  }

  /**
   * @return Returns the center.
   */
//  @Stored
  public String getCenter() {
    return center_;
  }

  /**
   * @return Returns the chi2.
   */
  @Stored()
  public Double getChi2() {
    return chi2_;
  }

  /**
   * @return Returns the chromosome.
   */
  @Stored
  public Chromosome getChromosome() {
    return chromosome_;
  }

  /**
   * @return Returns the maf.
   */
  @Stored
  public Double getMaf() {
    return maf_;
  }

  /**
   * @return Returns the map.
   */
//  @Stored
  public Map getMap() {
    return map_;
  }

  /**
   * @return Returns the orientation.
   */
//  @Stored(field="strand")
  public Orientation getOrientation() {
    return orientation_;
  }

  /**
   * @return Returns the panelLsid.
   */
//  @Stored
  public String getPanelLsid() {
    return panelLsid_;
  }

  /**
   * @return Returns the position.
   */
  @Stored
  public int getPosition() {
    return position_;
  }

  /**
   * @return Returns the protLsid.
   */
//  @Stored
  public String getProtLsid() {
    return protLsid_;
  }

  /**
   * @return Returns the qcCode.
   */
//  @Stored
  public QcCode getQcCode() {
    return qcCode_;
  }

  /**
   * @return Returns the snp.
   */
  @Stored
  public String getSnp() {
    return snp_;
  }

  /**
   * @param alleleA The alleleA to set.
   */
  public void setAlleleA(SnpAllele alleleA) {
    alleleA_ = alleleA;
  }

  /**
   * @param alleleB The alleleB to set.
   */
  public void setAlleleB(SnpAllele alleleB) {
    alleleB_ = alleleB;
  }

  /**
   * @param assayLsid The assayLsid to set.
   */
  public void setAssayLsid(String assayLsid) {
    assayLsid_ = assayLsid;
  }

  /**
   * @param callRate The callRate to set.
   */
  public void setCallRate(Double callRate) {
    callRate_ = callRate;
  }

  /**
   * @param center The center to set.
   */
  public void setCenter(String center) {
    center_ = center;
  }

  /**
   * @param chi2 The chi2 to set.
   */
  public void setChi2(Double chi2) {
    chi2_ = chi2;
  }

  /**
   * @param chromosome The chromosome to set.
   */
  public void setChromosome(Chromosome chromosome) {
    chromosome_ = chromosome;
  }

  /**
   * @param maf The maf to set.
   */
  public void setMaf(Double maf) {
    maf_ = maf;
  }

  /**
   * @param map The map to set.
   */
  public void setMap(Map map) {
    map_ = map;
  }

  /**
   * @param orientation The orientation to set.
   */
  public void setOrientation(Orientation orientation) {
    orientation_ = orientation;
  }

  /**
   * @param panelLsid The panelLsid to set.
   */
  public void setPanelLsid(String panelLsid) {
    panelLsid_ = panelLsid;
  }

  /**
   * @param position The position to set.
   */
  public void setPosition(int position) {
    position_ = position;
  }

  /**
   * @param protLsid The protLsid to set.
   */
  public void setProtLsid(String protLsid) {
    protLsid_ = protLsid;
  }

  /**
   * @param qcCode The qcCode to set.
   */
  public void setQcCode(QcCode qcCode) {
    qcCode_ = qcCode;
  }

  /**
   * @param snp The snp to set.
   */
  public void setSnp(String snp) {
    snp_ = snp;
  }

  
}
