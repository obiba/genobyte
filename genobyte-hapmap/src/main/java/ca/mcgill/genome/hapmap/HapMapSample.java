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
package ca.mcgill.genome.hapmap;

import java.util.LinkedList;
import java.util.List;

import org.obiba.bitwise.annotation.BitwiseRecord;
import org.obiba.bitwise.annotation.Stored;
import org.obiba.genobyte.model.SnpGenotype;


@BitwiseRecord
public class HapMapSample {

  private String name_ = null;

  private List<SnpGenotype<Integer>> genotypes_ = null;

  public HapMapSample() {
    super();
  }

  public void addGenotype(SnpGenotype<Integer> g) {
    if(genotypes_ == null) {
      genotypes_ = new LinkedList<SnpGenotype<Integer>>();
    }
    genotypes_.add(g);
  }

  @Override
  public String toString() {
    return "Sample name=["+name_+"]";
  }

  /**
   * @return Returns the genotypes.
   */
  public List<SnpGenotype<Integer>> getGenotypes() {
    return genotypes_;
  }

  /**
   * @return Returns the name.
   */
  @Stored(unique=true)
  public String getName() {
    return name_;
  }

  public void setName(String name) {
    name_ = name;
  }
}
