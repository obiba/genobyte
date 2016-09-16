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

import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.BitwiseStoreUtil;
import org.obiba.bitwise.Dictionary;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.dao.BaseBdbDaoTestCase;
import org.obiba.bitwise.query.Query;
import org.obiba.bitwise.query.QueryParser;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.bitwise.schema.StoreSchema;

import java.util.Properties;

public class StoreFromAnnotationTest extends BaseBdbDaoTestCase {

  /**
   * Build a store from an annotated class which uses inheritance.
   */
  public void testAnnotationInheritance() {
    AnnotationStoreSchemaBuilder sca = new AnnotationStoreSchemaBuilder();

    StoreSchema ss = sca.createSchema(FakeStoreWithInheritance.class);
    assertTrue(ss.getField("id") != null);
    assertTrue(ss.getField("name") != null);
    assertTrue(ss.getField("snp") != null);
    assertTrue(ss.getField("gene_name") != null);
    assertTrue(ss.getField("age") != null);
    assertTrue(ss.getField("happiness") != null);
  }

  /**
   * Try to build a schema that contains errors. We are testing that the proper exception and
   * exception message will be thrown.
   */
  public void testInvalidStoreCreation() {
    //When both dictionaryClassName and dictionaryClassName are defined for a DictionaryDef
    createFaultySchema(FakeFaultyStore1.class,
        "Class [org.obiba.bitwise.annotation.FakeFaultyStore1] can only have one of these attributes: dictionaryClass, dictionaryClassName.");

    //When none of dictionaryClassName and dictionaryClassName are defined for a DictionaryDef
    createFaultySchema(FakeFaultyStore2.class,
        "Class [org.obiba.bitwise.annotation.FakeFaultyStore2] should have at least one of these attributes: dictionaryClass, dictionaryClassName.");

    //When one property has two @Stored annotations
    createFaultySchema(FakeFaultyStore3.class,
        "Class [org.obiba.bitwise.annotation.FakeFaultyStore3] declares a property [id] that has more than one of these annotations (@Stored or @NotStored).");

    //When one property has both @Stored and @NotStored annotations
    createFaultySchema(FakeFaultyStore4.class,
        "Class [org.obiba.bitwise.annotation.FakeFaultyStore4] declares a property [id] that has more than one of these annotations (@Stored or @NotStored).");
  }

  private void createFaultySchema(Class storeClass, String expectedMessage) {
    AnnotationStoreSchemaBuilder sca = new AnnotationStoreSchemaBuilder();
    InvalidAnnotationException caughtException = null;

    try {
      sca.createSchema(storeClass);
    } catch (InvalidAnnotationException e) {
      caughtException = e;
    }
    assertTrue(caughtException != null && caughtException.getMessage().equals(expectedMessage));
  }

  /**
   * Build a valid store from a schema built with annotation, insert data in that store, and try to
   * run a query.
   */
  public void testStoreCreation() {
    String annotationStoreName = "myAnnotationStore";
    int storeSize = 10;

    BitwiseStore myStore = null;
    try {
      //Create store from Annotations schema
      myStore = createDbFromAnnotation(annotationStoreName, storeSize);
      myStore.startTransaction();
      fillData(myStore);
      myStore.commitTransaction();
      myStore.endTransaction();

      //Make sure the new store has been created
      assertNotNull(myStore);

      //Run some test queries on this annotation-created store.
      QueryParser qp = new QueryParser();
      String query1 = "(name:CDE OR snp:rs123) XOR age:4";
      String query2 = "NOT(gene_name:KCN*)";

      Query q1;
      Query q2;
      try {
        q1 = qp.parse(query1);
        q2 = qp.parse(query2);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      QueryResult qr1 = q1.execute(myStore);
      QueryResult qr2 = q2.execute(myStore);

      assertTrue(qr1.bits().toBitString().equals("1011000000"));
      assertTrue(qr2.bits().toBitString().equals("0011111111"));
    } finally {
      if (myStore != null) {
        myStore.endTransaction();
        myStore.close();
      }
    }

    System.out.println("Finished.");
  }

  /**
   * Build a new store based on class annotations
   *
   * @param pStoreName is the name of the store to be created.
   * @param pStoreSize is the starting size of the repository (number of records already set to null)
   * @return The newly created store as a BitwiseStore object.
   */
  public BitwiseStore createDbFromAnnotation(String pStoreName, int pStoreSize) {
    AnnotationStoreSchemaBuilder sca = new AnnotationStoreSchemaBuilder();
    StoreSchema mySchema = sca.createSchema(FakeStoreWithInheritance.class);

    //Creating the store from the information in StoreSchema
    Properties p = new Properties();
    p.put("je.maxMemory", "200000000");
    BitwiseStore newStore = BitwiseStoreUtil.getInstance().create(pStoreName, mySchema, pStoreSize, p);
    return newStore;
  }

  /**
   * Enter some testing data in the newly created store.
   *
   * @param pStore
   */
  public void fillData(BitwiseStore pStore) {
    this.addRecord(pStore, 2, "CDE", "rs222", "KCNMachin", 1);
    this.addRecord(pStore, 1, "B123A", "rs678", "KCNChose", 2);
    this.addRecord(pStore, 3, "ABC", "rs123", "SLCTruc", 3);
    this.addRecord(pStore, 10, "AAAA5678", "rs345", "WOW", 4);
    this.addRecord(pStore, 5, "AAAA5678", "rs123", "TEST", 4);
  }

  /**
   * Create a new row in the store at the next available index.
   *
   * @param pStore
   * @param pId
   * @param pName
   */
  private void addRecord(BitwiseStore pStore, int pId, String pName, String pSnp, String pGene, int pAge) {
    int recordIndex = pStore.nextIndex();

    //Setting "id" field
    Field idField = pStore.getField("id");
    Dictionary<Integer> idDict = idField.getDictionary();
    idField.setValue(recordIndex, idDict.lookup(pId));

    //Setting "name" field
    Field nameField = pStore.getField("name");
    Dictionary<String> nameDict = nameField.getDictionary();
    nameField.setValue(recordIndex, nameDict.lookup(pName));

    //Setting "snp" field
    Field snpField = pStore.getField("snp");
    Dictionary<String> snpDict = snpField.getDictionary();
    snpField.setValue(recordIndex, snpDict.lookup(pSnp));

    //Setting "gene" field
    Field geneField = pStore.getField("gene_name");
    Dictionary<String> geneDict = geneField.getDictionary();
    geneField.setValue(recordIndex, geneDict.lookup(pGene));

    //Setting "age" field
    Field ageField = pStore.getField("age");
    Dictionary<Integer> ageDict = ageField.getDictionary();
    ageField.setValue(recordIndex, ageDict.lookup(pAge));

    Integer storedId = idDict.reverseLookup(idField.getValue(recordIndex));
    String storedName = nameDict.reverseLookup(nameField.getValue(recordIndex));
    String storedSnp = snpDict.reverseLookup(snpField.getValue(recordIndex));
    String storedGene = geneDict.reverseLookup(geneField.getValue(recordIndex));
    Integer storedAge = ageDict.reverseLookup(ageField.getValue(recordIndex));

    assertEquals(new Integer(pId), storedId);
    assertEquals(pName, storedName);
    assertEquals(pSnp, storedSnp);
    assertEquals(pGene, storedGene);
    assertEquals(new Integer(pAge), storedAge);
  }
}  
  
