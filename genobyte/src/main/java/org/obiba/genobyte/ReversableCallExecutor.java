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
package org.obiba.genobyte;

import org.obiba.bitwise.BitwiseRecordManager;
import org.obiba.bitwise.Dictionary;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.FieldValueIterator;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.genobyte.model.DefaultGenotypingField;


public class ReversableCallExecutor<K> {

  GenotypingRecordStore<K, ?, ?> store_ = null;
  private ReversableCallProvider provider_ = null;

  public ReversableCallExecutor(GenotypingRecordStore<K, ?, ?> store) {
    store_ = store;
    provider_ = store.getReversableCallProvider();
  }

  public void reverse() {
    if(provider_ == null) return;
    BitwiseRecordManager<K, ?> manager = store_.getRecordManager();
    QueryResult reversable = provider_.getReversableRecords();
    for (int i = reversable.next(0); i != -1; i = reversable.next(i+1)) {
      K key = manager.getKey(i);
      Field calls = store_.getGenotypingField(DefaultGenotypingField.CALLS.toString(), key);
      if(calls == null) {
        continue;
      }

      Dictionary<Object> dict = calls.getDictionary();
      Field fwdCalls = store_.getGenotypingField(DefaultGenotypingField.COMPARABLE_CALLS.toString(), key, true);
      if(fwdCalls == null) {
        throw new IllegalStateException("Cannot create genotyping field ["+DefaultGenotypingField.COMPARABLE_CALLS+"] in store ["+store_.getStore().getName()+"]");
      }
      FieldValueIterator fvi = new FieldValueIterator(calls);
      while(fvi.hasNext()) {
        FieldValueIterator.FieldValue fv = fvi.next();
        Object call = fv.getValue();
        Object reversed = provider_.reverseCall(call);
        if(reversed != null) {
          fwdCalls.setValue(fv.getIndex(), dict.lookup(reversed));
        }
      }
    }
  }

}
