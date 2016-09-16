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
package org.obiba.genobyte;

import org.obiba.bitwise.*;
import org.obiba.bitwise.schema.FieldMetaData;
import org.obiba.bitwise.schema.StoreSchema;
import org.obiba.bitwise.util.BitwiseDiskUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GenotypingStoreTransposer<K, TK> implements StoreTransposer {

  private final Logger log = LoggerFactory.getLogger(GenotypingStoreTransposer.class);

  private TransposeProgressIndicator transposeProgressIndicator = null;

  private GenotypingRecordStore<K, ?, TK> sourceStore;

  /**
   * Flag that indicates whether or not to delete the destination store before transposing. It has been shown that transpo.
   */
  private boolean deleteDestinationStore = true;

  private List<GenotypingFieldValueTransposer<K, TK>> fieldTransposers_
      = new LinkedList<GenotypingFieldValueTransposer<K, TK>>();

  public GenotypingStoreTransposer(GenotypingRecordStore<K, ?, TK> source) {
    sourceStore = source;
    for (GenotypingField field : sourceStore.genotypingFields_.values()) {
      if (field.isTransposed()) {
        GenotypingFieldValueTransposer<K, TK> transposer = new GenotypingFieldValueTransposer<K, TK>(field, sourceStore,
            sourceStore.getTransposedStore());
        fieldTransposers_.add(transposer);
      }
    }
    log.debug("Created store transposer for {}", source.getStore().getName());
  }

  public void setDeleteDestinationStore(boolean deleteDestinationStore) {
    this.deleteDestinationStore = deleteDestinationStore;
  }

  @Override
  public void setTransposeProgressIndicator(TransposeProgressIndicator transposeProgressIndicator) {
    this.transposeProgressIndicator = transposeProgressIndicator;
  }

  public void transpose() {
    final BitwiseStore bitwiseSource = sourceStore.getStore();
    final String sourceName = bitwiseSource.getName();

    if (deleteDestinationStore) {
      log.debug("Locking store [{}]", sourceName);
      BitwiseStoreUtil.getInstance().lock(sourceName, new Runnable() {
        public void run() {
          log.debug("Lock obtained for [{}]", sourceName);
          log.debug("Copying source fields");

          Map<String, VolatileField> copy = new HashMap<String, VolatileField>();
          StoreSchema ss = bitwiseSource.getSchema();
          for (FieldMetaData fmd : ss.getFields()) {
            if (fmd.isTemplate() == false) {
              log.debug("Copying field [{}]", fmd.getName());
              Field sourceField = bitwiseSource.getField(fmd.getName());
              VolatileField destinationField = new VolatileField(fmd.getName(), bitwiseSource,
                  sourceField.getDictionary());
              copy.put(fmd.getName(), destinationField);
              destinationField.copyValues(sourceField);
            }
          }

          log.debug("Deleting source store [{}]", sourceName);
          BitVector deleted = bitwiseSource.getDeleted();
          try {
            bitwiseSource.endTransaction();
          } catch (RuntimeException e) {
            // ignore
          } finally {
            try {
              bitwiseSource.close();
            } catch (RuntimeException e) {
              // ignore
            }
          }
          BitwiseDiskUtil.deleteStore(sourceName);

          log.debug("Re-creating source store [{}]", sourceName);
          BitwiseStore bitwiseDestination = BitwiseStoreUtil.getInstance()
              .create(sourceName, ss, bitwiseSource.getCapacity());
          bitwiseDestination.undeleteAll();
          bitwiseDestination.delete(deleted);

          for (VolatileField copied : copy.values()) {
            log.debug("Copying field [{}]", copied.getName());
            Field sourceField = bitwiseDestination.getField(copied.getName());
            sourceField.copyValues(copied);
          }
          copy.clear();
          bitwiseDestination.flush();

          sourceStore.store_ = bitwiseDestination;
          sourceStore.manager_ = null;
        }
      });
    }

    FieldValueIterator<K> fvi = sourceStore.getRecordManager().keys();

    //Collect the value of the "unique" key for each store record 
    ArrayList<K> sourceKeys = new ArrayList<K>(sourceStore.getStore().getSize());
    int[] sourceIndexes = new int[sourceStore.getStore().getSize()];
    while (fvi.hasNext()) {
      FieldValueIterator<K>.FieldValue fv = fvi.next();
      sourceIndexes[sourceKeys.size()] = fv.getIndex();
      sourceKeys.add(fv.getValue());
    }

    long processedFields = 0;

    log.debug("Transposing store... (" + (long) fieldTransposers_.size() + " fields to process)");

    if (transposeProgressIndicator != null) {
      transposeProgressIndicator.setTotalItemsToTranspose((long) fieldTransposers_.size());
      transposeProgressIndicator.setProcessedItems(0);
    }

    for (GenotypingFieldValueTransposer<K, TK> t : fieldTransposers_) {
      t.transposeValues(sourceKeys, sourceIndexes);
      if (transposeProgressIndicator != null) {
        transposeProgressIndicator.setProcessedItems(processedFields++);
      }
    }
  }
}
