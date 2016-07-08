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

/**
 * Implementation of the CaseControlChiSquareModel interface for a Genotype
 * model. This implementation uses the algorithm specified at
 * http://v9doc.sas.com/cgi-bin/sasdoc/cgigdoc?file=../geneug.hlp/casecontrol_sect10.htm
 */
public class GenotypeChiSquareModel extends BaseChiSquareModelImpl implements CaseControlChiSquareModel {

  public String getName() {
    return "genotype";
  }

  public double calculateChiSquare(CaseControlGenotypeFrequency ccgf) {
    long R = ccgf.getR();
    long S = ccgf.getS();
    long N = ccgf.getN();

    if(R == 0 || S == 0 || N == 0) {
      return Double.NaN;
    }

    double chiSquare = 0.0d;
    int rValues[] = ccgf.getRValues();
    int sValues[] = ccgf.getSValues();
    int nValues[] = ccgf.getNValues();

    for(int i = 0; i < 3; i++) {
      long r = rValues[i];
      long s = sValues[i];
      long n = nValues[i];

      if(n == 0) {
        return Double.NaN;
      }

      double f1 = Math.pow((N * r) - (R * n), 2.0d) / (N * R * n);
      double f2 = Math.pow((N * s) - (S * n), 2.0d) / (N * S * n);

      chiSquare += f1 + f2;
    }

    return chiSquare;
  }

  public int getFreedom() {
    return 2;
  }
}
