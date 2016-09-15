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
 * Implementation of the CaseControlChiSquareModel interface for an Additive
 * model. This implementation uses the algorithm specified at
 * http://v9doc.sas.com/cgi-bin/sasdoc/cgigdoc?file=../geneug.hlp/casecontrol_sect10.htm.
 * The algorithm was found to be erronous and was corrected using the Peter D. Sasieni
 * paper.
 */
public class AdditiveChiSquareModel extends BaseChiSquareModelImpl {

  public double calculateChiSquare(CaseControlGenotypeFrequency ccgf) {
    int R = ccgf.getR();
    int N = ccgf.getN();

    if (R == 0 || N == 0) {
      return Double.NaN;
    }

    int r1 = ccgf.getR(1);
    int r2 = ccgf.getR(2);

    int n1 = ccgf.getN(1);
    int n2 = ccgf.getN(2);

    double f = N * (r1 + 2 * r2) - R * (n1 + 2 * n2);
    double numerator = N * f * f;

    f = n1 + 2 * n2;
    double denominator = R * (N - R) * (N * (n1 + 4 * n2) - (f * f));

    if (Double.compare(denominator, 0.0d) == 0) {
      return Double.NaN;
    }

    return numerator / denominator;
  }

  public int getFreedom() {
    return 1;
  }

  public String getName() {
    return "additive";
  }
}
