/*******************************************************************************
 * Copyright 2007(c) Genome Quebec. All rights reserved.
 * <p>
 * This file is part of GenoByte.
 * <p>
 * GenoByte is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * <p>
 * GenoByte is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.obiba.bitwise.dao.impl.jdbm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.obiba.bitwise.dao.DaoKey;
import org.obiba.bitwise.dao.KeyedDaoManager;
import org.obiba.bitwise.dao.KeyedDaoManagerDestroyListener;
import org.obiba.bitwise.util.DefaultConfigurationPropertiesProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jdbm.RecordManager;
import jdbm.RecordManagerOptions;
import jdbm.recman.Provider;

public class JdbmContext implements KeyedDaoManagerDestroyListener {

  static final Logger log = LoggerFactory.getLogger(JdbmContext.class);

  static private Map<DaoKey, JdbmContext> instanceMap_ = new HashMap<DaoKey, JdbmContext>();

  private DaoKey key_ = null;

  private BitwiseRecordManagerProvider provider;

  private Map<String, RecordManager> managers = new HashMap<String, RecordManager>();

  private JdbmContext(DaoKey key) {
    super();
    key_ = key;
  }

  static public void destroyInstance(DaoKey key) {
    JdbmContext ctx = instanceMap_.remove(key);
    if(ctx != null) {
      ctx.close();
    }
  }

  static void createInstance(DaoKey key, Properties props) {
    JdbmContext instance = new JdbmContext(key);

    String root = props.getProperty(DefaultConfigurationPropertiesProvider.ROOT_DIR_PROPERTY);
    props.setProperty(RecordManagerOptions.DISABLE_TRANSACTIONS, "true");
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
    instance.provider = new BitwiseRecordManagerProvider(envDir, props);
    KeyedDaoManager.addDestroyListener(key, instance);
    instanceMap_.put(key, instance);
  }

  static JdbmContext getInstance(DaoKey key) {
    return instanceMap_.get(key);
  }

  boolean managerExists(String name) {
    return this.provider.managerExists(name);
  }

  RecordManager getManager(String name) {
    RecordManager m = managers.get(name);
    if(m == null) {
      synchronized(managers) {
        // Test again due to possible race condition...
        m = managers.get(name);
        if(m == null) {
          try {
            m = provider.createRecordManager(name);
          } catch(IOException e) {
            throw new JdbmRuntimeException(e);
          }
          managers.put(name, m);
        }
      }
    }
    return m;
  }

  @Override
  public String toString() {
    return "JdbmContext{" + key_ + "}";
  }

  public void destroying() {
    destroyInstance(key_);
  }

  synchronized void commit() {
    for(RecordManager m : managers.values()) {
      try {
        m.commit();
      } catch(IOException e) {
        log.error("Error commiting manager [{}]: {}", m, e.getMessage());
      }
    }
  }

  synchronized void rollback() {
    for(RecordManager m : managers.values()) {
      try {
        m.rollback();
      } catch(IOException e) {
        log.error("Error commiting manager [{}]: {}", m, e.getMessage());
      }
    }
  }

  synchronized public void close() {
    for(RecordManager m : managers.values()) {
      try {
        m.close();
      } catch(IOException e) {
        log.error("Error closing manager [{}]: {}", m, e.getMessage());
      }
    }
  }

  private static class BitwiseRecordManagerProvider {
    Provider p = new Provider();

    Properties props;

    File root;

    BitwiseRecordManagerProvider(File root, Properties props) {
      this.root = root;
      this.props = props;
    }

    public RecordManager createRecordManager(String name) throws IOException {
      File r = new File(root, name);
      return p.createRecordManager(r.getAbsolutePath(), props);
    }

    public boolean managerExists(String name) {
      File test = new File(root, name + ".db");
      return test.exists();
    }
  }
}
