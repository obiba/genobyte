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
package org.obiba.bitwise.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.obiba.bitwise.util.HuffmanSeedProvider;


public class GenericNameProvider extends SeparatedValuesProvider implements HuffmanSeedProvider {

  static private List<String> SOURCE = null;

  static public void setSource() {
    SOURCE = new ArrayList<String>();
    SOURCE.add("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    SOURCE.add("abcdefghijklmnopqrstuvwxyz");
    SOURCE.add("1234567890");
  }

  public Iterator<String> getSeed() {
    return SOURCE.iterator();
  }
}
