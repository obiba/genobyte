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
import org.obiba.bitwise.util.BitwiseDiskUtil;
import org.obiba.bitwise.util.DefaultConfigurationPropertiesProvider;
import org.obiba.bitwise.util.FileUtil;

import java.io.IOException;
import java.util.Set;

public class BaseBdbDaoTestCase extends TestCase {

  public BaseBdbDaoTestCase() {
    super();
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    DefaultConfigurationPropertiesProvider.setAsProvider("./src/test/java/test-bitwise.properties");
    try {
      FileUtil.deltree(BitwiseDiskUtil.getRoot());
    } catch (IOException e) {
    }
  }

  @Override
  protected void tearDown() throws Exception {
    Set<String> stores = BitwiseStoreUtil.getInstance().list();
    for (String store : stores) {
      BitwiseStoreUtil.getInstance().forceClose(store);
    }
    try {
      FileUtil.deltree(BitwiseDiskUtil.getRoot());
    } catch (IOException e) {
    }
    super.tearDown();
  }

}
