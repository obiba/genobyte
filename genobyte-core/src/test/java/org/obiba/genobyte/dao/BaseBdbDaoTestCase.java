/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.obiba.genobyte.dao;

import junit.framework.TestCase;
import org.obiba.bitwise.BitwiseStoreUtil;
import org.obiba.bitwise.dao.DaoKey;
import org.obiba.bitwise.dao.KeyedDaoManager;
import org.obiba.bitwise.util.BitwiseDiskUtil;
import org.obiba.bitwise.util.BitwiseStoreTestingHelper;
import org.obiba.bitwise.util.DefaultConfigurationPropertiesProvider;
import org.obiba.bitwise.util.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BaseBdbDaoTestCase extends TestCase {

  private static final List<BitwiseStoreTestingHelper> stores_ = new ArrayList<BitwiseStoreTestingHelper>();

  public BaseBdbDaoTestCase() {
    super();
  }

  public BaseBdbDaoTestCase(String t) {
    super(t);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    DefaultConfigurationPropertiesProvider.setAsProvider("./src/test/java/test-bitwise.properties");
    try {
      FileUtil.deltree(BitwiseDiskUtil.getRoot());
    } catch(IOException e) {
    }
  }

  @Override
  protected void tearDown() throws Exception {
    Set<String> stores = BitwiseStoreUtil.getInstance().list();
    for(String store : stores) {
      BitwiseStoreUtil.getInstance().forceClose(store);
    }
    for(BitwiseStoreTestingHelper store : stores_) {
      KeyedDaoManager.destroyInstance(store.getDaoKey());
    }
    try {
      FileUtil.deltree(BitwiseDiskUtil.getRoot());
    } catch(IOException e) {
    }
    super.tearDown();
  }

  protected BitwiseStoreTestingHelper createMockStore(String name, int capacity) {
    DefaultConfigurationPropertiesProvider provider = new DefaultConfigurationPropertiesProvider(
        "./src/test/java/test-bitwise.properties");
    BitwiseStoreTestingHelper store = new BitwiseStoreTestingHelper(name, capacity);
    stores_.add(store);
    KeyedDaoManager.createInstance(new DaoKey(name), provider.getDefaultProperties());
    return store;
  }

}
