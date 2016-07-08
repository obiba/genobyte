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
package org.obiba.bitwise;

import java.util.Properties;

/**
 * Classes implementing this interface will provide a variety of parameters to the bitwise, such as
 * JVM and store-specific parameters.
 */
public interface ConfigurationPropertiesProvider {

  /**
   * Returns the default Bitwise configuration properties 
   * @return the default configuration properties
   */
  public Properties getDefaultProperties();

  /**
   * Store the custom properties provided to a store.
   * @param storeName is the name of the store to which to custom properties apply.
   * @param pProp are the default properties merged with custom properties for this store.
   * @param pSpecificProp are the properties specific to this store, the ones needing to be saved.
   */
  public void saveSpecificProperties(String storeName, Properties pProp);

  /**
   * Load the default properties, the ones that apply to all stores.
   * @param storeName is the name of a store for which we will use the default properties.
   * @return The default properties that apply to all stores.
   */
  public Properties loadProperties(String storeName);

}
