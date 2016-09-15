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
package org.obiba.bitwise.dao.impl.bdb;

import com.sleepycat.je.*;
import org.obiba.bitwise.dao.DaoKey;
import org.obiba.bitwise.dao.KeyedDaoManager;
import org.obiba.bitwise.dao.KeyedDaoManagerDestroyListener;
import org.obiba.bitwise.util.DefaultConfigurationPropertiesProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class BdbContext implements KeyedDaoManagerDestroyListener {

  static private final Logger log = LoggerFactory.getLogger(BdbContext.class);

  static private final String SEQUENCE_DATABASE = "seq.db";

  static private Map<DaoKey, BdbContext> instanceMap_ = new HashMap<DaoKey, BdbContext>();

  private DaoKey key_ = null;

  /** The Properties used to created the JE Environment*/
  private Properties envProps_ = null;

  /** The directory where the Environment points to */
  private File envDir_ = null;

  /** The JE Environment (one per store). Lazily instantiated: see {@link BdbContext#getEnvironment()} */
  private Environment env_ = null;

  private Map<String, Database> dbMap_ = new HashMap<String, Database>();

  private Map<String, SecondaryDatabase> secDbMap_ = new HashMap<String, SecondaryDatabase>();

  private Map<String, Sequence> seqMap_ = new HashMap<String, Sequence>();

  private boolean truncateOnClose_ = false;

  private BdbContext(DaoKey key) {
    super();
    key_ = key;
  }

  public void setTruncateOnClose(boolean truncate) {
    truncateOnClose_ = truncate;
  }

  static public void destroyInstance(DaoKey key) {
    BdbContext ctx = instanceMap_.remove(key);
    if(ctx != null) {
      ctx.close();
    }
  }

  static void createInstance(DaoKey key, Properties props) throws DatabaseException {
    BdbContext instance = new BdbContext(key);

    String root = null;
    Properties localProps = new Properties();
    for(Enumeration<?> e = props.propertyNames(); e.hasMoreElements(); ) {
      String propName = (String) e.nextElement();
      if(DefaultConfigurationPropertiesProvider.ROOT_DIR_PROPERTY.equals(propName)) {
        root = props.getProperty(propName);
      } else {
        // JE doesn't like having extra properties, so we need to remove them...
        if(propName.startsWith("je")) {
          localProps.setProperty(propName, props.getProperty(propName));
        }
      }
    }
    instance.envProps_ = localProps;

    if(root == null) {
      throw new IllegalStateException(
          "Configuration property [" + DefaultConfigurationPropertiesProvider.ROOT_DIR_PROPERTY + "] is missing.");
    }
    File rootDir = new File(root);
    if(rootDir.exists() == false) {
      if(rootDir.mkdirs() == false) {
        log.error("Cannot create bitwise root directory [{}].", rootDir);
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
    instance.envDir_ = envDir;

    KeyedDaoManager.addDestroyListener(key, instance);
    instanceMap_.put(key, instance);
  }

  static BdbContext getInstance(DaoKey key) {
    return instanceMap_.get(key);
  }

  @Override
  public String toString() {
    return "BdbContext{" + key_ + "}";
  }

  public void destroying() {
    destroyInstance(key_);
  }

  Environment getEnvironment() throws DatabaseException {
    if(env_ == null) {
      initEnvironment();
    }
    return env_;
  }

  synchronized void initEnvironment() throws DatabaseException {
    // Test to avoid race condition
    if(env_ == null) {
      log.debug("Creating BDB environment in [{}] with properties {}", envDir_, envProps_);

      EnvironmentConfig cfg = new EnvironmentConfig(envProps_);
      cfg.setAllowCreate(true);
      env_ = new Environment(envDir_, cfg);

      // Force a checkpoint: this helps cleaning log files.
      CheckpointConfig chkpt = new CheckpointConfig();
      chkpt.setForce(true);
      env_.checkpoint(chkpt);
    }
  }

  synchronized Database getDatabase(String name) throws DatabaseException {
    if(dbMap_.containsKey(name) == false) {
      DatabaseConfig dbCfg = new DatabaseConfig();
      dbCfg.setTransactional(getEnvironment().getConfig().getTransactional());
      dbCfg.setAllowCreate(true);
      dbCfg.setSortedDuplicates(false);
      dbMap_.put(name, getEnvironment().openDatabase(null, name, dbCfg));
    }
    return dbMap_.get(name);
  }

  synchronized SecondaryDatabase getSecondaryDatabase(String name, Database db, SecondaryKeyCreator creator)
      throws DatabaseException {
    if(secDbMap_.containsKey(name) == false) {
      SecondaryConfig dbCfg = new SecondaryConfig();
      dbCfg.setTransactional(getEnvironment().getConfig().getTransactional());
      dbCfg.setAllowCreate(true);
      dbCfg.setSortedDuplicates(false);
      dbCfg.setKeyCreator(creator);
      secDbMap_.put(name, getEnvironment().openSecondaryDatabase(null, name, db, dbCfg));
    }
    return secDbMap_.get(name);
  }

  synchronized Sequence getSequence(String name) {
    if(seqMap_.containsKey(name) == false) {
      Database seqDb = getDatabase(SEQUENCE_DATABASE);

      DatabaseEntry sequenceEntry;
      try {
        sequenceEntry = new DatabaseEntry(name.getBytes("UTF-8"));
      } catch(UnsupportedEncodingException e) {
        throw new RuntimeException("Couldn't create DatabaseEntry", e);
      }
      SequenceConfig seqCfg = new SequenceConfig();
      seqCfg.setAutoCommitNoSync(true);
      seqCfg.setAllowCreate(true);
      seqCfg.setCacheSize(50);
      seqCfg.setInitialValue(1);
      seqMap_.put(name, seqDb.openSequence(null, sequenceEntry, seqCfg));
    }
    return seqMap_.get(name);
  }

  synchronized public void close() {
    try {
      if(env_ != null) env_.sync();

      for(Sequence sequence : seqMap_.values()) {
        sequence.close();
      }
      seqMap_.clear();
      for(Database db : secDbMap_.values()) {
        db.close();
      }
      secDbMap_.clear();
      for(Database db : dbMap_.values()) {
        db.close();
      }
      dbMap_.clear();
      if(env_ != null) {
        if(truncateOnClose_) {
          truncate();
        } else {
          env_.cleanLog();
        }
        env_.close();
        env_ = null;
      }
    } catch(DatabaseException e) {
      throw new RuntimeException(e);
    }
  }

  private void truncate() {
    try {
      List<String> dbNames_ = getEnvironment().getDatabaseNames();
      for(String dbName : dbNames_) {
        getEnvironment().removeDatabase(null, dbName);
      }
    } catch(DatabaseException e) {
      throw new RuntimeException(e);
    }
  }

  static public void dumpStats() {
    dumpStats(false);
  }

  static public void dumpStats(boolean clear) {
    StatsConfig cfg = new StatsConfig();
    cfg.setClear(clear);
    for(BdbContext ctx : instanceMap_.values()) {
      EnvironmentStats o;
      try {
        System.err.println("--- Stats for [" + ctx.getEnvironment().getHome().getName() + "] ---");
        o = ctx.env_.getStats(cfg);
        System.err.println(o);
      } catch(DatabaseException e) {
        System.err.println(e);
      }
      System.err.println("-------------------------------------------------");
    }
  }

}
