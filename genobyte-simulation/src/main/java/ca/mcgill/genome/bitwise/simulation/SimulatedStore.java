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

import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.BitwiseStoreUtil;
import org.obiba.bitwise.annotation.AnnotationStoreSchemaBuilder;
import org.obiba.bitwise.dictionary.EnumDictionary;
import org.obiba.bitwise.schema.DictionaryMetaData;
import org.obiba.bitwise.schema.FieldMetaData;
import org.obiba.bitwise.schema.StoreSchema;
import org.obiba.genobyte.GenotypingStore;


public class SimulatedStore extends GenotypingStore<Integer, SimulatedAssay, Integer, SimulatedSample>{

  private static final String SAMPLE_STORE_NAME = "simul_samples";
  private static final String ASSAY_STORE_NAME = "simul_assays";

  private SimulatedStore(BitwiseStore samples, BitwiseStore assays) {
    super(new SimulatedSampleGenotypingStore(samples), new SimulatedAssayGenotypingStore(assays));
  }
  
  static private StoreSchema createCallsField(StoreSchema ss) {
    DictionaryMetaData dmd = new DictionaryMetaData();
    dmd.setName("calls");
    dmd.setClass(EnumDictionary.class.getName());
    ss.addDictionary(dmd);
    FieldMetaData fmd = new FieldMetaData();
    fmd.setName("calls");
    fmd.setTemplate(true);
    fmd.setDictionary("calls");
    ss.addField(fmd);
    fmd = new FieldMetaData();
    fmd.setName("comparableCalls");
    fmd.setTemplate(true);
    fmd.setDictionary("calls");
    ss.addField(fmd);
    return ss;
  }

  static public SimulatedStore create(int sampleSize, int assaySize) {
    
    AnnotationStoreSchemaBuilder builder = new AnnotationStoreSchemaBuilder();
    StoreSchema sampleSchema = createCallsField(builder.createSchema(SimulatedSample.class));
    StoreSchema assaySchema = createCallsField(builder.createSchema(SimulatedAssay.class));

//    XmlStoreSchemaBuilder builder = new XmlStoreSchemaBuilder();
//    StoreSchema sampleSchema = builder.parse(new InputSource(SimulatedStore.class.getResourceAsStream("sample-schema.xml")));
//    StoreSchema assaySchema = builder.parse(new InputSource(SimulatedStore.class.getResourceAsStream("assay-schema.xml")));

    BitwiseStore sampleStore = null;
    BitwiseStore assayStore = null;
    sampleStore = BitwiseStoreUtil.getInstance().create(SAMPLE_STORE_NAME, sampleSchema, sampleSize);
    assayStore = BitwiseStoreUtil.getInstance().create(ASSAY_STORE_NAME, assaySchema, assaySize);
    SimulatedStore store = new SimulatedStore(sampleStore, assayStore);
    return store;
  }
  
  static public SimulatedStore open() {
    BitwiseStore sampleStore = null;
    BitwiseStore assayStore = null;
    sampleStore = BitwiseStoreUtil.getInstance().open(SAMPLE_STORE_NAME);
    if(sampleStore == null) {
      throw new IllegalArgumentException("Cannot open store ["+SAMPLE_STORE_NAME+"]");
    }
    assayStore = BitwiseStoreUtil.getInstance().open(ASSAY_STORE_NAME);
    if(assayStore == null) {
      throw new IllegalArgumentException("Cannot open store ["+ASSAY_STORE_NAME+"]");
    }
    return new SimulatedStore(sampleStore, assayStore); 
  }

}
