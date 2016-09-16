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
package org.obiba.bitwise.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SeparatedValuesProvider {

  static protected List<String> getValues(File sourceFile, int... columns) {
    try {
      SeparatedValuesParser p = new SeparatedValuesParser(sourceFile, SeparatedValuesParser.COMMA);
      List<String> values = new ArrayList<String>(60000);
      SeparatedValuesRow row = p.nextRow();
      while (row != null) {
        for (int i = 0; i < columns.length; i++) {
          String v = row.getColumnValue(columns[i], String.class);
          if (v != null) values.add(v);
        }
        row = p.nextRow();
      }
      return values;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
