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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for implementations of the CaseControlChiSquareModel interface
 * that implements the
 * {@link CaseControlChiSquareModel#calculateEmpiricalPValue(double, List)}
 * method. Derived classes must implement the <code>calculateChiSquare</code>
 * and <code>getFreedom</code> methods.
 */
public abstract class BaseChiSquareModelImpl implements CaseControlChiSquareModel {

  private final Logger log = LoggerFactory.getLogger(BaseChiSquareModelImpl.class);

  protected BaseChiSquareModelImpl() {
  }

  public double calculateEmpiricalPValue(double referenceChiSquare, List<CaseControlGenotypeFrequency> empiricalValues) {
    if (empiricalValues == null || empiricalValues.isEmpty() == true) {
      return 0;
    }

    double ePValue;
    // The empirical value sample size (total number of empirical values)
    int samplingSize = empiricalValues.size();
    // The number of empirical chi-square values higher than the reference chi-square
    int pValue = 0;

    for (CaseControlGenotypeFrequency empiricalValue : empiricalValues) {
      double chiSquare = calculateChiSquare(empiricalValue);
      if (Double.compare(chiSquare, 0.0) < 0) {
        log.warn("Chi square model is erronous: chiSquare=[{}] from frequency=[{}]", chiSquare, empiricalValue);
        samplingSize--;
        continue;
      }

      if (Double.compare(chiSquare, referenceChiSquare) > 0) {
        pValue++;
      }
    }

    // Sanity check
    if (samplingSize == 0) {
      ePValue = Double.NaN;
    } else {
      ePValue = (double) pValue / (double) samplingSize;
    }
    return ePValue;
  }

  abstract public int getFreedom();
}
