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
package ca.mcgill.genome.hapmap;

import java.util.HashMap;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.BitwiseRecordManager;
import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.annotation.AnnotationBasedRecord;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.bitwise.util.BitVectorQueryResult;
import org.obiba.genobyte.GenotypingRecordStore;
import org.obiba.genobyte.ReversableCallProvider;
import org.obiba.genobyte.inconsistency.ComparableRecordProvider;
import org.obiba.genobyte.inconsistency.MendelianRecordTrioProvider;


public class HapMapSampleGenotypingStore extends GenotypingRecordStore<String, HapMapSample, Integer> {

  private BitwiseRecordManager<String, HapMapSample> manager_ = null;

  public HapMapSampleGenotypingStore(BitwiseStore store) {
    super(store);
    BitwiseRecordManager<String, HapMapSample> sampleManager = AnnotationBasedRecord.createInstance(store, HapMapSample.class);
    manager_ = new HapMapSampleKeyCache(sampleManager);
  }

  @Override
  public ComparableRecordProvider getComparableRecordProvider() {
    return null;
  }

  @Override
  public BitwiseRecordManager<String, HapMapSample> getRecordManager() {
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
    String[][] trios = 
    {{"NA12753","NA12763","NA12762"},
     {"NA12707","NA12717","NA12716"},
     {"NA10859","NA11882","NA11881"},
     {"NA10847","NA12239","NA12146"},
     {"NA10861","NA11995","NA11994"},
     {"NA10851","NA12057","NA12056"},
     {"NA10863","NA12234","NA12264"},
     {"NA10857","NA12044","NA12043"},
     {"NA07029","NA07000","NA06994"},
     {"NA07048","NA07055","NA07034"},
     {"NA10854","NA11840","NA11839"},
     {"NA10846","NA12145","NA12144"},
     {"NA07019","NA07056","NA07022"},
     {"NA10831","NA12156","NA12155"},
     {"NA12801","NA12813","NA12812"},
     {"NA12878","NA12892","NA12891"},
     {"NA10856","NA11830","NA11829"},
     {"NA06991","NA06985","NA06993"},
     {"NA12802","NA12815","NA12814"},
     {"NA12865","NA12875","NA12874"},
     {"NA10838","NA12004","NA12003"},
     {"NA10830","NA12236","NA12154"},
     {"NA10855","NA11832","NA11831"},
     {"NA10860","NA11993","NA11992"},
     {"NA07348","NA07345","NA07357"},
     {"NA10835","NA12249","NA12248"},
     {"NA12864","NA12873","NA12872"},
     {"NA10839","NA12006","NA12005"},
     {"NA12740","NA12751","NA12750"},
     {"NA12752","NA12761","NA12760"}};

    private QueryResult children_ = null;
    private java.util.Map<String, QueryResult> mothers_ = new HashMap<String, QueryResult>();
    private java.util.Map<String, QueryResult> fathers_ = new HashMap<String, QueryResult>();

    private TrioProvider() {
    }

    public QueryResult getChildRecords() {
      if(children_ == null) {
        findChildren();
      }
      return children_;
    }

    public QueryResult getFatherRecords(int childRecord) {
      String childName = manager_.getKey(childRecord);
      return fathers_.get(childName);
    }

    public QueryResult getMotherRecords(int childRecord) {
      String childName = manager_.getKey(childRecord);
      return mothers_.get(childName);
    }

    private void findChildren() {
      BitVector children = new BitVector(getStore().getCapacity());
      for (int i = 0; i < trios.length; i++) {
        BitVector motherVector = new BitVector(getStore().getCapacity());
        BitVector fatherVector = new BitVector(getStore().getCapacity());

        String[] trio = trios[i];
        String child = trio[0];
        String mother = trio[1];
        String father = trio[2];
        int index = manager_.getIndexFromKey(child);
        children.set(index);
        index = manager_.getIndexFromKey(mother);
        motherVector.set(index);
        index = manager_.getIndexFromKey(father);
        fatherVector.set(index);
        mothers_.put(child, new BitVectorQueryResult(motherVector));
        fathers_.put(child, new BitVectorQueryResult(fatherVector));
      }
      children_ = new BitVectorQueryResult(children);
    }
  }

}
