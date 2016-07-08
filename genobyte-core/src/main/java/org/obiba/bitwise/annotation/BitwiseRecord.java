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
package org.obiba.bitwise.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a class is defining a record structure for a bitwise store.
 * The elements of this annotation will give information on the store itself, and on dictionaries used in the store.
 * This annotation type is usually used together with @Stored and @NotStored annotations.   
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface BitwiseRecord {

  /**
   * If set to <tt>true</tt>, all class attributes will be considered as fields in the store.
   * If set to <tt>false</tt>, only the attributes with the "Stored" annotation will be considered.
   */
  boolean storeAll() default false;

  /**
   * Current version of this Bitwise Store.
   */
  String version() default "";

  /**
   * Name of the bitwise store.
   */
  String storeName() default "";

  /**
   * Dictionaries that will be associated to fields in the store. These dictionaries will be defined with sub-annotations
   * of the type @DictionaryDef.
   */
  DictionaryDef[] dictionary() default {};

  /**
   * Template field definitions. Templates are used to create fields at runtime.
   */
  FieldTemplate[] templates() default {};
}
