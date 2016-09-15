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
package org.obiba.bitwise.util;

import org.obiba.bitwise.BitwiseStoreUtil;
import org.obiba.bitwise.ConfigurationPropertiesProvider;

import java.io.*;
import java.util.Properties;

/**
 * Configuration provider for a BDB store that only defines the root of the store on disk. The store-specific properties file
 * is expected to be in each BDB store directory.
 */
public class DefaultConfigurationPropertiesProvider implements ConfigurationPropertiesProvider {

  /** The configuration key for the root directory of bitwise stores. */
  public static final String ROOT_DIR_PROPERTY = "bitwise.dir.root";

  /** The configuration key for the bitwise DAO implementation to use. */
  public static final String BITWISE_DAO_IMPL = "bitwise.dao.impl";

  private Properties defaultProperties_;

  public DefaultConfigurationPropertiesProvider() {
    loadBitwiseDefaultProperties();
    loadRuntimeProperties();
  }

  public DefaultConfigurationPropertiesProvider(String customProperties) {
    this();
    defaultProperties_ = loadPropertiesFile(defaultProperties_, customProperties, true);
  }

  public DefaultConfigurationPropertiesProvider(Properties customProperties) {
    this();
    // Override default properties with custom properties
    defaultProperties_.putAll(customProperties);
  }

  public Properties getDefaultProperties() {
    return defaultProperties_;
  }

  public Properties loadProperties(String storeName) {
    File storeDir = getStoreDir(storeName);
    File props = new File(storeDir, "bitwise.properties");
    return loadPropertiesFile(defaultProperties_, props, false);
  }

  public void saveSpecificProperties(String storeName, Properties prop) {
    File storeDir = getStoreDir(storeName);
    if(storeDir != null && storeDir.exists()) {
      File props = new File(storeDir, "bitwise.properties");
      OutputStream os = null;
      try {
        os = new FileOutputStream(props);
        prop.store(os, null);
      } catch(IOException e) {
        throw new RuntimeException(
            "An error occured while saving the bitwise properties for store " + storeName + ": " + e.getMessage(), e);
      } finally {
        try {
          if(os != null) os.close();
        } catch(IOException e) {
          // ignore
        }
      }
    }
  }

  private void loadBitwiseDefaultProperties() {
    InputStream is = DefaultConfigurationPropertiesProvider.class.getResourceAsStream("/bitwise.properties");
    if(is == null) {
      throw new RuntimeException("Factory Default Properties File cannot be found.");
    }
    defaultProperties_ = new Properties();
    loadPropertiesStream(defaultProperties_, is);
  }

  private void loadRuntimeProperties() {
    File runtimeProperties = new File("bitwise.properties");
    if(runtimeProperties.exists() == false) {
      return;
    }
    defaultProperties_ = loadPropertiesFile(defaultProperties_, runtimeProperties, false);
  }

  public static void setAsProvider(String customPropsFile) {
    BitwiseStoreUtil.getInstance()
        .setConfigurationPropertiesProvider(new DefaultConfigurationPropertiesProvider(customPropsFile));
  }

  public static void setRoot(String rootDir) {
    Properties p = new Properties();
    p.setProperty(ROOT_DIR_PROPERTY, rootDir);
    BitwiseStoreUtil.getInstance().setConfigurationPropertiesProvider(new DefaultConfigurationPropertiesProvider(p));
  }

  public static String getRoot() {
    return BitwiseStoreUtil.getInstance().getConfigurationPropertiesProvider().getDefaultProperties()
        .getProperty(ROOT_DIR_PROPERTY);
  }

  private File getStoreDir(String storeName) {
    return new File(defaultProperties_.getProperty(ROOT_DIR_PROPERTY), storeName);
  }

  private Properties loadPropertiesFile(Properties defaults, String propsFile, boolean mustExist) {
    return loadPropertiesFile(defaults, new File(propsFile), mustExist);
  }

  private Properties loadPropertiesFile(Properties defaults, File propsFile, boolean mustExist) {
    Properties props = new Properties(defaults);
    if(propsFile.exists() == false) {
      if(mustExist == true) {
        throw new RuntimeException(
            "Tried to load the properties file [" + propsFile.getAbsolutePath() + "] but it does not exist.");
      }
      return props;
    }
    try {
      loadPropertiesStream(props, new FileInputStream(propsFile));
      return props;
    } catch(FileNotFoundException e) {
      throw new RuntimeException("An error occured while loading the bitwise properties: " + e.getMessage(), e);
    }
  }

  private void loadPropertiesStream(Properties props, InputStream is) {
    try {
      props.load(is);
    } catch(IOException e) {
      throw new RuntimeException("An error occured while loading the bitwise properties: " + e.getMessage(), e);
    } finally {
      try {
        if(is != null) is.close();
      } catch(IOException e) {
        // ignore
      }
    }
  }
}
