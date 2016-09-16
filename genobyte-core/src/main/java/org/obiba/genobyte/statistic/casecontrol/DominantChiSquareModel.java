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

public class DominantChiSquareModel extends BaseChiSquareModelImpl implements CaseControlChiSquareModel {

  public String getName() {
    return "dominant";
  }

  public double calculateChiSquare(CaseControlGenotypeFrequency ccgf) {
    int R = ccgf.getR();
    int N = ccgf.getN();

    if (R == 0 || N == 0) {
      return Double.NaN;
    }

    int r1 = 0;
    int r2 = ccgf.getR(1) + ccgf.getR(2);

    int s1 = 0;
    int s2 = ccgf.getS(1) + ccgf.getS(2);

    int n1 = r1 + s1;
    int n2 = r2 + s2;

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
}
