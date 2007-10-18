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
package org.obiba.illumina.bitwise.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.obiba.bitwise.annotation.BitwiseRecord;
import org.obiba.bitwise.annotation.DictionaryDef;
import org.obiba.bitwise.annotation.DictionaryProperty;
import org.obiba.bitwise.annotation.FieldTemplate;
import org.obiba.bitwise.annotation.Stored;
import org.obiba.bitwise.dictionary.EnumDictionary;
import org.obiba.genobyte.model.DefaultCallsStats;



@BitwiseRecord(
    storeAll=true,
    dictionary={@DictionaryDef(name="calls", dictionaryClass=EnumDictionary.class, property={@DictionaryProperty(name="enumClassName", value="org.obiba.genobyte.model.SnpCall")})},
    templates={@FieldTemplate(prefix="calls", dictionary="calls")}
    )
public class Sample extends DefaultCallsStats {

  public enum Gender {
    Female, Male, Unknown
  }

  @Stored(unique=true)
  private String id = null;
  private String name = null;
  private String plate = null;
  private String well = null;
  private String sentrixBarcodeA = null;
  private String sentrixPositionA = null;

  private Gender gender = null;

  private String group = null;
  private String replicates = null;
  private String parent1 = null;
  private String parent2 = null;
  private String path = null;
  private String reference = null;
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getPlate() {
    return plate;
  }
  public void setPlate(String plate) {
    this.plate = plate;
  }
  public String getWell() {
    return well;
  }
  public void setWell(String well) {
    this.well = well;
  }
  public String getSentrixBarcodeA() {
    return sentrixBarcodeA;
  }
  public void setSentrixBarcodeA(String sentrixBarcodeA) {
    this.sentrixBarcodeA = sentrixBarcodeA;
  }
  public String getSentrixPositionA() {
    return sentrixPositionA;
  }
  public void setSentrixPositionA(String sentrixPositionA) {
    this.sentrixPositionA = sentrixPositionA;
  }
  public Gender getGender() {
    return gender;
  }
  public void setGender(Gender gender) {
    this.gender = gender;
  }
  public String getGroup() {
    return group;
  }
  public void setGroup(String group) {
    this.group = group;
  }
  public String getReplicates() {
    return replicates;
  }
  public void setReplicates(String replicates) {
    this.replicates = replicates;
  }
  public String getParent1() {
    return parent1;
  }
  public void setParent1(String parent1) {
    this.parent1 = parent1;
  }
  public String getParent2() {
    return parent2;
  }
  public void setParent2(String parent2) {
    this.parent2 = parent2;
  }
  public String getPath() {
    return path;
  }
  public void setPath(String path) {
    this.path = path;
  }
  public String getReference() {
    return reference;
  }
  public void setReference(String reference) {
    this.reference = reference;
  }
}
