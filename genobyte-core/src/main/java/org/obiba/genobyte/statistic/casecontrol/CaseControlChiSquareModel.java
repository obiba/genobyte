/*******************************************************************************
 * Copyright 2007(c) Genome Quebec. All rights reserved.
 * <p>
 * This file is part of GenoByte.
 * <p>
 * GenoByte is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * <p>
 * GenoByte is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.obiba.genobyte.statistic.casecontrol;

import java.util.List;

public interface CaseControlChiSquareModel extends ChiSquareModel {

  /**
   * Calculate the empirical probability value based on the empirical
   * frequencies
   *
   * @return the empirical p-value
   */
  public double calculateEmpiricalPValue(double referenceChiSquare, List<CaseControlGenotypeFrequency> freqs);

}
