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
package org.obiba.genobyte.model;

/**
 * Strand part of a position in the human genome.
 */
public enum Orientation {

  /**
   * Positive strand
   */
  FORWARD,

  /**
   * Negative strand
   */
  REVERSE;

  /**
   * Determines the orientation from a <tt>String</tt> containing the strand sign.
   *
   * @param sign the sign of the strand.
   * @return the <tt>Orientation</tt> corresponding to the given strand sign.
   */
  static public Orientation parseSign(String sign) {
    if (sign != null) {
      if (sign.equals("+")) {
        return FORWARD;
      } else if (sign.equals("-")) {
        return REVERSE;
      }
    }
    throw new IllegalArgumentException("Cannot convert sign [" + sign + "] to Orientation.");
  }

  /**
   * Gets the strand sign for this <tt>Orientation</tt> instance, in a <tt>String</tt> format.
   *
   * @return the strand sign.
   */
  public String getSign() {
    if (this == FORWARD) return "+";
    if (this == REVERSE) return "-";
    throw new IllegalStateException();
  }
}
