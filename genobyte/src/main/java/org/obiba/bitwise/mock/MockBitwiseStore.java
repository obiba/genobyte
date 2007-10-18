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
package org.obiba.bitwise.mock;

import java.util.HashMap;
import java.util.Map;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.Dictionary;
import org.obiba.bitwise.dao.DaoKey;
import org.obiba.bitwise.dto.BitwiseStoreDto;
import org.obiba.bitwise.schema.FieldMetaData;
import org.obiba.bitwise.schema.StoreSchema;



public class MockBitwiseStore extends BitwiseStore {

//  private FieldUtil fieldUtil_ = null;
//  private DictionaryUtil dictUtil_ = null;
  private StoreSchema mockSchema_ = new StoreSchema();

  private Map<String, Dictionary> dictionaries_ = new HashMap<String, Dictionary>();
//  private Map<String, String> fieldDict_ = new HashMap<String, String>();

  public MockBitwiseStore(String name, int capacity) {
    super(new BitwiseStoreDto(name, capacity));
//    fieldUtil_ = new FieldUtil(this);
//    dictUtil_ = new DictionaryUtil(this);
    getDto().setSchema(mockSchema_);
    getDto().setDeleted(new BitVector(capacity).setAll());
    getDto().setCleared(new BitVector(capacity).setAll());
  }

  public MockBitwiseStore() {
    this("MOCK_STORE", 10000);
  }

  public void setFieldDict(String name, String d) {
    FieldMetaData fmd = new FieldMetaData();
    fmd.setName(name);
    fmd.setDictionary(d);
    mockSchema_.addField(fmd);
  }

  @Override
  public Dictionary getDictionary(String name) {
    return dictionaries_.get(name);
  }
  
  public void addDictionary(Dictionary d) {
    dictionaries_.put(d.getName(), d);
  }

/*  public Dictionary createDictionary(String name, String type, String clazz, List<Property> props) {
    Dictionary d = dictUtil_.createDictionary(name, type, clazz, props);
    addDictionary(d);
    return d;
  }
  
  public Dictionary openDictionary(String name) {
    Dictionary d = dictUtil_.openDictionary(name);
    addDictionary(d);
    return d;
  }
*/
  @Override
  public DaoKey getDaoKey() {
    return super.getDaoKey();
  }

}
