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

import org.obiba.bitwise.BitwiseStore;
import org.obiba.genobyte.GenotypingStore;
import org.obiba.illumina.bitwise.model.Assay;
import org.obiba.illumina.bitwise.model.Sample;


public class InfiniumGenotypingStore extends GenotypingStore<Integer, Assay, String, Sample> {

  public InfiniumGenotypingStore(BitwiseStore samples, BitwiseStore assays) {
    super(new SampleStore(samples), new AssayStore(assays));
  }
  
  @Override
  public SampleStore getSampleRecordStore() {
    return (SampleStore)super.getSampleRecordStore();
  }
  
  @Override
  public AssayStore getAssayRecordStore() {
    return (AssayStore)super.getAssayRecordStore();
  }

}
