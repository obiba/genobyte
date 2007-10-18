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
package org.obiba.bitwise.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Defines a dictionary used to encode/decode values for one or more fields in the store.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(value={}) // Only usable as a property to other annotations
public @interface DictionaryDef {

  /**
   * Name of the dictionary associated with this field.
   */
  String name();


  /**
   * Java class that defines the mechanics of a dictionary. Dictionary definition can be a class or a class name,
   * but not both at the same time. Therefore, the elements dictionaryClass and dictionaryClassName are mutually exclusive. 
   */
  Class dictionaryClass() default void.class;


  /**
   * Java class that defines the mechanics of a dictionary. Dictionary definition can be a class or a class name,
   * but not both at the same time. Therefore, the elements dictionaryClass and dictionaryClassName are mutually exclusive. 
   */
  String dictionaryClassName() default "";


  /**
   * If specific properties must be provided for the dictionary, they must be listed here using @DictionaryProperty annotations.
   */
  DictionaryProperty[] property() default {};
}
