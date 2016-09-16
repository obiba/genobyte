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

    dictionary = {@DictionaryDef(
        name = "names",
        dictionaryClassName = "org.obiba.bitwise.dictionary.HuffmanDictionary",
        property = {@DictionaryProperty(
            name = "provider",
            value = "org.obiba.bitwise.annotation.GenericNameProvider")}), @DictionaryDef(
        name = "positiveInteger",
        dictionaryClassName = "org.obiba.bitwise.dictionary.IntegerDictionary",
        property = {@DictionaryProperty(
            name = "lower",
            value = "0"), @DictionaryProperty(
            name = "upper",
            value = "2147483647"), @DictionaryProperty(
            name = "step",
            value = "1")}), @DictionaryDef(
        name = "limitedInteger",
        dictionaryClassName = "org.obiba.bitwise.dictionary.IntegerDictionary",
        property = {@DictionaryProperty(
            name = "lower",
            value = "-5"), @DictionaryProperty(
            name = "upper",
            value = "5"), @DictionaryProperty(
            name = "step",
            value = "1")})})
public class FakeStore {

  private Integer id = null;

  //TODO: dictionary used is Default_Integer
  @Stored
  private String name = null;

  private String snp = null;

  //TODO: gene name in store is gene_wow, dictionary used is "names"
  @Stored(dictionary = "names", field = "gene_name")
  private String gene = null;

  //TODO: age dictionary is default Integer dictionary
  @Stored(field = "age")
  private Integer age = null;

  public FakeStore() {
    super();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("FakeStore{ snp=[").append(snp).append("]").append(" name=[").append(name).append("]").append(" gene=[")
        .append(gene).append("]").append("}");
    return sb.toString();
  }

  //TODO: id is the unique field, the dictionary positiveInteger is properly set.
  @Stored(dictionary = "positiveInteger", unique = true)
  public Integer getId() {
    return id;
  }

  public void setId(Integer pId) {
    id = pId;
  }

  public String getName() {
    return name;
  }

  public void setName(String pName) {
    name = pName;
  }

  //TODO: snp is set to HuffmanDictionary
  public String getSnp() {
    return snp;
  }

  @Stored
  public void setSnp(String pSnp) {
    snp = pSnp;
  }

  public String getGene() {
    return gene;
  }

  public void setGene(String pGene) {
    gene = pGene;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer pAge) {
    age = pAge;
  }

//  public String getAge1() { return "boite"; }
//  public void setAge1(Integer pAge) { }
}
