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
package org.obiba.genobyte;

import org.obiba.bitwise.BitwiseRecordManager;
import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.BitwiseStoreUtil;
import org.obiba.genobyte.inconsistency.MendelianErrorCalculator;
import org.obiba.genobyte.inconsistency.MendelianErrorCountingStrategy;
import org.obiba.genobyte.inconsistency.ReproducibilityErrorCalculator;
import org.obiba.genobyte.inconsistency.ReproducibilityErrorCountingStrategy;
import org.obiba.genobyte.inconsistency.util.DefaultMendelianErrorCountingStrategy;
import org.obiba.genobyte.inconsistency.util.DefaultReproducibilityErrorCountingStrategy;
import org.obiba.genobyte.model.DefaultGenotypingField;
import org.obiba.genobyte.statistic.DefaultAssayDigester;
import org.obiba.genobyte.statistic.DefaultAssayStatsRunDefinition;
import org.obiba.genobyte.statistic.DefaultFrequencyStatsDigester;
import org.obiba.genobyte.statistic.DefaultSampleStatsRunDefinition;
import org.obiba.genobyte.statistic.StatsPool;


/**
 * Store to hold genotypes for any given assay and sample. A <tt>GenotypingStore</tt> is composed of two <tt>BitwiseStore</tt> objects,
 * one for the assays and one for the samples. This allows for stats to be computed efficiently per assay and per sample.
 * To keep both tables up-to-date, transpositions are constantly made between the two tables as the data is modified.
 * <p/>
 * In the <b>sample</b> store, each record is a sample, and there are two fields per existing assay: a genotype and a score.
 * In the <b>assay</b> store, each record is an assay, and there are the same two fields per existing sample: a genotype and a score.
 * @param <AK> Assay Key Type
 * @param <AT> Assay Type
 * @param <SK> Sample Key Type
 * @param <ST> Sample Type
 */
abstract public class GenotypingStore<AK, AT, SK, ST> {

  /** The store that contains the genotype data, organized by sample. */
  protected GenotypingRecordStore<SK, ST, AK> samples_ = null;
  /** The store that contains the genotype data, organized by assay. */
  protected GenotypingRecordStore<AK, AT, SK> assays_ = null;

  protected GenotypingStore(GenotypingRecordStore<SK, ST, AK> samples, GenotypingRecordStore<AK, AT, SK> assays) {
    super();
    samples_ = samples;
    assays_ = assays;
    samples_.setTransposedStore(assays);
    assays_.setTransposedStore(samples);

    for (DefaultGenotypingField field : DefaultGenotypingField.defaultSampleFields()) {
      samples_.registerGenotypingField(field);
    }

    for (DefaultGenotypingField field : DefaultGenotypingField.defaultAssayFields()) {
      assays_.registerGenotypingField(field);
    }
    
    //Prepare the statistics digester for both stores.
    StatsPool<AK,SK> assayStatsPool = new StatsPool<AK,SK>(assays_, new DefaultAssayStatsRunDefinition());
    StatsPool<SK,AK> sampleStatsPool = new StatsPool<SK,AK>(samples_, new DefaultSampleStatsRunDefinition());
    assays_.setStatsPool(assayStatsPool);
    samples_.setStatsPool(sampleStatsPool);
    assays_.setStatsDigester(new DefaultAssayDigester());
    samples_.setStatsDigester(new DefaultFrequencyStatsDigester());
  }

  public GenotypingRecordStore<AK, AT, SK> getAssayRecordStore() {
    return assays_;
  }

  public GenotypingRecordStore<SK, ST, AK> getSampleRecordStore() {
    return samples_;
  }

  public BitwiseRecordManager<AK, AT> getAssayManager() {
    return getAssayRecordStore().getRecordManager();
  }

  public BitwiseRecordManager<SK, ST> getSampleManager() {
    return getSampleRecordStore().getRecordManager();
  }

  public void empty() {
    getSampleRecordStore().empty();
    getAssayRecordStore().empty();
  }


  /**
   * Starts a transaction on the two bitwise stores <b>sample</b> and <b>assay</b>.
   * @see org.obiba.bitwise.BitwiseStore.startTransaction()
   */
  public void startTransaction() {
    getSampleStore().startTransaction();
    getAssayStore().startTransaction();
  }


  /**
   * Transfers <tt>BitwiseStore</tt> modifications to the persistant medium for both sample and assay stores.
   */
  public void flush() {
    getSampleStore().flush();
    getAssayStore().flush();
  }


  /**
   * Commits the ongoing transaction in the two bitwise stores <b>sample</b> and <b>assay</b>.
   * @see org.obiba.bitwise.BitwiseStore.commitTransaction()
   */
  public void commitTransaction() {
    getSampleStore().commitTransaction();
    getAssayStore().commitTransaction();
  }


  /**
   * Rollbacks ongoing transactions on the two bitwise stores <b>sample</b> and <b>assay</b>, and clear their transaction cache.
   * @see org.obiba.bitwise.BitwiseStore.endTransaction()
   */
  public void endTransaction() {
    getSampleStore().endTransaction();
    getAssayStore().endTransaction();
  }


  /**
   * Gets the number of samples in this store.
   * @see org.obiba.bitwise.BitwiseStore.getSize()
   * @return the total number of samples.
   */
  public int getSampleCount() {
    return getSampleStore().getSize();
  }


  /**
   * Gets the number of assays in this store.
   * @see org.obiba.bitwise.BitwiseStore.getSize()
   * @return the total number of assays.
   */
  public int getAssayCount() {
    return getAssayStore().getSize();
  }


  /**
   * Gets the current maximum number of samples that can be put in this store.
   * @see org.obiba.bitwise.BitwiseStore.getCapacity()
   * @return the maximum number of samples.
   */
  public int getSampleCapacity() {
    return getSampleStore().getCapacity();
  }


  /**
   * Guarantees that at least a certain number of samples can be put in this store.
   * @see org.obiba.bitwise.BitwiseStore.ensureCapacity()
   * @param capacity the minimum number of samples.
   */
  public void ensureSampleCapacity(int capacity) {
    getSampleStore().ensureCapacity(capacity);
  }


  /**
   * Guarantees that at least a certain number of samples can be put in this store.
   * @see org.obiba.bitwise.BitwiseStore.ensureCapacity()
   * @param capacity the minimum number of samples.
   */
  public void ensureAssayCapacity(int capacity) {
    getAssayStore().ensureCapacity(capacity);
  }


  /**
   * Computes a set of basic statistics on the genotypes of this <tt>GenotypingStore</tt>.
   * Basic statistics include alleles frequency, heterozygosity, Hardy-Weinberg Equilibrium, etc.
   * They do not include more specific concepts such as inconsistancies.
   * <p/>
   * Stats are first computed record per record in the <b>assay</b> bitwise store. They are then computed in the same way
   * for all records in the <b>sample</b> bitwise store. 
   */
  public void updateStats() {
    //First, compute statistics per assay.
    assays_.updateStats();

    //Then compute sample data
    samples_.updateStats();
  }


  /**
   * Closes the two <tt>BitwiseStore</tt> instances for assays and samples.i
   * @see org.obiba.bitwise.BitwiseStore.startTransaction()
   */
  public void close() {
    try {
      startTransaction();
      flush();
      commitTransaction();
    } finally {
      endTransaction();
      BitwiseStoreUtil.getInstance().close(getSampleStore());
      BitwiseStoreUtil.getInstance().close(getAssayStore());
    }
  }


  /**
   * Permanently deletes the sample and assay <tt>BitwiseStore</tt> instances.
   * @see org.obiba.bitwise.BitwiseStoreUtil.delete()
   */
  public void delete() {
    BitwiseStoreUtil.getInstance().delete(getSampleStore());
    BitwiseStoreUtil.getInstance().delete(getAssayStore());
  }


  public void reproDna() {
    reproDna(new DefaultReproducibilityErrorCountingStrategy<SK, AK>(DefaultGenotypingField.REPRO_DNA, DefaultGenotypingField.REPRO_DNA_TESTS, samples_, assays_));
  }


  public void reproDna(ReproducibilityErrorCountingStrategy<SK> strategy) {
    ReproducibilityErrorCalculator<SK> calculator = new ReproducibilityErrorCalculator<SK>(samples_);
    calculator.setComparableRecordProvider(samples_.getComparableRecordProvider());
    calculator.setCountingStrategy(strategy);
    calculator.calculate();
  }


  public void reproAssay() {
    reproAssay(new DefaultReproducibilityErrorCountingStrategy<AK, SK>(DefaultGenotypingField.REPRO_ASSAY, DefaultGenotypingField.REPRO_ASSAY_TESTS, assays_, samples_));
  }


  public void reproAssay(ReproducibilityErrorCountingStrategy<AK> strategy) {
    ReproducibilityErrorCalculator<AK> calculator = new ReproducibilityErrorCalculator<AK>(assays_);
    calculator.setComparableRecordProvider(assays_.getComparableRecordProvider());
    calculator.setCountingStrategy(strategy);
    calculator.calculate();
  }


  /**
   * Computes mendelian errors found in the genotypes. This is the general method to invoke when the
   * <tt>MendelianErrorCountingStrategy</tt> is not known.
   */
  public void mendel() {
    mendel(new DefaultMendelianErrorCountingStrategy<SK>(samples_, assays_));
  }


  public void mendel(MendelianErrorCountingStrategy<SK> strategy) {
    MendelianErrorCalculator<SK> calculator = new MendelianErrorCalculator<SK>(samples_);
    calculator.setCountingStrategy(strategy);
    calculator.setRecordProvider(samples_.getMendelianRecordTrioProvider());
    calculator.calculate();
  }


  public void reverseCalls() {
    ReversableCallExecutor<AK> executor = new ReversableCallExecutor<AK>(assays_);
    executor.reverse();
  }


  /**
   * Gets the <tt>BitwiseStore</tt> instance used to store samples in this <tt>GenotypingStore</tt>.
   * @return the <tt>BitwiseStore</tt>.
   */
  protected BitwiseStore getSampleStore() {
    return  samples_.getStore();
  }
  
  /**
   * Gets the <tt>BitwiseStore</tt> instance used to store assays in this <tt>GenotypingStore</tt>.
   * @return the <tt>BitwiseStore</tt>.
   */
  protected BitwiseStore getAssayStore() {
    return assays_.getStore();
  }

}
