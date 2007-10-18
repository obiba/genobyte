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
package ca.mcgill.genome.bitwise.simulation;

import java.util.HashSet;
import java.util.Set;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.BitwiseRecordManager;
import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.FieldValueIterator;
import org.obiba.bitwise.annotation.AnnotationBasedRecord;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.bitwise.util.BitVectorQueryResult;
import org.obiba.genobyte.GenotypingRecordStore;
import org.obiba.genobyte.ReversableCallProvider;
import org.obiba.genobyte.inconsistency.ComparableRecordProvider;
import org.obiba.genobyte.inconsistency.MendelianRecordTrioProvider;


public class SimulatedSampleGenotypingStore extends GenotypingRecordStore<Integer, SimulatedSample, Integer> {

  private BitwiseRecordManager<Integer, SimulatedSample> manager_ = null;
  private ComparableSampleProvider provider_ = null;

  public SimulatedSampleGenotypingStore(BitwiseStore store) {
    super(store);
    BitwiseRecordManager<Integer, SimulatedSample> sampleManager = AnnotationBasedRecord.createInstance(store, SimulatedSample.class);
    manager_ = new SimulatedSampleKeyCache(sampleManager);
  }

  @Override
  public ComparableRecordProvider getComparableRecordProvider() {
    if(provider_ == null) {
      provider_ = new ComparableSampleProvider();
    }
    return provider_;
  }

  @Override
  public BitwiseRecordManager<Integer, SimulatedSample> getRecordManager() {
    return manager_;
  }
  
  @Override
  public MendelianRecordTrioProvider getMendelianRecordTrioProvider() {
    return new TrioProvider();
  }
  
  @Override
  public ReversableCallProvider getReversableCallProvider() {
    return null;
  }

  private class TrioProvider implements MendelianRecordTrioProvider {
    private Field individualNameField_ = null;
    private Field motherNameField_ = null;
    private Field fatherNameField_ = null;
    private QueryResult children_ = null;

    private TrioProvider() {
      individualNameField_ = store_.getField("individualName");
      motherNameField_ = store_.getField("motherName");
      fatherNameField_ = store_.getField("fatherName");
    }
    
    public QueryResult getChildRecords() {
      if(children_ == null) {
        findChildren();
      }
      return children_;
    }

    public QueryResult getFatherRecords(int childRecord) {
      // Find all records that have the individual name == this record's mother name
      BitVector parentName = motherNameField_.getValue(childRecord);
      return individualNameField_.query(parentName);
    }

    public QueryResult getMotherRecords(int childRecord) {
      // Find all records that have the individual name == this record's father name
      BitVector parentName = fatherNameField_.getValue(childRecord);
      return individualNameField_.query(parentName);
    }

    private void findChildren() {
      QueryResult hasMother = motherNameField_.query(motherNameField_.getDictionary().lookup(null)).not();
      QueryResult hasFather = fatherNameField_.query(fatherNameField_.getDictionary().lookup(null)).not();
      children_ = hasMother.or(hasFather);
    }
  }

  private class ComparableSampleProvider implements ComparableRecordProvider {
    private Field individualNameField_ = null;
    private QueryResult references_ = null;

    private ComparableSampleProvider() {
      individualNameField_ = store_.getField("individualName");
    }

    public QueryResult getComparableRecords(int referenceRecord) {
      // Find all records that have the same individual name and that is not a reference.
      BitVector individualName = individualNameField_.getValue(referenceRecord);
      return individualNameField_.query(individualName).andNot(references_);
    }

    public QueryResult getComparableReferenceRecords() {
      if(references_ == null) {
        findReferences();
      }
      return references_;
    }

    private void findReferences() {
      BitVector bv = new BitVector(individualNameField_.getSize());
      Set<String> names = new HashSet<String>(); 
      FieldValueIterator<String> fvi = new FieldValueIterator<String>(individualNameField_);
      while(fvi.hasNext()) {
        FieldValueIterator<String>.FieldValue fv = fvi.next();
        if(names.add(fv.getValue())) {
          // First time we add this name, its a potential reference (it may or may not have replicates)
          bv.set(fv.getIndex());
        }
      }
      references_ = new BitVectorQueryResult(bv);
    }
  }
}
