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

public enum SnpCall {

  // Moving these around will break existing bitwise stores. You may only append new calls.
  U, A, B, C, D, H;

  static public SnpCall valueOf(char c) {
    switch(c) {
      case 'a':
      case 'A':
        return A;
      case 'b':
      case 'B':
        return B;
      case 'c':
      case 'C':
        return C;
      case 'd':
      case 'D':
        return D;
      case 'h':
      case 'H':
        return H;
      case 'u':
      case 'U':
        return U;
      default:
        throw new IllegalArgumentException("Unknown SnpCall [" + c + "]");
    }
  }
}
