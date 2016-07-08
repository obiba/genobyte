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
package org.obiba.genobyte.statistic.casecontrol;

/**
 * Encapsulates genotype frequencies at a given locus for a certain population.
 */
final class GenotypeFrequency {

  private int asArray[] = null;

  /** The number of homozygotes A */
  int aa = 0;
  /** The number of heterozygotes */
  int ab = 0;
  /** The number of homozygotes B */
  int bb = 0;

  /** The number of no-calls */
  int uu = 0;

  /** The locus' call rate */
  double callRate = 0.0d;

  /** The locus' minor allele frequency */
  double maf = 0.0d;

  /** The Hardy-Weinberg Chi Square */
  double hwChi2 = 0.0d;

  /** The Hardy-Weinberg p-value */
  double hwPValue = 0.0d;

  public GenotypeFrequency() {
  }
  
  final public int get(int index) {
    return toArray()[index];
  }

  final public int[] toArray() {
    if (asArray == null) {
      asArray = new int[] { aa, ab, bb };
    }
    return asArray;
  }

  final public int getTotal() {
    return aa + ab + bb;
  }
}