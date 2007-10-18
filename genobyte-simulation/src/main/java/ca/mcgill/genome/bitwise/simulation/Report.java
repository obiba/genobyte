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

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Report {

  private Double callRate_ = null;

  private Integer freqA_ = null;
  private Integer freqB_ = null;
  private Integer freqH_ = null;
  private Integer freqU_ = null;
  private Integer totalCalls_ = null;
  private Integer reproAssay_ = null;
  private Integer reproAssayTests_ = null;
  private Integer reproDna_ = null;
  private Integer reproDnaTests_ = null;
  private Integer mendel_ = null;
  private Integer mendelTests_ = null;

  public Report() {
    super();
  }
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  public Double getCallRate() {
    return callRate_;
  }
  public Integer getFreqA() {
    return freqA_;
  }
  public Integer getFreqB() {
    return freqB_;
  }
  public Integer getFreqH() {
    return freqH_;
  }
  public Integer getFreqU() {
    return freqU_;
  }
  public Integer getTotalCalls() {
    return totalCalls_;
  }
  public Integer getMendel() {
    return mendel_;
  }
  public Integer getMendelTests() {
    return mendelTests_;
  }
  public Integer getReproAssay() {
    return reproAssay_;
  }
  public Integer getReproAssayTests() {
    return reproAssayTests_;
  }
  public Integer getReproDna() {
    return reproDna_;
  }
  public Integer getReproDnaTests() {
    return reproDnaTests_;
  }
  public void setCallRate(Double callRate) {
    callRate_ = callRate;
  }
  public void setFreqA(Integer freqA) {
    freqA_ = freqA;
  }
  public void setFreqB(Integer freqB) {
    freqB_ = freqB;
  }
  public void setFreqH(Integer freqH) {
    freqH_ = freqH;
  }
  public void setFreqU(Integer freqU) {
    freqU_ = freqU;
  }
  public void setTotalCalls(Integer totalCalls) {
    totalCalls_ = totalCalls;
  }
  public void setMendel(Integer mendel) {
    mendel_ = mendel;
  }
  public void setMendelTests(Integer mendelTests) {
    mendelTests_ = mendelTests;
  }
  public void setReproAssay(Integer reproAssay) {
    reproAssay_ = reproAssay;
  }
  public void setReproAssayTests(Integer reproAssayTests) {
    reproAssayTests_ = reproAssayTests;
  }
  public void setReproDna(Integer reproDna) {
    reproDna_ = reproDna;
  }
  public void setReproDnaTests(Integer reproDnaTests) {
    reproDnaTests_ = reproDnaTests;
  }

}
