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
package org.obiba.genobyte.statistic;

import org.obiba.bitwise.BitwiseRecordManager;
import org.obiba.bitwise.BitwiseStore;
import org.obiba.genobyte.GenotypingField;
import org.obiba.genobyte.GenotypingRecordStore;
import org.obiba.genobyte.ReversableCallProvider;
import org.obiba.genobyte.inconsistency.ComparableRecordProvider;
import org.obiba.genobyte.inconsistency.MendelianRecordTrioProvider;
import org.obiba.genobyte.model.DefaultGenotypingField;


public class MockGenotypingRecordStore extends GenotypingRecordStore {
  
  BitwiseRecordManager brm_ = null;
  
  public MockGenotypingRecordStore(BitwiseStore bs) {
    super(bs);
    brm_ = new MockBitwiseRecordManager(bs);
    registerGenotypingField(DefaultGenotypingField.CALLS);
  }
  
  
  public void registerGenotypingField(GenotypingField field) {
    super.genotypingFields_.put(field.getName(), field);
  }


  @Override
  public BitwiseRecordManager getRecordManager() {
    // TODO Auto-generated method stub
    return brm_;
  }
  

  @Override
  public ComparableRecordProvider getComparableRecordProvider() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public MendelianRecordTrioProvider getMendelianRecordTrioProvider() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public ReversableCallProvider getReversableCallProvider() {
    // TODO Auto-generated method stub
    return null;
  }

}
