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
package org.obiba.genobyte.statistic.util;

/**
 * Utility class to compute the gamma function which may be used to compute a Chi-Square probability.
 * <br/>
 * Algorithm was taken from Numerical Recipes in C: The Art of Scientific Computing (ISBN 0-521-43108-5). 
 */
final public class ChiSquare {

  /** Maximum number of iterations to converge. */
  private static final int ITMAX = 100;

  /** An epsilon value used to determine convergence */
  private static final double EPS = 3e-7;
  
  /** A very small value used during computation. Defined as "near the smallest representable floating-point number" in the reference algorithm. */ 
  private static final double FPMIN = 1.0e-30;

  /** Some computing coefficients for the ln(Gamma) function. */
  private static final double GAMMA_COF[] = {76.18009172947146d, -86.50532032941677d, 24.01409824083091d,-1.231739572450155d, 0.1208650973866179e-2d, -0.5395239384953e-5d};

  /**
   * Returns P(chi2, v) defined as the probability that the observed chi-square for a correct model should be less than a value chi2.
   *
   * @param chi the chi-square value
   * @param df the number of degrees of freedom
   * @return the chi-square probability
   */
  static final public double chiSquareP(double chi, int df) {
    return gammap(df/2.0d, chi/2.0d);
  }

  /**
   * Returns Q(chi2, v) defined as the probability that the observed chi-square will exceed the value chi2 by chance <em>even</em> for a correct model.
   * 
   * <pre>
   * Q(chi2, v) = 1 - P(chi2, v)
   * </pre>
   *
   * @param chi the chi-square value
   * @param df the number of degrees of freedom
   * @return the chi-square probability
   */
  static final public double chiSquareQ(double chi, int df) {
    return gammaq(df/2.0d, chi/2.0d);
  }

  /**
   * Returns the incomplete gamma function P(a,x)
   *
   * @return the incomplete gamma function P(a,x)
   */
  static final public double gammap(double a, double x) {
    if(x < 0.0 || a <= 0.0) {
      throw new IllegalArgumentException();
    }

    if(x < (a + 1.0)) {
      return gser(a, x);
    } else {
      return 1.0 - gcf(a,x);
    }
  }

  /**
   * Returns the incomplete gamma function Q(a,x) = 1 - P(a, x)
   *
   * @return the incomplete gamma function Q(a,x) = 1 - P(a, x)
   */
  static final public double gammaq(double a, double x) {
    if(x < 0.0 || a <= 0.0) {
      throw new IllegalArgumentException();
    }

    if(x < (a + 1.0)) {
      return 1.0 - gser(a, x);
    } else {
      return gcf(a,x);
    }
  }

  /**
   * Implementation of equation 6.2.5 from Numerical Recipes in C p. 218.
   */
  static final private double gser(double a, double x) {
    double sum, del, ap; 
    double gln = gammln(a);
    
    if(x <= 0) {
      if(x < 0) {
        throw new IllegalArgumentException();
      }
      return 0.0d;
    } else {
      ap = a;
      del = sum = 1.0 / a;
      for(int n = 1; n <= ITMAX; n++) {
        ++ap;
        del *= x / ap;
        sum += del;
        if(Math.abs(del) < Math.abs(sum) * EPS) {
          return sum * Math.exp(-x + a * Math.log(x) - gln);
        }
      }
    }

    throw new ArithmeticException("a too large, ITMAX too small in routine gser");
  }

  /**
   * Implementation of equation 6.2.7 from Numerical Recipes in C p. 219.
   */
  static final private double gcf(double a, double x) {
    double an, b, c, d, del, h;
    double gln = gammln(a);
    b = x + 1.0 - a;
    c = 1.0 / FPMIN;
    h = d = 1.0 / b;
    for(int n = 1; n <= ITMAX; n++) {
      an = -n * (n - a);
      b += 2.0;
      d = an * d + b;
      if(Math.abs(d) < FPMIN) d = FPMIN;
      c = b + an / c;
      if(Math.abs(c) < FPMIN) c = FPMIN;
      d = 1.0 / d;
      del = d * c;
      h *= del;
      if(Math.abs(del - 1.0) < EPS)  {
        return Math.exp(-x + a * Math.log(x) - gln) * h;
      }
    }
    throw new ArithmeticException("a too large, ITMAX too small in routine gcf");
  }
  
  /**
   * Returns the natural logarithm of the gamma function for d where d > 0. Implementation of
   * equation 6.1.5 from Numerical Recipes in C p. 214.
   *
   * @param d
   * @return ln(gamma(d))
   */
  static final public double gammln(double d) {
    double x, y, temp, ser;
    y = x = d;
    temp = x + 5.5;
    temp -= (x+0.5) * Math.log(temp);
    ser = 1.000000000190015d;
    for(int j = 0; j < 6; j++) {
      ser += GAMMA_COF[j] / ++y;
    }
    return -temp + Math.log(2.5066282746310005d * ser / x);
  }

}
