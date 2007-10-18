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
 * Configuration provider for a BDB store that only defines the root of the store on disk. The store-specific properties file
 * is expected to be in each BDB store directory.
 */
public class BdbRootDirectoryProvider extends LocalPropertiesProvider {
  private String rootDir_ = null;

  public BdbRootDirectoryProvider(String rootDir) {
    super("./bitwise.properties");
    rootDir_ = rootDir;
  }

  
  public void saveSpecificProperties(String pStoreName, Properties pProp) {
    String targetPath = rootDir_ + "/" + pStoreName + "/bitwise.properties";
    savePropertiesFile(targetPath, pStoreName, pProp);
  }
  
  
  public Properties loadProperties(String pStoreName) {
    //Load default properties
    Properties p = super.loadProperties();
    p.setProperty(BdbEnvUtil.BDB_ROOT_PROPERTY, rootDir_);
    
    String specificPropertiesFile = rootDir_ + "/" + pStoreName + "/bitwise.properties";
    Properties sp = loadPropertiesFile(specificPropertiesFile, "Store-Specific Properties File", false);
    if (sp != null) {
      p.putAll(sp);
    }
    
    return p;
  }


  /**
   * Sets the root directory where all BDB stores all located for this project.
   * @param rootDir the BDB stores root directory.
   */
  public static void setRoot(String rootDir) {
    BdbRootDirectoryProvider p = new BdbRootDirectoryProvider(rootDir);
    BitwiseStoreUtil.getInstance().setConfigurationPropertiesProvider(p);
  }


  /**
   * Gets the root directory where all BDB stores all located for this project.
   * @return the BDB stores root directory.
   */
  public static String getRoot() {
    BdbRootDirectoryProvider p = (BdbRootDirectoryProvider)BitwiseStoreUtil.getInstance().getConfigurationPropertiesProvider();
    return p.rootDir_;
  }
}
