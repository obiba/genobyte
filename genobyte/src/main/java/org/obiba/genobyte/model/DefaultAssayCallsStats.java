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
import org.obiba.genobyte.statistic.util.FrequencyStatistics;


/**
 * A utility bean to hold default statistics calculated by the {@link FrequencyStatistics} implementation and
 * the inconsistencies package specific to assays.
 */
@BitwiseRecord(storeAll=true)
public class DefaultAssayCallsStats extends DefaultCallsStats {

  private Double hw;
  private Double heterozygosity;
  private Double maf;

  public Double getHw() {
    return hw;
  }
  public void setHw(Double hw) {
    this.hw = hw;
  }
  public Double getHeterozygosity() {
    return heterozygosity;
  }
  public void setHeterozygosity(Double heterozygosity) {
    this.heterozygosity = heterozygosity;
  }
  public Double getMaf() {
    return maf;
  }
  public void setMaf(Double maf) {
    this.maf = maf;
  }

}
