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

import java.io.IOException;
import java.util.Properties;

import junit.framework.TestCase;

import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.BitwiseStoreUtil;
import org.obiba.bitwise.annotation.AnnotationStoreSchemaBuilder;
import org.obiba.bitwise.annotation.FakeStoreWithInheritance;
import org.obiba.bitwise.schema.StoreSchema;

public class StorePropertiesFileTest extends TestCase {

  private final String propertyFileLocation_ = "./src/test/java/org/obiba/bitwise/util/defaultPropertyFile/bitwise.properties";
  private final String storeLocation_ = "./src/test/java/org/obiba/bitwise/util/superStore";
  
  public void setUp() throws Exception {
    super.setUp();
    
    //Delete test store directory if it already exists, as we are testing here the creation of a new store.
    try {
      FileUtil.deltree(storeLocation_);
    } catch (IOException e) {}
  }
  

  /**
   * Build a store using a default properties file, and set specific properties to it.
   */
  public void testPropertiesFile() {
    String storeName = "greatNewStore";
    int storeSize = 10;
    
    DefaultConfigurationPropertiesProvider.setAsProvider(propertyFileLocation_);
    DefaultConfigurationPropertiesProvider cpp = (DefaultConfigurationPropertiesProvider) BitwiseStoreUtil.getInstance().getConfigurationPropertiesProvider();

    BitwiseStore myStore = null;
    try {
      Properties p = cpp.loadProperties(storeName);
      
      //Create store from Annotations schema
      myStore = createDb(storeName, storeSize);
      //Make sure the new store has been created
      assertNotNull(myStore);
      
      myStore.startTransaction();
      myStore.commitTransaction();
      myStore.endTransaction();
      
      //Make sure properties from the default file have been extracted.
      assertEquals(p.getProperty(DefaultConfigurationPropertiesProvider.ROOT_DIR_PROPERTY), storeLocation_);
    }
    finally {
      if(myStore != null) {
        myStore.close();
      }
    }
    
    //We will now reload the store and make sure the store-specific properties have been loaded 
    try { 
      myStore = BitwiseStoreUtil.getInstance().open(storeName);
      Properties p = cpp.loadProperties(storeName);
      assertEquals(p.toString(), p.getProperty("sp.test"), "It works.");
      assertEquals(p.getProperty("overwrittenProperty"), "2");
    }
    finally {
      if(myStore != null) {
        myStore.close();
      }
    }
    
    try {
      FileUtil.deltree(storeLocation_);
    } catch (IOException e) {}
  }


  public BitwiseStore createDb(String pStoreName, int pStoreSize) {
    AnnotationStoreSchemaBuilder sca = new AnnotationStoreSchemaBuilder();
    StoreSchema mySchema = sca.createSchema(FakeStoreWithInheritance.class);
    
    //Creating store-specific properties
    Properties p = new Properties();
    p.put("overwrittenProperty", "2");
    p.put("sp.test", "It works.");
    
    //Creating the store from the information in StoreSchema    
    BitwiseStore newStore = BitwiseStoreUtil.getInstance().create(pStoreName, mySchema, pStoreSize, p);
    return newStore;
  }
}  
  
