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
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Used to indicate that a field should be stored in the bitwise. This annotation can be put on a field,
 * or on one of its accession methods. @NotStored and @Stored annotations are mutually exclusive.
 * There cannot be more than one annotation of type @NotStored or @Stored in total on a field and its accession methods.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface Stored {
  
  /**
   * Name of the field.
   */
  String field() default "" ;


  /**
   * Sets this field as the unique field of the store. A unique field is a field whose value must be unique among all records.
   * It is also used as the primary key by the bitwise. There can only be one unique field in a bitwise store.
   */
  boolean unique() default false;


  /**
   * Name of the dictionary associated with this field. It is the name given to a dictionary in a @DictionaryDef annotation, not the
   * name of a dictionary class.
   */
  String dictionary() default "";
}
