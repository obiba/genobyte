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
package org.obiba.genobyte.model;

/**
 * Allele value at a specific position, like a marker.
 */
public enum SnpAllele {

  /** Allele Value A */ A,
  /** Allele Value C */ C,
  /** Allele Value G */ G,
  /** Allele Value T */ T,
  
  /** Allele Value could not be obtained (for example, because of a genotyping error). */
  U,
  
  //TODO: Explain N value
  N,
  
  //TODO: Explain X value
  X,
  
  //TODO: Explain Y value
  Y,
  
  //TODO: Explain DEL value
  DEL;

  
  /**
   * Gets the <tt>Enum</tt> value corresponding to the given <tt>String</tt> allele code.
   * @param v the <tt>String</tt> allele code.
   * @return the corresponding <tt>Enum</tt> value.
   */
  static public SnpAllele parseAllele(String v) {
    if(v.equals("-")) return DEL;
    return valueOf(v);
  }


  /**
   * Returns a <tt>String</tt> representing this <tt>Enum</tt> instance value. 
   * @return the <tt>String</tt> representation.
   */
  @Override
  public String toString() {
    switch(this) {
      case DEL: 
        return "-";
      default:
        return super.toString();
    }
  }

}
