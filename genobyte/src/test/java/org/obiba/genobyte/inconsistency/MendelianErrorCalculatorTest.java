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
package org.obiba.genobyte.inconsistency;

import junit.framework.TestCase;

import org.obiba.bitwise.Dictionary;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.genobyte.GenotypingRecordStore;
import org.obiba.genobyte.model.SnpCall;
import org.obiba.genobyte.statistic.MockBitwiseStore;
import org.obiba.genobyte.statistic.MockGenotypingStore;
import org.obiba.genobyte.statistic.StatsRunDefinition;
import org.obiba.genobyte.statistic.util.CallRate;
import org.obiba.genobyte.statistic.util.Frequencies;
import org.obiba.genobyte.statistic.util.Maf;
import org.obiba.genobyte.statistic.util.TotalCalls;


public class MendelianErrorCalculatorTest extends TestCase {

  MockGenotypingStore store = null;
  MockBitwiseStore bsAssay = null;
  MockBitwiseStore bsSample = null;
  
  MendelianErrorCalculator<String> mendelCalculator = null;
  MendelianErrorAccum errorAccum = null;
  
  
  protected void setUp() throws Exception {
    super.setUp();
    
    StatsRunDefinition srd = new StatsRunDefinition();
    srd.addStatistic(new Frequencies());
    srd.addStatistic(new TotalCalls());
    srd.addStatistic(new Maf());
    srd.addStatistic(new CallRate());

    bsAssay = new MockBitwiseStore("mock_assay");
    bsSample = new MockBitwiseStore("mock_sample");
    store = new MockGenotypingStore(bsSample, bsAssay);
    
    Field trioStatus = bsSample.createField("trio_status", Integer.class);
    Dictionary<Integer> trioStatusDict = trioStatus.getDictionary();
    
    trioStatus.setValue(0, trioStatusDict.lookup(1));
    trioStatus.setValue(1, trioStatusDict.lookup(2));
    trioStatus.setValue(2, trioStatusDict.lookup(3));
    
    GenotypingRecordStore samples = store.getSampleRecordStore();
    mendelCalculator = new MendelianErrorCalculator<String>(samples);
    DummyTrioProvider trioProvider = new DummyTrioProvider(store.getSampleRecordStore(), "trio_status");
    mendelCalculator.setRecordProvider(trioProvider);
    errorAccum = new MendelianErrorAccum(samples);
    mendelCalculator.setCountingStrategy(errorAccum);
  }
  
  
  /**
   * Alls trio tests that do not represent a Mendelian error.
   */
  public void testValidTrioCases() {
    bsAssay.setSize(10);
    
    bsAssay.setCall(0, 0, SnpCall.A);
    bsAssay.setCall(1, 0, SnpCall.A);
    bsAssay.setCall(2, 0, SnpCall.A);
    
    bsAssay.setCall(0, 1, SnpCall.A);
    bsAssay.setCall(1, 1, SnpCall.B);
    bsAssay.setCall(2, 1, SnpCall.H);
    
    bsAssay.setCall(0, 2, SnpCall.B);
    bsAssay.setCall(1, 2, SnpCall.A);
    bsAssay.setCall(2, 2, SnpCall.H);
    
    bsAssay.setCall(0, 3, SnpCall.A);
    bsAssay.setCall(1, 3, SnpCall.H);
    bsAssay.setCall(2, 3, SnpCall.A);
    
    bsAssay.setCall(0, 4, SnpCall.A);
    bsAssay.setCall(1, 4, SnpCall.H);
    bsAssay.setCall(2, 4, SnpCall.H);
    
    bsAssay.setCall(0, 5, SnpCall.H);
    bsAssay.setCall(1, 5, SnpCall.A);
    bsAssay.setCall(2, 5, SnpCall.A);
    
    bsAssay.setCall(0, 6, SnpCall.H);
    bsAssay.setCall(1, 6, SnpCall.A);
    bsAssay.setCall(2, 6, SnpCall.H);
    
    bsAssay.setCall(0, 7, SnpCall.H);
    bsAssay.setCall(1, 7, SnpCall.H);
    bsAssay.setCall(2, 7, SnpCall.A);
    
    bsAssay.setCall(0, 8, SnpCall.H);
    bsAssay.setCall(1, 8, SnpCall.H);
    bsAssay.setCall(2, 8, SnpCall.B);
    
    bsAssay.setCall(0, 9, SnpCall.H);
    bsAssay.setCall(1, 9, SnpCall.H);
    bsAssay.setCall(2, 9, SnpCall.H);
    
    mendelCalculator.calculate();
    assertEquals(10, errorAccum.getTrioTests());
    assertEquals(0, errorAccum.getTrioErrors());
  }
  
  
  public void testValidFatherDuoWithNoMotherCall() {
    bsAssay.setCall(0, 0, SnpCall.A);
//    bsAssay.setCall(1, 0, SnpCall.A);     Mother record has no genotype at all
    bsAssay.setCall(2, 0, SnpCall.A);
    
    mendelCalculator.calculate();
    assertEquals(0, errorAccum.getTrioTests());
    assertEquals(1, errorAccum.getFatherDuoTests());
    assertEquals(0, errorAccum.getMotherDuoTests());
    assertEquals(0, errorAccum.getTrioErrors());
    assertEquals(0, errorAccum.getFatherDuoErrors());
    assertEquals(0, errorAccum.getMotherDuoErrors());
  }
  
  
  public void testValidMotherDuoWithNoMotherCall() {
//    bsAssay.setCall(0, 0, SnpCall.A);     Father record has no genotype at all
    bsAssay.setCall(1, 0, SnpCall.A);
    bsAssay.setCall(2, 0, SnpCall.A);
    
    mendelCalculator.calculate();
    assertEquals(0, errorAccum.getTrioTests());
    assertEquals(0, errorAccum.getFatherDuoTests());
    assertEquals(1, errorAccum.getMotherDuoTests());
    assertEquals(0, errorAccum.getTrioErrors());
    assertEquals(0, errorAccum.getFatherDuoErrors());
    assertEquals(0, errorAccum.getMotherDuoErrors());
  }
  
  
  public void testValidFatherDuoWithSomeMotherCalls() {
    bsAssay.setCall(0, 0, SnpCall.A);
    bsAssay.setCall(1, 0, SnpCall.A);
    bsAssay.setCall(2, 0, SnpCall.A);
    
    bsAssay.setCall(0, 1, SnpCall.A);
//    bsAssay.setCall(1, 1, SnpCall.A);
    bsAssay.setCall(2, 1, SnpCall.A);
    
    mendelCalculator.calculate();
    assertEquals(1, errorAccum.getTrioTests());
    assertEquals(1, errorAccum.getFatherDuoTests());
    assertEquals(0, errorAccum.getMotherDuoTests());
    assertEquals(0, errorAccum.getTrioErrors());
    assertEquals(0, errorAccum.getFatherDuoErrors());
    assertEquals(0, errorAccum.getMotherDuoErrors());
  }
  
  
  public void testValidMotherDuoWithSomeFatherCalls() {
    bsAssay.setCall(0, 0, SnpCall.A);
    bsAssay.setCall(1, 0, SnpCall.A);
    bsAssay.setCall(2, 0, SnpCall.A);
    
//    bsAssay.setCall(0, 1, SnpCall.A);
    bsAssay.setCall(1, 1, SnpCall.A);
    bsAssay.setCall(2, 1, SnpCall.A);
    
    mendelCalculator.calculate();
    assertEquals(1, errorAccum.getTrioTests());
    assertEquals(0, errorAccum.getFatherDuoTests());
    assertEquals(1, errorAccum.getMotherDuoTests());
    assertEquals(0, errorAccum.getTrioErrors());
    assertEquals(0, errorAccum.getFatherDuoErrors());
    assertEquals(0, errorAccum.getMotherDuoErrors());
  }
  
  
  public void testErrorsInTriosAndDuos() {
    bsAssay.setSize(5);
    
    bsAssay.setCall(0, 0, SnpCall.A);
    bsAssay.setCall(1, 0, SnpCall.A);
    bsAssay.setCall(2, 0, SnpCall.B);
    
    bsAssay.setCall(0, 1, SnpCall.A);
    bsAssay.setCall(1, 1, SnpCall.H);
    bsAssay.setCall(2, 1, SnpCall.B);
    
    bsAssay.setCall(0, 2, SnpCall.A);
//    bsAssay.setCall(1, 2, SnpCall.A);
    bsAssay.setCall(2, 2, SnpCall.B);
    
//    bsAssay.setCall(0, 3, SnpCall.A);
    bsAssay.setCall(1, 3, SnpCall.A);
    bsAssay.setCall(2, 3, SnpCall.B);
    
    //Valid trio that should not be counted
    bsAssay.setCall(0, 4, SnpCall.A);
    bsAssay.setCall(1, 4, SnpCall.H);
    bsAssay.setCall(2, 4, SnpCall.H);
    
    mendelCalculator.calculate();
    assertEquals(3, errorAccum.getTrioTests());
    assertEquals(1, errorAccum.getFatherDuoTests());
    assertEquals(1, errorAccum.getMotherDuoTests());
    assertEquals(2, errorAccum.getTrioErrors());
    assertEquals(1, errorAccum.getFatherDuoErrors());
    assertEquals(1, errorAccum.getMotherDuoErrors());
  }
  
  
  private class MendelianErrorAccum implements MendelianErrorCountingStrategy<String> {

    private int trioTests = 0;
    private int fatherDuoTests = 0;
    private int motherDuoTests = 0;
    private int trioErrors = 0;
    private int fatherDuoErrors = 0;
    private int motherDuoErrors = 0;
    private int fatherId = -1;
    private int motherId = -1;
    private int childId = -1;

    MendelianErrorAccum(GenotypingRecordStore store) {}

    public void countInconsistencies(MendelianErrors<String> errors) {
      if (errors.getMotherIndex() != -1 && errors.getFatherIndex() != -1) {
        trioTests += errors.getTests().count();
        trioErrors += errors.getInconsistencies().count();
      }
      else if (errors.getFatherIndex() != -1) {
        fatherDuoTests += errors.getTests().count();
        fatherDuoErrors += errors.getInconsistencies().count();
      }
      else if (errors.getMotherIndex() != -1) {
        motherDuoTests += errors.getTests().count();
        motherDuoErrors += errors.getInconsistencies().count();
      }
      
      fatherId = errors.getFatherIndex();
      motherId = errors.getMotherIndex();
      childId = errors.getChildIndex();
    }
    
    public int getTrioTests() { return trioTests; }
    public int getFatherDuoTests() { return fatherDuoTests; }
    public int getMotherDuoTests() { return motherDuoTests; }
    public int getTrioErrors() { return trioErrors; }
    public int getFatherDuoErrors() { return fatherDuoErrors; }
    public int getMotherDuoErrors() { return motherDuoErrors; }
    public int getFatherId() { return fatherId; }
    public int getMotherId() { return motherId; }
    public int getChildId() { return childId; }
  }
  

  //A fake trio provider that looks at the trio_status field we just created.
  //Value is 1 for father, 2 for mother, 3 for child
  public class DummyTrioProvider implements MendelianRecordTrioProvider {

    private GenotypingRecordStore store_;
    private Field trioStatusField_ = null;

    public DummyTrioProvider(GenotypingRecordStore store, String trioStatusFieldName) {
      store_ = store;
      trioStatusField_ = store_.getStore().getField(trioStatusFieldName);
    }

    public QueryResult getFatherRecords(int childRecord) {
      QueryResult father = trioStatusField_.query(trioStatusField_.getDictionary().lookup(1));
      return father;
    }

    public QueryResult getMotherRecords(int childRecord) {
      QueryResult mother = trioStatusField_.query(trioStatusField_.getDictionary().lookup(2));
      return mother;
    }
    
    public QueryResult getChildRecords() {
      QueryResult child = trioStatusField_.query(trioStatusField_.getDictionary().lookup(3));
      return child;
    }
  
  }
  
}