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
package org.obiba.genobyte.inconsistency;

/**
 * Holds the record keys for the reproducibility errors identified by a {@link ReproducibilityErrorCalculator}.
 *
 * @param <K> the type of the compared records key
 */
public class ReproducibilityErrors<K> extends Inconsistencies {

  /**
   * The reference record's unique key
   */
  private K referenceKey_ = null;

  /**
   * The reference record's store index
   */
  private int referenceIndex_ = -1;

  /**
   * The replicate record's unique key
   */
  private K replicateKey_ = null;

  /**
   * The replicate record's store index
   */
  private int replicateIndex_ = -1;

  /**
   * @return the reference record's store index
   */
  public int getReferenceIndex() {
    return referenceIndex_;
  }

  /**
   * @return the reference record's key
   */
  public K getReferenceKey() {
    return referenceKey_;
  }

  /**
   * @return the replicate record's store index
   */
  public int getReplicateIndex() {
    return replicateIndex_;
  }

  /**
   * @return the replicate record's key
   */
  public K getReplicateKey() {
    return replicateKey_;
  }

  /**
   * @param referenceIndex the reference record's store index to set
   */
  public void setReferenceIndex(int referenceIndex) {
    referenceIndex_ = referenceIndex;
  }

  /**
   * @param referenceKey the reference record's key to set
   */
  public void setReferenceKey(K referenceKey) {
    referenceKey_ = referenceKey;
  }

  /**
   * @param replicateIndex the replicate record's store index to set
   */
  public void setReplicateIndex(int replicateIndex) {
    replicateIndex_ = replicateIndex;
  }

  /**
   * @param replicateKey the replicate record's key to set
   */
  public void setReplicateKey(K replicateKey) {
    replicateKey_ = replicateKey;
  }
}
