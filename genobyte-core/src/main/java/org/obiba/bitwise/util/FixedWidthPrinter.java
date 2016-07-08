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

import java.io.PrintStream;

/**
 * Utility class to print a fixed-width table to a <tt>PrintStream</tt>.
 */
public class FixedWidthPrinter {

  private PrintStream output;

  private int[] widths;

  /**
   * Build a printer that will output a fixed width table to <tt>ps</tt>. The number of columns that will be output must be specified with the <tt>columns</tt> parameter.
   * @param ps the output stream to print to
   * @param columns the number of columns to output
   */
  public FixedWidthPrinter(PrintStream ps, int columns) {
    this.output = ps;
    this.widths = new int[columns];
  }

  /**
   * Set the columns widths. The last column will always take the remaining space. Hence, it is not necessary to specify its width. If 0 is specified for any column, no
   * fixed-width formating will occur which will break the output for multiple lines. It is recommended to output all non-fixed columns after fixed width columns.
   *
   * @param widths
   */
  public void setWidths(int... widths) {
    if(widths.length > this.widths.length) throw new IllegalArgumentException("too many widths");
    for(int i = 0; i < widths.length; i++) {
      this.widths[i] = widths[i];
    }
  }

  /**
   * Set the width of the column <tt>column</tt>
   *
   * @param column the column's index
   * @param width the width to set for the specified column
   */
  public void setWidth(int column, int width) {
    widths[column] = width;
  }

  /**
   * Print values to the output with padding and truncating the values as needed.
   * @param columns the values to output
   */
  public void printLine(Object... columns) {
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < columns.length; i++) {
      if(i > 0) sb.append(" ");

      String column = asString(columns[i]);
      if(i < widths.length && widths[i] > 0) {
        pad(sb, column, widths[i]);
      } else {
        sb.append(column);
      }
    }
    output.println(sb.toString());
  }

  private void pad(StringBuilder sb, String value, int size) {
    if(value.length() > size) {
      // need to truncate the value
      value = value.substring(0, size);
    }
    // Pad with spaces up to newSize - value.length()
    int newSize = sb.length() + size - value.length();
    while(sb.length() < newSize) sb.append(" ");
    sb.append(value);
  }

  private String asString(Object o) {
    if(o == null) return "";
    return o.toString();
  }

}
