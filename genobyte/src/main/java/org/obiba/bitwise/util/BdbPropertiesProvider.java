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
package org.obiba.bitwise.util;

import java.util.Properties;

import org.obiba.bitwise.BitwiseStoreUtil;


/**
 * This configuration properties provider is designed for BDB stores with local configuration files.
 * It has three levels of properties files that can define iBatis and Bitwise properties. They are, in increasing order of priority:
 * <ul><li>Bitwise API default settings,</li>
 * <li>Project-wide properties, which apply to all stores in a given project,</li>
 * <li>Store-specific properties.</li></ul>
 */
public class BdbPropertiesProvider extends LocalPropertiesProvider {
  
  public BdbPropertiesProvider(String pPropFilename) {
    super(pPropFilename);
  }
  
  
  /**
   * Saves in a properties file the properties specific to a store.
   * @param pStoreName is the name of the store for which specific properties are being saved.
   * @param pProp are the properties specific to this store.
   */
  public void saveSpecificProperties(String pStoreName, Properties pProp) {
    String targetPath = loadProperties(pStoreName).getProperty("bdb.root") + "/" + pStoreName + "/bitwise.properties";
    savePropertiesFile(targetPath, pStoreName, pProp);
  }

  
  /**
   * Loads all configuration properties and merge them. In order of priority, from highest to lowest:
   * Store-Specific properties, Project-Wide Properties, Bitwise Factory Settings.
   * @param pStoreName is the name of the store for which to find specific properties file.
   * @return Merged configuration properties.
   */
  public Properties loadProperties(String pStoreName) {
    //Load default properties
    Properties p = super.loadProperties();
    
    //Load properties that are specific to this store using the storeName
    //If BDB root store directory is not defined, use working directory.
    String rootStoreDir = p.getProperty("bdb.root");
    if (rootStoreDir == null) {
      rootStoreDir = "./";
    }
    String specificPropertiesFile = rootStoreDir + "/" + pStoreName + "/bitwise.properties";
    Properties sp = loadPropertiesFile(specificPropertiesFile, "Store-Specific Properties File", false);
    if (sp != null) {
      p.putAll(sp);
    }
    
    return p;
  }
  
  
  /**
   * Sets the current class as the BitwiseStoreUtil properties provider.
   * In this case, the path to project-wide properties file is not provided,
   * so we'll check the current working directory.
   */
  public static void setAsProvider() {
    setAsProvider("./bitwise.properties");
  }
  
  
  /**
   * Sets the current class as the BitwiseStoreUtil properties provider.
   * @param pFilename is the path to the project-wide properties file.
   */
  public static void setAsProvider(String pFilename) {
    BdbPropertiesProvider pp = new BdbPropertiesProvider(pFilename);
    BitwiseStoreUtil.getInstance().setConfigurationPropertiesProvider(pp);
  }
}
