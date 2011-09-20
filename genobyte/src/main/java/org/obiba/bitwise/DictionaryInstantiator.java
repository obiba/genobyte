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

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConstructorUtils;
import org.obiba.bitwise.util.Property;


/**
 * Creates instances of dictionaries that will encode/decode data from/to BitVectors.
 */
public class DictionaryInstantiator {
  public static Dictionary<?> createInstance(String pName, String pClazz) {
    try {
      Class<?> clazz = Class.forName(pClazz);
      if(Dictionary.class.isAssignableFrom(clazz) == false) {
        throw new IllegalStateException("Class " + pClazz + " does not implement the Dictionary interface.");
      }
      return (Dictionary<?>)ConstructorUtils.invokeConstructor(clazz, pName);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    } catch (InstantiationException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public static void setProperties(Dictionary<?> d, List<Property> properties) {
    if(properties == null) {
      return;
    }
    for (Property property : properties) {
      try {
        BeanUtils.setProperty(d, property.getName(), property.getValue());
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      } catch (InvocationTargetException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
