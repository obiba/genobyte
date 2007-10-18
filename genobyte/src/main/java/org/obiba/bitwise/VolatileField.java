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

import org.obiba.bitwise.dto.FieldDto;


public class VolatileField extends AbstractField {

  //The BitVectors are not providable in this constructor, as the state of this field will not be persisted.
  //The holded values will always be initialized through method setValue().
  public VolatileField(String pName, BitwiseStore pStore, Dictionary pDict) {
    super();
    super.store_ = pStore;
    super.dictionary_ = pDict;
    super.vectors_ = new BitVector[pDict.dimension()];
    
    super.data_ = new FieldDto();
    super.data_.setName(pName);
    super.data_.setSize(pStore.getCapacity());
    super.data_.setBitIndex(new int[pDict.dimension()]);
    super.data_.setDictionaryName(pDict.getName());
  }

  
  /**
   * Compares this <code>Field</code> with the specified Object for equality.
   * @param o <code>Object</code> to which this Field is to be compared.
   * @return true if the name of both <code>Field</code> objects is the same.
   */
  public boolean equals(Object o) {
    if(o instanceof VolatileField == false) {
      return false;
    }
    VolatileField d = (VolatileField)o;
    return super.data_.getName().equals(d.data_.getName());
  }


  /**
   * Returns a hash code for this Field.
   * @return a hash code value for this object.
   */
  public int hashCode() {
    return super.data_.getName().hashCode();
  }

}
