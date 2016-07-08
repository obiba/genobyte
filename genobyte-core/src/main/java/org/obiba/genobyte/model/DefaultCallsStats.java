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
package org.obiba.genobyte.model;

import org.obiba.bitwise.annotation.BitwiseRecord;
import org.obiba.genobyte.GenotypingRecordStore;
import org.obiba.genobyte.statistic.util.FrequencyStatistics;


/**
 * A utility bean to hold default statistics calculated by the {@link FrequencyStatistics} implementation and
 * the inconsistencies package. It may be used as the base class for a {@link GenotypingRecordStore} record class.
 */
@BitwiseRecord(storeAll=true)
abstract public class DefaultCallsStats {

  private Integer freqA;
  private Integer freqB;
  private Integer freqH;
  private Integer freqU;
  private Integer totalCalls;
  private Double callRate;
  private Integer reproDna;
  private Integer reproDnaTests;
  private Integer reproAssay;
  private Integer reproAssayTests;
  private Integer mendel;
  private Integer mendelTests;

  public Integer getFreqA() {
    return freqA;
  }
  public void setFreqA(Integer freqA) {
    this.freqA = freqA;
  }
  public Integer getFreqB() {
    return freqB;
  }
  public void setFreqB(Integer freqB) {
    this.freqB = freqB;
  }
  public Integer getFreqH() {
    return freqH;
  }
  public void setFreqH(Integer freqH) {
    this.freqH = freqH;
  }
  public Integer getFreqU() {
    return freqU;
  }
  public void setFreqU(Integer freqU) {
    this.freqU = freqU;
  }
  public Integer getTotalCalls() {
    return totalCalls;
  }
  public void setTotalCalls(Integer totalCalls) {
    this.totalCalls = totalCalls;
  }
  public Double getCallRate() {
    return callRate;
  }
  public void setCallRate(Double callRate) {
    this.callRate = callRate;
  }
  public Integer getReproDna() {
    return reproDna;
  }
  public void setReproDna(Integer reproDna) {
    this.reproDna = reproDna;
  }
  public Integer getReproDnaTests() {
    return reproDnaTests;
  }
  public void setReproDnaTests(Integer reproDnaTests) {
    this.reproDnaTests = reproDnaTests;
  }
  public Integer getReproAssay() {
    return reproAssay;
  }
  public void setReproAssay(Integer reproAssay) {
    this.reproAssay = reproAssay;
  }
  public Integer getReproAssayTests() {
    return reproAssayTests;
  }
  public void setReproAssayTests(Integer reproAssayTests) {
    this.reproAssayTests = reproAssayTests;
  }
  public Integer getMendel() {
    return mendel;
  }
  public void setMendel(Integer mendel) {
    this.mendel = mendel;
  }
  public Integer getMendelTests() {
    return mendelTests;
  }
  public void setMendelTests(Integer mendelTests) {
    this.mendelTests = mendelTests;
  }

}
