/*******************************************************************************
 * Copyright 2007(c) Genome Quebec. All rights reserved.
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
package org.obiba.genobyte;

import java.util.ArrayList;

import org.obiba.bitwise.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allows reading values from one <tt>GenotypingStore</tt> and transpose them into another <tt>GenotypingStore</tt>. 
 * Fields that may be transposed are the ones that are present in both stores (such as Calls).
 */
class FastGenotypingFieldValueTransposer<K> implements Runnable {
  private final Logger log = LoggerFactory.getLogger(FastGenotypingFieldValueTransposer.class);

  private final Field destField;
  private final int destIndex;
  private final int[] sourceIndexes;
  private final Field[] sourceFields;
  private final ArrayList<K> sourceKeys;

  public FastGenotypingFieldValueTransposer(Field destField, int destIndex, int[] sourceIndexes, Field[] sourceFields, ArrayList<K> sourceKeys) {
    if(destField == null) {
      throw new NullPointerException("destField argument");
    }
    if(destIndex < 0) {
      throw new IllegalArgumentException("destIndex argument");
    }
    if(sourceIndexes == null) {
      throw new NullPointerException("sourceIndexes argument");
    }
    if(sourceFields == null) {
      throw new NullPointerException("sourceFields argument");
    }
    if(sourceKeys == null) {
      throw new NullPointerException("sourceKeys argument");
    }

    this.destField = destField;
    this.destIndex = destIndex;
    this.sourceIndexes = sourceIndexes;
    this.sourceFields = sourceFields;
    this.sourceKeys = sourceKeys;
  }

  public void run() {
    // Don't mistake idx with a field index. It's just an Array index. 
    for(int idx=0; idx < sourceIndexes.length; idx++) {
      // In the destination field, the source is the index and destination is the index
      // in the source. We are working in a transposed world.
      if (sourceFields[idx] != null) {
      	destField.copyValue(sourceIndexes[idx], destIndex, sourceFields[idx]);
      }
    }
  }
}
