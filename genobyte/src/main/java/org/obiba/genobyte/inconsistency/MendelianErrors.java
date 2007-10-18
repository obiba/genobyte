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
package org.obiba.genobyte.inconsistency;

/**
 * Holds the record keys for the mendelian errors identified by a {@link MendelianErrorCalculator}.
 * @param <K> the type of the record key
 */
public class MendelianErrors<K> extends Inconsistencies {

  /** The child record's key */
  private K childKey_ = null;
  /** The child record's store index */
  private int childIndex_ = -1;
  /** The mother record's key */
  private K motherKey_ = null;
  /** The mother record's store index */
  private int motherIndex_ = -1;
  /** The father record's key */
  private K fatherKey_ = null;
  /** The father record's store index */
  private int fatherIndex_ = -1;

  /**
   * @return the child record's store index
   */
  public int getChildIndex() {
    return childIndex_;
  }
  /**
   * @return the child record's key
   */
  public K getChildKey() {
    return childKey_;
  }
  /**
   * @return the father record's store index
   */
  public int getFatherIndex() {
    return fatherIndex_;
  }
  /**
   * @return the father record's key
   */
  public K getFatherKey() {
    return fatherKey_;
  }
  /**
   * @return the mother record's store index
   */
  public int getMotherIndex() {
    return motherIndex_;
  }
  /**
   * @return the mother record's key
   */
  public K getMotherKey() {
    return motherKey_;
  }
  /**
   * @param childIndex the childIndex to set
   */
  public void setChildIndex(int childIndex) {
    childIndex_ = childIndex;
  }
  /**
   * @param childKey the childKey to set
   */
  public void setChildKey(K childKey) {
    childKey_ = childKey;
  }
  /**
   * @param fatherIndex the fatherIndex to set
   */
  public void setFatherIndex(int fatherIndex) {
    fatherIndex_ = fatherIndex;
  }
  /**
   * @param fatherKey the fatherKey to set
   */
  public void setFatherKey(K fatherKey) {
    fatherKey_ = fatherKey;
  }
  /**
   * @param motherIndex the motherIndex to set
   */
  public void setMotherIndex(int motherIndex) {
    motherIndex_ = motherIndex;
  }
  /**
   * @param motherKey the motherKey to set
   */
  public void setMotherKey(K motherKey) {
    motherKey_ = motherKey;
  }

}
