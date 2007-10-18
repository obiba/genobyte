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
 * Chromosome part of the position in the human genome.
 */
public enum Chromosome {
  /** Chromosome 1 */ chr1,
  /** Chromosome 2 */ chr2,
  /** Chromosome 3 */ chr3,
  /** Chromosome 4 */ chr4,
  /** Chromosome 5 */ chr5,
  /** Chromosome 6 */ chr6,
  /** Chromosome 7 */ chr7,
  /** Chromosome 8 */ chr8,
  /** Chromosome 9 */ chr9,
  /** Chromosome 10 */ chr10,
  /** Chromosome 11 */ chr11,
  /** Chromosome 12 */ chr12,
  /** Chromosome 13 */ chr13,
  /** Chromosome 14 */ chr14,
  /** Chromosome 15 */ chr15,
  /** Chromosome 16 */ chr16,
  /** Chromosome 17 */ chr17,
  /** Chromosome 18 */ chr18,
  /** Chromosome 19 */ chr19,
  /** Chromosome 20 */ chr20,
  /** Chromosome 21 */ chr21,
  /** Chromosome 22 */ chr22,
  /** Chromosome X */ chrX,
  /** Chromosome Y */ chrY,
  
  /** A chromosome that couldn't be read or couldn't be properly entered. */
  chrBad,
  
  /**Used by illumina for diploid snps on XY*/
  chrXY,
  
  /** Mitochondrion */
  chrMT,
  
  /** Heterogametic female gender chromosome. Seen in birds */
  chrW,
  
  /** Male part of the heterogametic gender chromosome. Seen in birds */
  chrZ,
  
  /** Chromosome 23 */ chr23,
  /** Chromosome 24 */ chr24,
  /** Chromosome 25 */ chr25,
  /** Chromosome 26 */ chr26,
  /** Chromosome 27 */ chr27,
  /** Chromosome 28 */ chr28,
  /** Chromosome 29 */ chr29,
  /** Chromosome 30 */ chr30,
  /** Chromosome 31 */ chr31,
  /** Chromosome 32 */ chr32,
  /** Chromosome 33 */ chr33,
  /** Chromosome 34 */ chr34,
  /** Chromosome 35 */ chr35,
  /** Chromosome 36 */ chr36,
  /** Chromosome 37 */ chr37,
  /** Chromosome 38 */ chr38;
}
