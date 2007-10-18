/*******************************************************************************
 * Copyright 2007(c) G�nome Qu�bec. All rights reserved.
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
package org.obiba.genobyte.statistic;

import org.obiba.bitwise.BitwiseStore;
import org.obiba.genobyte.GenotypingRecordStore;
import org.obiba.genobyte.GenotypingStore;


public class MockGenotypingStore extends GenotypingStore {

  public MockGenotypingStore() {
    super(new MockGenotypingRecordStore(new MockBitwiseStore("samples")), new MockGenotypingRecordStore(new MockBitwiseStore("assays")));
  }

  public MockGenotypingStore(BitwiseStore samples, BitwiseStore assays) {
    super(new MockGenotypingRecordStore(samples), new MockGenotypingRecordStore(assays));
  }

  public MockGenotypingStore(GenotypingRecordStore samples, GenotypingRecordStore assays) {
    super(samples, assays);
  }

}
