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

import java.util.Set;
import java.util.TreeSet;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.BitwiseRecordManager;
import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.FieldValueIterator;
import org.obiba.bitwise.annotation.AnnotationBasedRecord;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.bitwise.util.BitVectorQueryResult;
import org.obiba.bitwise.util.IntegerKeyCache;
import org.obiba.genobyte.GenotypingRecordStore;
import org.obiba.genobyte.ReversableCallProvider;
import org.obiba.genobyte.inconsistency.ComparableRecordProvider;
import org.obiba.genobyte.inconsistency.MendelianRecordTrioProvider;
import org.obiba.genobyte.model.Chromosome;
import org.obiba.genobyte.model.SnpAllele;
import org.obiba.illumina.bitwise.model.Assay;
import org.obiba.illumina.io.ManifestFile;
import org.obiba.illumina.io.ManifestFileAssayEntry;


public class AssayStore extends GenotypingRecordStore<Integer, Assay, String> {

  public AssayStore(BitwiseStore store) {
    super(store);
  }

  @Override
  public ComparableRecordProvider getComparableRecordProvider() {
    return new AssayComparator();
  }

  @Override
  public MendelianRecordTrioProvider getMendelianRecordTrioProvider() {
    return null;
  }

  @Override
  public BitwiseRecordManager<Integer, Assay> createRecordManager(BitwiseStore store) {
    BitwiseRecordManager<Integer, Assay> manager = AnnotationBasedRecord.createInstance(store, Assay.class);
    return new KeyCache(manager);
  }

  @Override
  public ReversableCallProvider getReversableCallProvider() {
    return null;
  }

  public void loadAssays(ManifestFile manifest) {
    this.getStore().ensureCapacity(this.getStore().getSize() + manifest.getSnpCount());
    for(ManifestFileAssayEntry entry : manifest.getManifestEntries()) {
      getRecordManager().insert(fromEntry(entry));
    }
  }

  private Assay fromEntry(ManifestFileAssayEntry entry) {
    Assay a = new Assay();
    a.setLocusId(entry.getIndex());
    a.setIlmnId(entry.getIlmnId());
    a.setSnpName(entry.getSnpName());
    a.setIlmnStrand(Assay.Strand.valueOf(entry.getIlmnStrand().toUpperCase()));
    a.setAlleleA(SnpAllele.valueOf(entry.getSnp().substring(1,2)));
    a.setAlleleB(SnpAllele.valueOf(entry.getSnp().substring(3,4)));
    a.setAddressAid(entry.getAddressAid());
    a.setAlleleAProbeSeq(entry.getAlleleAProbeSeq());
    a.setAddressBid(entry.getAddressBid());
    a.setAlleleBProbeSeq(entry.getAlleleBProbeSeq());
    a.setChromosome(Chromosome.valueOf("chr"+entry.getChromosome().toUpperCase()));
    a.setMapInfo(Integer.parseInt(entry.getMapInfo()));
    a.setPloidy(entry.getPloidy());
    a.setSpecies(entry.getSpecies());
    String s = entry.getCustomerStrand();
    if(s != null && s.length() > 0) {
      a.setCustomerStrand(Assay.Strand.valueOf(s.toUpperCase()));
    }
    a.setIllumiCodeSeq(entry.getIllumiCodeSeq());
    a.setTopGenomicSeq(entry.getTopGenomicSeq());
    return a;
  }
  
  private static class KeyCache extends IntegerKeyCache<Assay> {
    public KeyCache(BitwiseRecordManager<Integer, Assay> a) {
      super(a);
    }

    @Override
    public Integer getKey(Assay a) {
      return a.getLocusId();
    }
  }

  private class AssayComparator implements ComparableRecordProvider {

    QueryResult references = null;
    QueryResult[] replicates = null;

    private AssayComparator() {
      // Pre-build vectors so we don't have to query the snpName field to many times.
      BitVector refs = new BitVector(getStore().getCapacity());
      replicates = new QueryResult[refs.size()];

      Set<String> allNames = new TreeSet<String>();
      Field snpName = getStore().getField("snpName");
      FieldValueIterator<String> names = new FieldValueIterator<String>(snpName);
      while(names.hasNext()) {
        FieldValueIterator<String>.FieldValue fv = names.next();
        if(allNames.add(fv.getValue()) == false) {
          QueryResult thisName = snpName.query(fv.getBitValue());
          int reference = thisName.next(0);
          refs.set(reference);
          replicates[reference] = thisName;
        }
      }
      references = new BitVectorQueryResult(refs);
      for (QueryResult replicate : this.replicates) {
        if(replicate != null) replicate.andNot(references);
      }
    }

    public QueryResult getComparableRecords(int reference) {
      return replicates[reference];
    }

    public QueryResult getComparableReferenceRecords() {
      return references;
    }
  }
}
