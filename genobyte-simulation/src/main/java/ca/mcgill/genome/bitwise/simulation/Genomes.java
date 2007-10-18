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
package ca.mcgill.genome.bitwise.simulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class Genomes {

  Random r = new Random();
  String[] genomes = null;
  String[] replicateGenomes = null;
  
  public Genomes(File genomesFile) throws IOException {
    genomes = parseGenomes(genomesFile);
  }

  public int size() {
    return genomes.length;
  }

  public String get(int i) {
    return genomes[i-1];
  }

  public String getRandom() {
    return genomes[r.nextInt(genomes.length)];
  }

  final private String[] parseGenomes(File genomeFile) throws IOException {
    System.out.println("Parsing genomes from ["+genomeFile+"]");
    BufferedReader br = new BufferedReader(new FileReader(genomeFile));
    List<String> g = new LinkedList<String>();
    String line = br.readLine();
    while(line != null) {
      g.add(line);
      line = br.readLine();
    }
    String[] genomes = new String[g.size()];
    int i = 0;
    for(String genome : g) {
      genomes[i++] = genome;
    }
    return genomes;
  }
}