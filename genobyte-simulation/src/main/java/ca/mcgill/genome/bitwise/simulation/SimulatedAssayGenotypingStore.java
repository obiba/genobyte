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

import org.obiba.bitwise.BitwiseRecordManager;
import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.annotation.AnnotationBasedRecord;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.genobyte.GenotypingRecordStore;
import org.obiba.genobyte.ReversableCallProvider;
import org.obiba.genobyte.inconsistency.ComparableRecordProvider;
import org.obiba.genobyte.inconsistency.MendelianRecordTrioProvider;
import org.obiba.genobyte.model.Orientation;
import org.obiba.genobyte.model.SnpCall;


public class SimulatedAssayGenotypingStore extends GenotypingRecordStore<Integer, SimulatedAssay, Integer> {

  private BitwiseRecordManager<Integer, SimulatedAssay> manager_ = null;

  public SimulatedAssayGenotypingStore(BitwiseStore store) {
    super(store);
    BitwiseRecordManager<Integer, SimulatedAssay> m = AnnotationBasedRecord.createInstance(store, SimulatedAssay.class);
    manager_ = new SimulatedAssayKeyCache(m);
  }

  @Override
  public ComparableRecordProvider getComparableRecordProvider() {
    return null;
  }

  @Override
  public BitwiseRecordManager<Integer, SimulatedAssay> getRecordManager() {
    return manager_;
  }

  @Override
  public MendelianRecordTrioProvider getMendelianRecordTrioProvider() {
    return null;
  }
  
  @Override
  public ReversableCallProvider getReversableCallProvider() {
    return new ReverseProvider();
  }
  
  private class ReverseProvider implements ReversableCallProvider {
    public QueryResult getReversableRecords() {
      Field field = getStore().getField("orientation");
      return field.query(field.getDictionary().lookup(Orientation.REVERSE));
    }
    
    public Object reverseCall(Object call) {
      SnpCall snpCall = (SnpCall)call;
      if(snpCall != null) {
        switch(snpCall) {
          case A:
            return SnpCall.B;
          case B:
            return SnpCall.A;
        }
      }
      return null;
    }
  }

}
