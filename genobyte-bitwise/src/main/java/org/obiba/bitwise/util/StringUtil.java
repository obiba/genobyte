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
package org.obiba.bitwise.util;

/**
 * A collection of static methods to manage string.
 */
public final class StringUtil {

  /**
   * Aggregates/untokenizes an array of field with the specify token
   * @param fields the fields to aggregate into a <tt>String</tt>.
   * @param token the token inserted between the aggregated fields.
   * @return a string of fields separate by the given token.
   */
  public static String aggregate(String[] fields, String token) {
    StringBuilder untokenize = new StringBuilder("");

    if(fields != null) {
      for(int i = 0; i < fields.length; i++) {
        if(i > 0) {
          untokenize.append(token);
        }
        if(fields[i] != null) {
          untokenize.append(fields[i].trim());
        }
      }
    }
    return untokenize.toString();
  }

  /**
   * Verifies if a string is empty/null.
   * @param s the <tt>String</tt> to verify.
   * @return <tt>true</tt> if s is null or empty, <tt>false</tt> otherwise.
   */
  public static boolean isEmptyString(String s) {
    if((s != null) && (!s.equals(""))) {
      return false;
    }

    return true;
  }

  /**
   * Verifies if a string is null or equals to "null". 
   * @param s the <tt>String</tt> to verify.
   * @return <tt>true</tt> if the <tt>String</tt> is null, <tt>false</tt> otherwise.
   */
  public static boolean isNullString(String s) {
    if((s != null) && (!s.equalsIgnoreCase("null"))) {
      return false;
    }

    return true;
  }

}
