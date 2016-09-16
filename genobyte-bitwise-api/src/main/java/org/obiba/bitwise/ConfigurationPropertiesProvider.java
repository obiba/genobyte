/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.obiba.bitwise;

import java.util.Properties;

/**
 * Classes implementing this interface will provide a variety of parameters to the bitwise, such as
 * JVM and store-specific parameters.
 */
public interface ConfigurationPropertiesProvider {

  /**
   * The configuration key for the root directory of bitwise stores.
   */
  String ROOT_DIR_PROPERTY = "bitwise.dir.root";

  /**
   * The configuration key for the bitwise DAO implementation to use.
   */
  String BITWISE_DAO_IMPL = "bitwise.dao.impl";

  /**
   * Returns the default Bitwise configuration properties
   *
   * @return the default configuration properties
   */
  Properties getDefaultProperties();

  /**
   * Store the custom properties provided to a store.
   *
   * @param storeName is the name of the store to which to custom properties apply.
   * @param pProp     are the default properties merged with custom properties for this store.
   */
  void saveSpecificProperties(String storeName, Properties pProp);

  /**
   * Load the default properties, the ones that apply to all stores.
   *
   * @param storeName is the name of a store for which we will use the default properties.
   * @return The default properties that apply to all stores.
   */
  Properties loadProperties(String storeName);

}
