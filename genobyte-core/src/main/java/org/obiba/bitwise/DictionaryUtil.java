/*******************************************************************************
 * Copyright 2007(c) G�nome Qu�bec. All rights reserved.
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
package org.obiba.bitwise;

import java.util.Arrays;
import java.util.List;

import org.obiba.bitwise.dao.DictionaryDtoDao;
import org.obiba.bitwise.dao.KeyedDaoManager;
import org.obiba.bitwise.dto.DictionaryDto;
import org.obiba.bitwise.util.Property;


/**
 * Various utility methods to handle <tt>Dictionary</tt> instances in a store.
 */
class DictionaryUtil {

  private BitwiseStore store_ = null;
  
  DictionaryUtil(BitwiseStore store) {
    super();
    store_ = store;
  }
  
  /**
   * List the name of all existing dictionaries in the BitwiseStore.
   * 
   * @return a <code>List&lt;String&gt;</code> of dictionary names.
   */
  List<String> list() {
    DictionaryDtoDao dao = (DictionaryDtoDao)KeyedDaoManager.getInstance(store_.getDaoKey()).getDao(DictionaryDtoDao.class);
    return dao.keys();
  }

  Dictionary<?> createDictionary(String name, String clazz, List<Property> properties) {
    DictionaryDto data = new DictionaryDto(name, clazz, properties);
    DictionaryDtoDao dao = (DictionaryDtoDao)KeyedDaoManager.getInstance(store_.getDaoKey()).getDao(DictionaryDtoDao.class);
    dao.create(data);

    Dictionary<?> d = DictionaryInstantiator.createInstance(data.getName(), data.getClazz());
    if(d == null) {
      throw new IllegalArgumentException("Dictonary meta data is invalid: " + data);
    }
    DictionaryInstantiator.setProperties(d, data.getProperties());
    return d;
  }

  Dictionary<?> openDictionary(String name) {
    DictionaryDtoDao dao = (DictionaryDtoDao)KeyedDaoManager.getInstance(store_.getDaoKey()).getDao(DictionaryDtoDao.class);
    DictionaryDto data = dao.load(name);

    Dictionary<?> d = DictionaryInstantiator.createInstance(data.getName(), data.getClazz());
    if(d == null) {
      throw new IllegalArgumentException("Dictonary meta data is invalid: " + data);
    }
    d.setRuntimeData(data.getRuntimeData());
    DictionaryInstantiator.setProperties(d, data.getProperties());
    return d;
  }


  /**
   * Saves a dictionary instance into disk.
   * @param d the dictionary to be saved.
   */
  void saveDictionary(Dictionary<?> d) {
    DictionaryDtoDao dao = (DictionaryDtoDao)KeyedDaoManager.getInstance(store_.getDaoKey()).getDao(DictionaryDtoDao.class);
    DictionaryDto data = dao.load(d.getName());
    if(Arrays.equals(data.getRuntimeData(), d.getRuntimeData()) == false) {
      data.setRuntimeData(d.getRuntimeData());
      dao.save(data);
    }
  }

}
