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
package org.obiba.bitwise.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.obiba.bitwise.BitwiseStoreUtil;
import org.obiba.bitwise.dao.DaoKey;
import org.obiba.bitwise.dao.KeyedDaoManager;
import org.obiba.bitwise.mock.MockBitwiseStore;
import org.obiba.bitwise.util.BdbEnvUtil;
import org.obiba.bitwise.util.BdbPropertiesProvider;
import org.obiba.bitwise.util.FileUtil;

import junit.framework.TestCase;

public class BaseBdbDaoTestCase extends TestCase {

  private static final List<MockBitwiseStore> stores_ = new ArrayList<MockBitwiseStore>();
  
  public BaseBdbDaoTestCase() {
    super();
  }

  public BaseBdbDaoTestCase(String t) {
    super(t);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    BdbPropertiesProvider.setAsProvider("./src/test/java/test-bitwise.properties");
    try {
      FileUtil.deltree(BdbEnvUtil.getRoot());
    } catch (IOException e) {
    }
  }

  @Override
  protected void tearDown() throws Exception {
    Set<String> stores = BitwiseStoreUtil.getInstance().list();
    for (String store : stores) {
      BitwiseStoreUtil.getInstance().forceClose(store);
    }
    for(MockBitwiseStore store : stores_) {
      KeyedDaoManager.destroyInstance(store.getDaoKey());
    }
    try {
      FileUtil.deltree(BdbEnvUtil.getRoot());
    } catch (IOException e) {
    }
    super.tearDown();
  }

  protected MockBitwiseStore createMockStore(String name, int capacity) {
    Properties props = new Properties();
    props.setProperty(BdbEnvUtil.BDB_ROOT_PROPERTY, BdbEnvUtil.getRoot());
    MockBitwiseStore store = new MockBitwiseStore(name, capacity);
    stores_.add(store);
    KeyedDaoManager.createInstance(new DaoKey(name), props);
    return store;
  }

}
