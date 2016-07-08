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
 * Encapsulates the 2x3 genotype frequency matrix from
 * <em>From Genotypes to Genes: Doubling the Sample Size</em>
 * Peter D. Sasieni<br/>
 * Biometrics, Vol. 53, No. 4. (Dec., 1997), pp. 1253-1261.)<br/>
 *
 * <table>
 *   <tr>
 *     <td/>
 *     <td>A</td>
 *     <td>H</td>
 *     <td>B</td>
 *     <td/>
 *   </tr>
 *   <tr>
 *     <td>Cases</td>
 *     <td>r0</td>
 *     <td>r1</td>
 *     <td>r2</td>
 *     <td>R</td>
 *   </tr>
 *   <tr>
 *     <td>Controls</td>
 *     <td>s0</td>
 *     <td>s1</td>
 *     <td>s2</td>
 *     <td>S</td>
 *   </tr>
 *   <tr>
 *     <td></td>
 *     <td>n0</td>
 *     <td>n1</td>
 *     <td>n2</td>
 *     <td>N</td>
 *   </tr>
 * </table>
 */
final public class CaseControlGenotypeFrequency {

  private GenotypeFrequency caseFreq = null;

  private GenotypeFrequency controlFreq = null;

  private int asArray_[] = null;

  CaseControlGenotypeFrequency(GenotypeFrequency caseFreq, GenotypeFrequency controlFreq) {
    this.caseFreq = caseFreq;
    this.controlFreq = controlFreq;
  }

  final public int getS() {
    return controlFreq.getTotal();
  }

  final public int getS(int index) {
    return controlFreq.get(index);
  }

  final public int[] getSValues() {
    return controlFreq.toArray();
  }

  final public int getR() {
    return caseFreq.getTotal();
  }

  final public int getR(int index) {
    return caseFreq.get(index);
  }

  final public int[] getRValues() {
    return caseFreq.toArray();
  }

  final public int getN(int index) {
    return getR(index) + getS(index);
  }

  final public int getN() {
    return caseFreq.getTotal() + controlFreq.getTotal();
  }

  final public int[] getNValues() {
    if(asArray_ == null) {
      asArray_ = new int[] { getN(0), getN(1), getN(2) };
    }
    return asArray_;
  }

}