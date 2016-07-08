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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.BitwiseStoreUtil;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.FieldValueIterator;
import org.obiba.bitwise.VolatileField;
import org.obiba.bitwise.schema.FieldMetaData;
import org.obiba.bitwise.schema.StoreSchema;
import org.obiba.bitwise.util.BitwiseDiskUtil;
import org.obiba.bitwise.util.PrintGC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FastGenotypingStoreTransposer<K, TK> implements StoreTransposer {
  private final Logger log = LoggerFactory.getLogger(FastGenotypingStoreTransposer.class);

  private final ThreadGroup transposerThreadGroup;

  private final int corePoolSize;

  private final int maximumPoolSize;

  private final long keepAliveTime;

  private final TimeUnit unit;

  private TransposeProgressIndicator transposeProgressIndicator = null;

  private GenotypingRecordStore<K, ?, TK> sourceStore;

  /**
   * Flag that indicates whether or not to delete the destination store before transposing. It has been shown that
   * transpo.
   */
  private boolean deleteDestinationStore = true;

  public FastGenotypingStoreTransposer(GenotypingRecordStore<K, ?, TK> source) {
    this(source, 1, 1, 1, TimeUnit.MILLISECONDS);
  }

  public FastGenotypingStoreTransposer(GenotypingRecordStore<K, ?, TK> source, int corePoolSize, int maximumPoolSize,
      long keepAliveTime, TimeUnit unit) {
    this.sourceStore = source;
    this.corePoolSize = corePoolSize;
    this.maximumPoolSize = maximumPoolSize;
    this.keepAliveTime = keepAliveTime;
    this.unit = unit;

    this.transposerThreadGroup = new ThreadGroup("FastGenotypingStoreTransposerThreadGroup");
    // Set daemon so they get killed when the VM is being shut down.
    transposerThreadGroup.setDaemon(true);
    log.debug("Created store transposer for {}", source.getStore().getName());
  }

  public void setDeleteDestinationStore(boolean deleteDestinationStore) {
    this.deleteDestinationStore = deleteDestinationStore;
  }

  @Override
  public void setTransposeProgressIndicator(TransposeProgressIndicator transposeProgressIndicator) {
    this.transposeProgressIndicator = transposeProgressIndicator;
  }

  @Override
  public void transpose() {
    log.debug("Transposing store...started");
    BitwiseStore bitwiseSource = sourceStore.getStore();

    BlockingQueue<Runnable> fieldTransposerQueue = new LinkedBlockingQueue<Runnable>();
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit,
        fieldTransposerQueue, new FastGenotypingStoreTransposerThreadFactory());

    if(deleteDestinationStore) {
      deleteDestinationStore(bitwiseSource);
    }

    List<GenotypingField> genotypingFieldsToTranspose = new ArrayList<GenotypingField>();
    for(GenotypingField field : sourceStore.genotypingFields_.values()) {
      if(field.isTransposed()) {
        genotypingFieldsToTranspose.add(field);
      }
    }

    if(transposeProgressIndicator != null) {
      transposeProgressIndicator.setTotalItemsToTranspose(
          (long) genotypingFieldsToTranspose.size() * (long) sourceStore.getTransposedStore().getStore().getSize());
      transposeProgressIndicator.setProcessedItems(0);
    }
    long processedFields = 0;
    for(GenotypingField field : genotypingFieldsToTranspose) {
      if(log.isDebugEnabled()) {
        log.debug("Transposing field: {}...started", field.getName());
        log.debug("Memory used begining of field: {}", (PrintGC.printGC() / 1024l / 1024l));
      }
      // Collect the value of the "unique" key for each store record
      ArrayList<K> sourceKeys = new ArrayList<K>(sourceStore.getStore().getSize());
      int[] sourceIndexes = new int[sourceStore.getStore().getSize()];
      Field[] sourceFields = new Field[sourceStore.getStore().getSize()];
      log.debug("Loading source keys ...");
      FieldValueIterator<K> fvi = sourceStore.getRecordManager().keys();
      while(fvi.hasNext()) {
        FieldValueIterator<K>.FieldValue fv = fvi.next();

        K key = fv.getValue();
        Field gf = sourceStore.getGenotypingField(field.getName(), key);

        sourceIndexes[sourceKeys.size()] = fv.getIndex();
        sourceFields[sourceKeys.size()] = gf;
        sourceKeys.add(fv.getValue());
      }

      log.debug("Loading source keys ... done");
      FieldValueIterator<TK> tfvi = sourceStore.getTransposedStore().getRecordManager().keys();
      long itemsToProcess = 0;
      while(tfvi.hasNext()) {
        FieldValueIterator<TK>.FieldValue fv = tfvi.next();
        Field destField = sourceStore.getTransposedStore().getGenotypingField(field.getName(), fv.getValue(), true);
        FastGenotypingFieldValueTransposer<K> fgfvt = new FastGenotypingFieldValueTransposer<K>(destField,
            fv.getIndex(), sourceIndexes, sourceFields, sourceKeys);
        threadPoolExecutor.execute(fgfvt);
        itemsToProcess++;
      }
      log.debug("Field [{}] - Items that will be process : {}", field.getName(), itemsToProcess);
      log.debug("Current state of threadPool - Queue size: [{}] - Active Count: [{}]",
          threadPoolExecutor.getQueue().size(), threadPoolExecutor.getActiveCount());

      while(threadPoolExecutor.getQueue().size() != 0 || threadPoolExecutor.getActiveCount() != 0) {
        try {
          Thread.sleep(10000);
          if(transposeProgressIndicator != null) {
            transposeProgressIndicator.setProcessedItems((itemsToProcess - threadPoolExecutor.getQueue().size()) +
                (processedFields * sourceStore.getTransposedStore().getStore().getSize()));
          }
          // log.debug("Current state of threadPool - Queue size: [{}] - Active Count: [{}]",
          // threadPoolExecutor.getQueue().size(), threadPoolExecutor.getActiveCount());
        } catch(InterruptedException e) {
          log.error("Store Transposer was interrupted", e);
        }
      }
      if(transposeProgressIndicator != null) {
        transposeProgressIndicator.setProcessedItems(itemsToProcess - threadPoolExecutor.getQueue().size() +
            (processedFields * sourceStore.getTransposedStore().getStore().getSize()));
      }

      if(log.isDebugEnabled()) {
        log.debug("Memory used end of field: {}", (PrintGC.printGC() / 1024l / 1024l));
      }

      // Release all the locks in the JE log files
      // See http://forums.oracle.com/forums/thread.jspa?threadID=475591
      log.debug("Field [{}] - freeing JE locks", field.getName());
      sourceStore.getStore().commitTransaction();
      sourceStore.getStore().endTransaction();
      sourceStore.getStore().startTransaction();
      // write assays
      sourceStore.getTransposedStore().getStore().flush();
      log.debug("Transposing field: {}...done", field.getName());
      processedFields++;
    }
    threadPoolExecutor.shutdown();
    log.debug("Transposing store...done");
  }

  private void deleteDestinationStore(final BitwiseStore bitwiseSource) {
    final String sourceName = bitwiseSource.getName();

    log.debug("Locking store [{}]", sourceName);
    BitwiseStoreUtil.getInstance().lock(sourceName, new Runnable() {
      @Override
      public void run() {
        log.debug("Lock obtained for [{}]", sourceName);
        log.debug("Copying source fields");

        Map<String, VolatileField> copy = new HashMap<String, VolatileField>();
        StoreSchema ss = bitwiseSource.getSchema();
        for(FieldMetaData fmd : ss.getFields()) {
          if(fmd.isTemplate() == false) {
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
        } catch(RuntimeException e) {
          // ignore
        } finally {
          try {
            bitwiseSource.close();
          } catch(RuntimeException e) {
            // ignore
          }
        }
        BitwiseDiskUtil.deleteStore(sourceName);

        log.debug("Re-creating source store [{}]", sourceName);
        BitwiseStore bitwiseDestination = BitwiseStoreUtil.getInstance()
            .create(sourceName, ss, bitwiseSource.getCapacity());
        bitwiseDestination.undeleteAll();
        bitwiseDestination.delete(deleted);

        for(VolatileField copied : copy.values()) {
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

  private class FastGenotypingStoreTransposerThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {
      Thread retVal = new Thread(transposerThreadGroup, r, sourceStore.getStore().getName() + "_TransposerThread");
      retVal.setDaemon(true);
      return retVal;
    }

  }
}
