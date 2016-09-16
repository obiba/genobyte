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
package org.obiba.bitwise.schema.defaultDict;

import org.obiba.bitwise.annotation.BitwiseRecord;

@BitwiseRecord(
    storeAll = true,
    storeName = "MyTestStore",
    version = "25.0")
public class FakeStoreForDefaultDictionaries {

  private byte a;

  private short b;

  private int c;

  private long d;

  private float e;

  private double f;

  private boolean g;

  private char h;

  private Integer i;

  private Double j;

  private String k;

  public FakeStoreForDefaultDictionaries() {
    super();
  }

  public byte getA() {
    return a;
  }

  public void setA(byte p) {
    a = p;
  }

  public short getB() {
    return b;
  }

  public void setB(short p) {
    b = p;
  }

  public int getC() {
    return c;
  }

  public void setC(int p) {
    c = p;
  }

  public long getD() {
    return d;
  }

  public void setD(long p) {
    d = p;
  }

  public float getE() {
    return e;
  }

  public void sete(float p) {
    e = p;
  }

  public double getF() {
    return f;
  }

  public void setF(double p) {
    f = p;
  }

  public boolean getG() {
    return g;
  }

  public void setG(boolean p) {
    g = p;
  }

  public char getH() {
    return h;
  }

  public void setH(char p) {
    h = p;
  }

  public Integer getI() {
    return i;
  }

  public void setI(Integer p) {
    i = p;
  }

  public Double getJ() {
    return j;
  }

  public void setJ(Double p) {
    j = p;
  }

  public String getK() {
    return k;
  }

  public void setK(String p) {
    k = p;
  }

}
