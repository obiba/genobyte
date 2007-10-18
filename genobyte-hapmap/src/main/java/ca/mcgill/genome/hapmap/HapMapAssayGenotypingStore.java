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

import org.obiba.bitwise.BitwiseRecordManager;
import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.annotation.AnnotationBasedRecord;
import org.obiba.genobyte.GenotypingRecordStore;
import org.obiba.genobyte.ReversableCallProvider;
import org.obiba.genobyte.inconsistency.ComparableRecordProvider;
import org.obiba.genobyte.inconsistency.MendelianRecordTrioProvider;


public class HapMapAssayGenotypingStore extends GenotypingRecordStore<Integer, HapMapAssay, String> {

  private BitwiseRecordManager<Integer, HapMapAssay> manager_ = null;

  public HapMapAssayGenotypingStore(BitwiseStore store) {
    super(store);
    BitwiseRecordManager<Integer, HapMapAssay> manager = AnnotationBasedRecord.createInstance(store, HapMapAssay.class);
    manager_ = new HapMapAssayKeyCache(manager);
  }

  @Override
  public ComparableRecordProvider getComparableRecordProvider() {
    return null;
  }

  @Override
  public BitwiseRecordManager<Integer, HapMapAssay> getRecordManager() {
    return manager_;
  }

  @Override
  public MendelianRecordTrioProvider getMendelianRecordTrioProvider() {
    return null;
  }
  
  @Override
  public ReversableCallProvider getReversableCallProvider() {
    return null;
  }
}
