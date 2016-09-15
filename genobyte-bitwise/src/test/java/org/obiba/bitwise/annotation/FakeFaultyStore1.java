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

@BitwiseRecord(
    storeAll = false,
    storeName = "MyTestStore",
    version = "25.0",

    dictionary = { @DictionaryDef(
        name = "positiveInteger",
        dictionaryClassName = "org.obiba.bitwise.dictionary.IntegerDictionary",
        dictionaryClass = Integer.class,
        property = { @DictionaryProperty(
            name = "lower",
            value = "0"), @DictionaryProperty(
            name = "upper",
            value = "2147483647"), @DictionaryProperty(
            name = "step",
            value = "1") }) })
public class FakeFaultyStore1<T> {

  private Integer id = null;

  public FakeFaultyStore1() {
    super();
  }

  @Stored(dictionary = "positiveInteger", unique = true)
  public Integer getId() { return id; }

  public void setId(Integer pId) { id = pId; }
}
