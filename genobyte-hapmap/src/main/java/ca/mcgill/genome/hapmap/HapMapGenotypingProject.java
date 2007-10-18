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

import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.BitwiseStoreUtil;
import org.obiba.bitwise.schema.StoreSchema;
import org.obiba.bitwise.schema.XmlStoreSchemaBuilder;
import org.obiba.bitwise.util.BdbRootDirectoryProvider;
import org.obiba.genobyte.GenotypingStore;
import org.xml.sax.InputSource;


public class HapMapGenotypingProject extends GenotypingStore<Integer, HapMapAssay, String, HapMapSample> {

  private static String ROOT_DIR = "hapmap_bitwise";
  
  private HapMapGenotypingProject(String name, BitwiseStore samples, BitwiseStore assays) {
    super(new HapMapSampleGenotypingStore(samples), new HapMapAssayGenotypingStore(assays));
  }
  
  static public void setDirectory(String dir) {
    ROOT_DIR = dir;
  }

  static public HapMapGenotypingProject create(String population) {
    BdbRootDirectoryProvider.setRoot(ROOT_DIR);
    XmlStoreSchemaBuilder builder = new XmlStoreSchemaBuilder();
    StoreSchema sampleSchema = builder.parse(new InputSource(HapMapGenotypingProject.class.getResourceAsStream("sample-schema.xml")));
    StoreSchema assaySchema = builder.parse(new InputSource(HapMapGenotypingProject.class.getResourceAsStream("assay-schema.xml")));
    BitwiseStore sampleStore = BitwiseStoreUtil.getInstance().create(population + "_samples", sampleSchema, 10);
    BitwiseStore assayStore = BitwiseStoreUtil.getInstance().create(population + "_assays", assaySchema, 10);
    return new HapMapGenotypingProject(population, sampleStore, assayStore);
  }

  static public HapMapGenotypingProject open(String population) {
    BdbRootDirectoryProvider.setRoot(ROOT_DIR);
    BitwiseStore samples = BitwiseStoreUtil.getInstance().open(population + "_samples");
    BitwiseStore assays = BitwiseStoreUtil.getInstance().open(population + "_assays");
    if(samples == null || assays == null) {
      return null;
    }
    return new HapMapGenotypingProject(population, samples, assays);
  }

}
