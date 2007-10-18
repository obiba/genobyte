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
package org.obiba.bitwise;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.VolatileField;
import org.obiba.bitwise.dto.FieldDto;

public class MockField extends Field {
  
  private VolatileField field_;
  
  public MockField(BitwiseStore pBs, VolatileField pVf) {
    super(pBs, null, pVf.getDictionary(), null);
    field_ = pVf;

    FieldDto thisDto = new FieldDto();
    thisDto.setName(pVf.getName());
    thisDto.setDictionaryName(pVf.getDictionary().getName());
    thisDto.setSize(pVf.getSize());
    
    super.data_ = thisDto;
    
    // Share the bit vectors
    super.vectors_ = new BitVector[pVf.vectors_.length];
    for (int i = 0; i<pVf.vectors_.length; i++) {
      BitVector sourceVector = pVf.vectors_[i];
      if(sourceVector != null) this.vectors_[i] = sourceVector;
    }
  }
  
  @Override
  void setDirty(boolean d) {
    // Move any new bit vector into the volatile field
    // The reason we do this, is that the getField method in the MockBitwiseStore creates a new MockField on every call
    for (int i = 0; i< super.vectors_.length; i++) {
      BitVector sourceVector = super.vectors_[i];
      if(sourceVector != null) field_.vectors_[i] = sourceVector;
    }
  }
}
