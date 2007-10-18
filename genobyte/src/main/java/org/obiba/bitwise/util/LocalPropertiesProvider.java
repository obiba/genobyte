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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.obiba.bitwise.ConfigurationPropertiesProvider;


/**
 * Configuration Providers extending this abstract class are using Properties Files located on disk
 * to store iBatis and Bitwise configuration properties.
 */
public abstract class LocalPropertiesProvider implements ConfigurationPropertiesProvider {
  private String projectPropFile_ = null;

  public LocalPropertiesProvider(String pProjectPropFile) {
    projectPropFile_ = pProjectPropFile;
  }
  
  
  /**
   * Load Bitwise Factory Settings and project-wide properties file.
   * @return Merged properties, giving priority to project-wide properties over Bitwise Factory Settings.
   */
  public Properties loadProperties() {
    //Load the Bitwise default Properties File.
    Properties p = loadDefaultProperties();
    
    //Load project-wide properties, and merge with Bitwise "factory settings" 
    Properties pp = loadPropertiesFile(projectPropFile_, "Project Default Properties File", false);
    if (pp != null) {
      p.putAll(pp);
    }
    
    return p;
  }

  
  /**
   * Returns the properties file provided by default with the Bitwise API.
   * @return The default properties for the bitwise system.
   */
  protected Properties loadDefaultProperties() {
    InputStream is = LocalPropertiesProvider.class.getResourceAsStream("/bitwise.properties");
    if (is == null) {
      throw new RuntimeException("Factory Default Properties File cannot be found.");
    }
    return loadPropertiesStream(is, "Factory Default Properties");
  }
  
  
  /**
   * Load a properties file at a provided location.
   * @param pPath is the path where the properties file can be found.
   * @param pDescription is the information about the properties file, used to display error message when loading fails.
   * @return Properties that could be found in the properties file.
   */
  protected Properties loadPropertiesFile(String pPath, String pDescription, boolean pMustExist) {
    Properties p = new Properties();
    try {
      InputStream is = new FileInputStream(pPath);
      p = loadPropertiesStream(is, pDescription);
    }
    catch (FileNotFoundException e) {
      if (pMustExist) {
        throw new RuntimeException(pDescription + " [" + pPath + "] cannot be found.");
      }
      else {
        return null;
      }
    }
    return p;
  }
  
  
  /**
   * Load properties from a stream provided in a "properties file" format.
   * @param pStream is the stream containing the properties data in a "properties file" format.
   * @param pDescription is the information about the properties stream, used to display error message when loading fails.
   * @return Properties that could be found in the stream.
   */
  protected Properties loadPropertiesStream(InputStream pStream, String pDescription) {
    Properties p = new Properties();
    try {
      p.load(pStream);
    }
    catch (IOException e) {
      throw new RuntimeException(pDescription + " stream cannot be loaded.");
    }
    return p;
  }
  
  
  /**
   * Save in a properties file the properties specific to a store.
   * @param pPath is the name of the store for which specific properties are being saved.
   * @param pProp are the properties specific to this store.
   */
  protected void savePropertiesFile(String pPath, String pStoreName, Properties pProp) {
    try {
      pProp.store(new FileOutputStream(pPath), "Custom Properties File for store " + pStoreName);
    }
    catch (IOException e) {
      throw new RuntimeException("Custom Properties File for store [" + pStoreName + "] could not be created at [" + pPath + "].");
    }
  }
  
}
