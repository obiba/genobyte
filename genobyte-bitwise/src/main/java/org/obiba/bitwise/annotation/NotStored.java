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

import java.lang.annotation.*;

/**
 * Used to indicate that a field should not be stored in the bitwise. It is useful when the @BitwiseRecord annotation sets the element
 * <code>storeAll = true</code>, but that a certain field should not be included in the store anyway.
 * This annotation can be put on a field, or on one of its accession methods. @NotStored and @Stored annotations are mutually exclusive.
 * There cannot be more than one annotation of type @NotStored or @Stored in total on a field and its accession methods.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface NotStored {

}
