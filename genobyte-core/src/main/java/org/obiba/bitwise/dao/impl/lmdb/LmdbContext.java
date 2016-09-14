/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.bitwise.dao.impl.lmdb;

import org.fusesource.lmdbjni.Database;
import org.fusesource.lmdbjni.Env;
import org.fusesource.lmdbjni.Transaction;
import org.obiba.bitwise.dao.DaoKey;
import org.obiba.bitwise.dao.KeyedDaoManager;
import org.obiba.bitwise.dao.KeyedDaoManagerDestroyListener;
import org.obiba.bitwise.dao.impl.jdbm.JdbmContext;
import org.obiba.bitwise.util.DefaultConfigurationPropertiesProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LmdbContext implements KeyedDaoManagerDestroyListener {

  static final Logger log = LoggerFactory.getLogger(LmdbContext.class);

  static private Map<DaoKey, LmdbContext> instanceMap_ = new HashMap<>();

  private DaoKey key_ = null;

  private DatabaseProvider provider;

  private LmdbContext(DaoKey key) {
    super();
    key_ = key;
  }

  static void createInstance(DaoKey key, Properties props) {
    LmdbContext instance = new LmdbContext(key);

    String root = props.getProperty(DefaultConfigurationPropertiesProvider.ROOT_DIR_PROPERTY);
    File rootDir = new File(root);
    if(rootDir.exists() == false) {
      if(rootDir.mkdirs() == false) {
        log.error("Cannot create root directory [{}].", rootDir);
        throw new RuntimeException("Cannot mkdir [" + rootDir + "]");
      }
    }
    File envDir = new File(root, key.toString());
    if(envDir.exists() == false) {
      if(envDir.mkdirs() == false) {
        log.error("Cannot create environment directory [{}]", envDir);
        throw new RuntimeException("Cannot mkdir [" + envDir + "]");
      }
    }
    log.debug("Creating environment in [{}] with properties {}", envDir, props);
    instance.provider = new DatabaseProvider(envDir, props);
    KeyedDaoManager.addDestroyListener(key, instance);
    instanceMap_.put(key, instance);
  }

  static LmdbContext getInstance(DaoKey key) {
    return instanceMap_.get(key);
  }

  static public void destroyInstance(DaoKey key) {
    LmdbContext ctx = instanceMap_.remove(key);
    if(ctx != null) {
      ctx.close();
    }
  }

  public Transaction createTransaction() {
    return provider.createTransaction();
  }

  synchronized public void close() {
    // TODO
  }

  @Override
  public String toString() {
    return "JdbmContext{" + key_ + "}";
  }

  @Override
  public void destroying() {
    destroyInstance(key_);
  }

  private static class DatabaseProvider {

    private Env env;

    public DatabaseProvider(File envDir, Properties props) {
      env = new Env(envDir.getAbsolutePath());
    }

    Database createDatabase() {
      return env.openDatabase();
    }

    Transaction createTransaction() {
      return env.createReadTransaction();
    }
  }
}
