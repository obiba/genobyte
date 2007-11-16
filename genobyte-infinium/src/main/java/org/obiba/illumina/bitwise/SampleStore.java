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
package org.obiba.illumina.bitwise;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.obiba.bitwise.BitwiseRecordManager;
import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.annotation.AnnotationBasedRecord;
import org.obiba.bitwise.util.StringKeyCache;
import org.obiba.genobyte.GenotypingRecordStore;
import org.obiba.genobyte.ReversableCallProvider;
import org.obiba.genobyte.inconsistency.ComparableRecordProvider;
import org.obiba.genobyte.inconsistency.MendelianRecordTrioProvider;
import org.obiba.genobyte.inconsistency.util.IndividualIdFieldTrioProvider;
import org.obiba.genobyte.inconsistency.util.ReferenceIdFieldComparableRecordProvider;
import org.obiba.genobyte.model.SnpCall;
import org.obiba.genobyte.model.SnpGenotype;
import org.obiba.illumina.bitwise.model.Sample;
import org.obiba.illumina.io.LocusXDnaReportFile;
import org.obiba.illumina.io.LocusXDnaReportSampleDataEntry;
import org.obiba.illumina.io.SampleSheetFile;
import org.obiba.illumina.io.SampleSheetFileEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SampleStore extends GenotypingRecordStore<String, Sample, Integer> {

  private final Logger log = LoggerFactory.getLogger(SampleStore.class);

  public SampleStore(BitwiseStore store) {
    super(store);
  }

  @Override
  public ComparableRecordProvider getComparableRecordProvider() {
    return new ReferenceIdFieldComparableRecordProvider(this, "id", "reference");
  }

  @Override
  public MendelianRecordTrioProvider getMendelianRecordTrioProvider() {
    return new IndividualIdFieldTrioProvider(this, "id", "parent1", "parent2");
  }

  @Override
  public BitwiseRecordManager<String, Sample> createRecordManager(BitwiseStore store) {
    log.debug("Creating BitwiseRecordManager for store {}", store);
    BitwiseRecordManager<String, Sample> manager = AnnotationBasedRecord.createInstance(store, Sample.class);
    return new KeyCache(manager);
  }

  @Override
  public ReversableCallProvider getReversableCallProvider() {
    return null;
  }

  public void loadGenotypes(PrintStream ps, LocusXDnaReportFile lxd) throws IOException {
    int total  = lxd.getNumberDna();

    int[] locusIds = lxd.getGtsLocusId();
    List<SnpGenotype<Integer>> genotypes = new ArrayList<SnpGenotype<Integer>>(locusIds.length);

    int index = 1;
    Iterator<LocusXDnaReportSampleDataEntry> entries = lxd.getEntries();
    while(entries.hasNext()) {
      ps.print("Processing sample " + (index++) + "/"+total + "\r");
      LocusXDnaReportSampleDataEntry entry = entries.next();
      String sampleId = entry.getInstituteLabel();
      String calls[] = entry.getCalls();
      for(int i = 0; i < calls.length; i++) {
        Integer assayId = locusIds[i];
        SnpCall call = SnpCall.valueOf(calls[i]);
        SnpGenotype<Integer> g = new SnpGenotype<Integer>();
        g.setTransposedKey(assayId);
        g.setValue(call);
        genotypes.add(g);
      }
      this.setTransposedValues("calls", sampleId, genotypes);
      genotypes.clear();
      this.updateStats(sampleId);
      if(index % 100 == 0) {
        getStore().flush();
        getTransposedStore().getStore().flush();
      }
    }
    ps.println("");
  }

  public void loadSamples(SampleSheetFile ss) {
    this.getStore().ensureCapacity(this.getStore().getSize() + ss.getSampleData().size());
    for(SampleSheetFileEntry entry : ss.getSampleData()) {
      getRecordManager().insert(fromEntry(entry));
    }
  }

  private Sample fromEntry(SampleSheetFileEntry e) {
    Sample s = new Sample();
    s.setId(e.getSampleId());
    s.setName(e.getSampleName());
    s.setGender(e.getGender() != null ? Sample.Gender.valueOf(e.getGender().toString()) : null);
    s.setGroup(e.getSampleGroup());
    s.setPlate(e.getSamplePlate());
    s.setWell(e.getSampleWell());
    s.setParent1(e.getParent1());
    s.setParent2(e.getParent2());
    s.setPath(e.getPath());
    s.setReference(e.getReference());
    s.setReplicates(e.getReplicates());
    s.setSentrixBarcodeA(e.getSentrixBarcodeA());
    s.setSentrixPositionA(e.getSentrixPositionA());
    return s;
  }

  private static class KeyCache extends StringKeyCache<Sample> {
    public KeyCache(BitwiseRecordManager<String, Sample> mgr) {
      super(mgr);
    }

    @Override
    public String getKey(Sample arg0) {
      return arg0.getId();
    }
  }

}
