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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.obiba.bitwise.AbstractField;
import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.Dictionary;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.MockField;
import org.obiba.bitwise.VolatileField;
import org.obiba.bitwise.dto.BitwiseStoreDto;
import org.obiba.bitwise.schema.FieldMetaData;
import org.obiba.bitwise.schema.StoreSchema;
import org.obiba.bitwise.schema.defaultDict.DefaultDictionaryFactory;
import org.obiba.genobyte.model.SnpCall;

public class MockBitwiseStore extends BitwiseStore {
  public static final int DEFAULT_SIZE = 3;
  private DefaultDictionaryFactory ddf_ = new DefaultDictionaryFactory();
  Map<String,VolatileField> dummyStore_ = new HashMap<String,VolatileField>();

  public MockBitwiseStore(String name) {
    this(name, DEFAULT_SIZE);
  }

  public MockBitwiseStore(String name, int size) {
    super(new BitwiseStoreDto(name, size));
    getDto().setDeleted(new BitVector(size).setAll());
    getDto().setCleared(new BitVector(size).setAll());
    createDummyStore();
  }

  public Properties getConfigurationProperties() {
    return new Properties();
  }

  @Override
  public void ensureCapacity(int capacity) {
    super.ensureCapacity(capacity);
    for (VolatileField f : dummyStore_.values()) {
      f.grow(capacity);
    }
  }
  
  public void setSize(int size) {
    this.ensureCapacity(size);
    Dictionary<Integer> integerDict = ddf_.getInstance(Integer.class, "integerdict");
    VolatileField f = dummyStore_.get("id");
    for (int i=0; i < getCapacity(); i++) {
      f.setValue(i, integerDict.lookup(i));
      getDto().getDeleted().clear(i);
    }
  }

  private void createDummyStore() {
    Dictionary<Integer> integerDict = ddf_.getInstance(Integer.class, "integerdict");
    Dictionary<SnpCall> callDict = ddf_.getInstance(SnpCall.class, "callDict");
    
    // Filling dummy store with bogus records
    dummyStore_.put("id", new VolatileField("id", this, integerDict));
    VolatileField f = dummyStore_.get("id");
    for (int i=0; i < getCapacity(); i++) {
      f.setValue(i, integerDict.lookup(i));
      getDto().getDeleted().clear(i);
    }
  }

  public void setCall(int pTransposedIndex, int pIndex, SnpCall pValue) {
    AbstractField f = dummyStore_.get("calls_" + pTransposedIndex);
    if(f == null) {
      f = createField("calls_" + pTransposedIndex);
    }
    f.setValue(pIndex, f.getDictionary().lookup(pValue));
  }

  @Override
  public Field createField(String name) {
    if(name.startsWith("calls")) {
      Dictionary<SnpCall> callDict = ddf_.getInstance(SnpCall.class, "callDict");
      dummyStore_.put(name, new VolatileField(name, this, callDict));
      return getField(name);
    }
    return null;
  }
  
  /**
   * Allows to create any kind of <tt>Field</tt> by providing a data type.
   * @param name the name of the field to be created
   * @param dataType the data type, to create the appropriate <tt>Dictionary</tt>
   * @return the newly created <tt>Field</tt>
   */
  public Field createField(String name, Class<?> dataType) {
    Dictionary dict = ddf_.getInstance(dataType, name + "Dict");
    dummyStore_.put(name, new VolatileField(name, this, dict));
    return getField(name);
  }

  public Field getField(String pFieldName) {
    VolatileField f = dummyStore_.get(pFieldName);
    if(f == null) return null;
    Field mf = new MockField(this, f);
    return mf;
  }
  
  @Override
  public StoreSchema getSchema() {
    StoreSchema ss = new StoreSchema();
    for(VolatileField vf : dummyStore_.values()) {
      FieldMetaData fmd = new FieldMetaData();
      fmd.setName(vf.getName());
      fmd.setDictionary(vf.getDictionary().getName());
      fmd.setTemplate(false);
      if(fmd.getName().startsWith("calls")) fmd.setTemplate(true);
    }
    return ss;
  }
  
  @Override
  public void startTransaction() {
  }
  
  @Override
  public void endTransaction() {
  }

  @Override
  public void commitTransaction() {
  }
}
