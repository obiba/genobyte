/*******************************************************************************
 * Copyright 2007(c) G�nome Qu�bec. All rights reserved.
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
import java.util.Arrays;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.util.BitVectorQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allows reading values from one <tt>GenotypingStore</tt> and transpose them into another <tt>GenotypingStore</tt>. 
 * Fields that may be transposed are the ones that are present in both stores (such as Calls).
 */
class GenotypingFieldValueTransposer<K, TK> {

  private final Logger log = LoggerFactory.getLogger(GenotypingFieldValueTransposer.class);

  // Size of one transpose block
  private long transposeBlockSize_ = 100 * 1024 * 1024;

  // The field being transposed from source to destination (ie: calls field)
  private GenotypingField field_ = null;
  // The source of the transposed values
  private GenotypingRecordStore<K, ?, TK> source_ = null;
  // The destination of the transposed values
  private GenotypingRecordStore<TK, ?, K> destination_ = null;

  public GenotypingFieldValueTransposer(GenotypingField field, GenotypingRecordStore<K, ?, TK> source, GenotypingRecordStore<TK, ?, K> destination) {
    if(field == null) {
      throw new NullPointerException("field argument");
    }
    if(source == null) {
      throw new NullPointerException("source argument");
    }
    if(destination == null) {
      throw new NullPointerException("destination argument");
    }

    field_ = field;
    source_ = source;
    destination_ = destination;
    if(source.getTransposeMemSize() > 0) {
      this.transposeBlockSize_ = source.getTransposeMemSize();
    }
  }

  /**
   * Gets the size of the data block used to transport data from one store to the other.
   * @return the current block size.
   */
  public long getTransposeBlockSize() {
    return transposeBlockSize_;
  }

  /**
   * Sets the size of the data block used to transport data from one store to the other.
   * @param b the block size to set.
   */
  public void setTransposeBlockSize(long b) {
    transposeBlockSize_ = b;
  }

  /**
   * Transposes the values of the specified source records into the destination store.
   * <p/>
   * This method will split the destination fields into blocks of the size specified by
   * the transposeBlockSize property. For each source record, the values for each transposed
   * record is read for each block. The values are then stored into the appropriate destination 
   * field.
   * <p/>
   * The larger the block size, the more RAM is required to do the work, but the less time it will take.
   * @param sourceKeys a List of records unique keys to be transposed
   */
  void transposeValues(ArrayList<K> sourceKeysList, int[] sourceIndexes) {
    // If there are no record keys, nothing to do
    if(sourceKeysList == null || sourceKeysList.size() == 0) {
      return;
    }

    //Put in an array the index of all records that are to be transposed
    log.debug("Transposing [{}] values for field [{}]", sourceKeysList.size(), field_.getName());

    // Find an example source field to make some calculations
    Field sourceField = null;
    for (K key : sourceKeysList) {
      sourceField = this.source_.getGenotypingField(this.field_.getName(), key);
      if(sourceField != null) {
        break;
      }
    }

    if(sourceField == null) {
      // No data to transpose, do nothing.
      return;
    }

    // Number of records in destination
    int targetColumns = this.source_.getStore().getSize();

    // Number of bytes ONE target field requires when in memory. There is an overhead due to using objects to hold these bytes though...
    long targetFieldSize = ((targetColumns >> 6 ) + 1 ) * 8 * sourceField.getDictionary().dimension() + 2048;

    log.debug("Calculated target field size is [{}] bytes.", targetFieldSize);

    // Number of target fields to populate in one iteration 
    int nbTargetFieldsPerIteration = (int)(this.transposeBlockSize_ / targetFieldSize);
    if(nbTargetFieldsPerIteration == 0)
      nbTargetFieldsPerIteration = 1;

    log.debug("Number of target fields per iteration is [{}]", nbTargetFieldsPerIteration);

    int nbTargets = this.destination_.getStore().getSize();
    int nbIterations = (int)Math.ceil(nbTargets / (double)nbTargetFieldsPerIteration);
    log.debug("Number of iterations is [{}]", nbIterations);

    TransposeIterationBlock block = new TransposeIterationBlock(nbTargetFieldsPerIteration);
    block.sourceKeysList = sourceKeysList;
    block.sourceIndexes = sourceIndexes;

    if(field_.updateStats()) {
      block.targetIndexVector = new BitVector(destination_.getStore().getCapacity());
    }

    for(int i = 0; i < nbIterations; i++) {
      log.debug("iteration [{}]", (i+1));

      // Determine the first targetIndex for this block. It should be the previous' last targetIndex + 1 or 0 if this is the first block.
      int firstTargetIndex = 0;
      if(block.targetCount > 0) {
        firstTargetIndex = block.targetIndexes[block.targetCount - 1] + 1;
      }
      makeIterationBlock(firstTargetIndex, nbTargetFieldsPerIteration, block);
      processBlock(block);
      log.debug("flushing to disk");
      source_.getStore().flush();
      // Release all the locks in the JE log files
      // See http://forums.oracle.com/forums/thread.jspa?threadID=475591
      log.debug("freeing JE locks");
      source_.getStore().commitTransaction();
      source_.getStore().endTransaction();
      source_.getStore().startTransaction();
    }

    destination_.getStore().flush();
    log.debug("Transposed [{}] keys for field [{}]", sourceKeysList.size(), field_.getName());
  }

  private void makeIterationBlock(int firstIndex, int fieldsPerIteration, TransposeIterationBlock block) {
    log.debug("creating block starting at record index [{}]", firstIndex);
    Arrays.fill(block.targetIndexes, -1);

    block.targetCount = 0;
    int nextIndex = firstIndex;
    for(int i = 0; i < fieldsPerIteration; i++) {
      int targetIndex = destination_.getStore().nextRecord(nextIndex);
      if(targetIndex == -1) break;
      nextIndex = targetIndex+1;

      TK targetKey = this.destination_.getRecordManager().getKey(targetIndex);
      block.targetCount++;
      block.targetIndexes[i] = targetIndex;
      if(block.targetIndexVector != null) block.targetIndexVector.set(targetIndex);
      block.targetFields[i] = this.destination_.getGenotypingField(this.field_.getName(), targetKey, true);
    }
    log.debug("block size is [{}]", block.targetCount);
  }

  /**
   * Actually copies the source data into the destination for the specified iteration block.
   *
   * @param block the iteration block to process
   */
  private void processBlock(TransposeIterationBlock block) {
    log.debug("processing block");

    for(int i = 0; i < block.sourceIndexes.length; i++) {
      int sourceIndex = block.sourceIndexes[i];
      K sourceKey = block.sourceKeysList.get(i);
      Field sourceField = source_.getGenotypingField(field_.getName(), sourceKey);
      if(sourceField != null) {
        for(int target = 0; target < block.targetCount; target++) {
          // Copy the value from the sourceField at targetIndex into the targetField at sourceIndex
          // The reason why the targetIndex is used in the sourceField is because the transposed field is in the destination BitwiseStore
          int targetIndex = block.targetIndexes[target];
          block.targetFields[target].copyValue(sourceIndex, targetIndex, sourceField);
        }
        // Remove from cache to save memory if possible
        if(sourceField.isDirty() == false) {
          destination_.getStore().detach(sourceField);
        }
      }
    }
    if(field_.updateStats() && block.targetIndexVector.count() > 0) {
      log.debug("updating stats for block [{}]", block.targetIndexVector.count());
      destination_.updateStats(new BitVectorQueryResult(block.targetIndexVector));
      block.targetIndexVector.clearAll();
    }
  }

  /**
   * Utility class to hold the destination fields into which the source data is copied into during one of the
   * possibly multiple iterations of the transposition process. 
   */
  private class TransposeIterationBlock {
    ArrayList<K> sourceKeysList;
    int[] sourceIndexes;

    // The number of target records in this block.
    int targetCount = -1;
    // Array of target record index
    int[] targetIndexes;
    // An optional BitVector of target record indexes used for updating the statistics
    BitVector targetIndexVector;
    // Array of target fields
    Field[] targetFields;

    TransposeIterationBlock(int size) {
      targetIndexes = new int[size];
      targetFields = new Field[size];
    }
  }
  
}
