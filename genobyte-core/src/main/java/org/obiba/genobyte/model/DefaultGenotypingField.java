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

import java.util.EnumSet;

import org.obiba.genobyte.GenotypingField;


/**
 * Default implementation of the <tt>GenotypingField</tt> interface, defining the genotype-related fields
 * of the two <tt>BitwiseStore</tt> inside a <tt>GenotypingStore</tt> (assay and sample).
 * @see org.obiba.genobyte.GenotypingField
 */
public enum DefaultGenotypingField implements GenotypingField {

  /** A call frequency */ 
  FREQ_A,
  /** B call frequency */ 
  FREQ_B,
  /** H (heterozygote) call frequency */ 
  FREQ_H,
  /** U (unknown) call frequency */ 
  FREQ_U,
  /** Sum of good (A, B, H) calls */ 
  TOTAL_CALLS,
  /** Rate of total calls to all calls */ 
  CALL_RATE,
  /** Minor allele frequency */ 
  MAF,
  /** Hardy-Weinberg (p-value) */ 
  HW,
  /** Heterozygozity */ 
  HET,
  /** Date of the last update */ 
  LAST_UPDATE,

  /** Number of DNA reproducibility errors */ 
  REPRO_DNA,
  /** Number of tests (comparisions) done while counting the number of inconsistencies */ 
  REPRO_DNA_TESTS,
  /** Number of Assay reproducibility errors */ 
  REPRO_ASSAY,
  /** Number of tests (comparisions) done while counting the number of inconsistencies */ 
  REPRO_ASSAY_TESTS,
  /** Number of mendelian errors */ 
  MENDEL,
  /** Number of tests (comparisions) done while counting the number of inconsistencies */ 
  MENDEL_TESTS,

  /** Holds the genotypes (calls) */ 
  CALLS(true, true, true),
  /** Holds the comparable genotypes (calls that can be compared using the Field.diff method) */ 
  COMPARABLE_CALLS(true, true);

  //Whether this field can be created when it does not exist in the bitwise.
  private boolean creatable_ = false;
  //Wheter the values of this field should be transposed into the associated GenotypingRecordStore.
  private boolean transposed_ = false;
  //Whether to update the statistics of the associated record after transposing the values for this field.
  private boolean updateStats_ = false;


  private DefaultGenotypingField() {
    this(false, false);
  }


  private DefaultGenotypingField(boolean transposed, boolean creatable) {
    transposed_ = transposed;
    creatable_ = creatable;
  }


  private DefaultGenotypingField(boolean transposed, boolean creatable, boolean update) {
    transposed_ = transposed;
    creatable_ = creatable;
    updateStats_ = update;
  }


  public boolean isTransposed() {
    return transposed_;
  }


  public boolean isCreatable() {
    return creatable_;
  }


  public boolean updateStats() {
    return updateStats_;
  }


  public String getName() {
    return super.toString().toLowerCase();
  }


  /**
   * Returns a <tt>String</tt> representing a text version of this instance.
   * @return the <tt>String</tt>.
   */
  @Override
  public String toString() {
    return super.toString().toLowerCase();
  }


  /**
   * Returns a set of all genotyping fields that are used for sample records.
   * @return the set of genotyping fields.
   */
  public static EnumSet<DefaultGenotypingField> defaultSampleFields() {
    EnumSet<DefaultGenotypingField> s = EnumSet.allOf(DefaultGenotypingField.class);
    s.remove(HW);
    s.remove(HET);
    s.remove(MAF);
    s.remove(COMPARABLE_CALLS);
    return s;
  }


  /**
   * Returns a set of all genotyping fields that are used for assay records.
   * @return the set of genotyping fields.
   */
  public static EnumSet<DefaultGenotypingField> defaultAssayFields() {
    EnumSet<DefaultGenotypingField> s = EnumSet.allOf(DefaultGenotypingField.class);
    return s;
  }

}

