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
package org.obiba.genobyte.statistic.util;

import org.obiba.bitwise.AbstractField;
import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.VolatileField;
import org.obiba.genobyte.dao.BaseBdbDaoTestCase;
import org.obiba.genobyte.mock.MockBitwiseStore;
import org.obiba.bitwise.util.BitVectorQueryResult;
import org.obiba.genobyte.mock.MockGenotypingStore;
import org.obiba.genobyte.model.SnpCall;
import org.obiba.genobyte.statistic.StatsPool;
import org.obiba.genobyte.statistic.StatsRunDefinition;

/**
 * Tests the AssayFrequencies statistic.
 */
public class FrequencyStatisticsTest extends BaseBdbDaoTestCase {

  StatsPool<Integer, Integer> sp_ = null;

  protected void setUp() throws Exception {
    super.setUp();

    StatsRunDefinition srd = new StatsRunDefinition();
    srd.addStatistic(new AssayFrequencies());

    MockBitwiseStore bsAssay = new MockBitwiseStore("mock_assay");
    MockBitwiseStore bsSample = new MockBitwiseStore("mock_sample");
    MockGenotypingStore store = new MockGenotypingStore(bsSample, bsAssay);

    //Fill dummy data
    bsSample.setCall(0, 0, SnpCall.A);
    bsSample.setCall(0, 1, SnpCall.B);
    bsSample.setCall(0, 2, SnpCall.H);
    bsSample.setCall(1, 0, SnpCall.A);
    bsSample.setCall(1, 1, SnpCall.B);
    bsSample.setCall(1, 2, SnpCall.A);
    bsSample.setCall(2, 0, SnpCall.H);
    bsSample.setCall(2, 1, SnpCall.U);
    bsSample.setCall(2, 2, SnpCall.H);

    sp_ = new StatsPool<Integer, Integer>(store.getAssayRecordStore(), srd);
  }

  public void testWholeCalculation() {
    //Calculate statistics
    sp_.calculate();

    //Validate the results obtained from statistics calculation
    //Validate freqAjava
    VolatileField freqA = sp_.getPooledField("freqA", Integer.class);
    assertEquals(1, freqA.getDictionary().reverseLookup(freqA.getValue(0)));
    assertEquals(2, freqA.getDictionary().reverseLookup(freqA.getValue(1)));
    assertEquals(0, freqA.getDictionary().reverseLookup(freqA.getValue(2)));

    //Validate freqB
    VolatileField freqB = sp_.getPooledField("freqB", Integer.class);
    assertEquals(1, freqB.getDictionary().reverseLookup(freqB.getValue(0)));
    assertEquals(1, freqB.getDictionary().reverseLookup(freqB.getValue(1)));
    assertEquals(0, freqB.getDictionary().reverseLookup(freqB.getValue(2)));

    //Validate freqH
    VolatileField freqH = sp_.getPooledField("freqH", Integer.class);
    assertEquals(1, freqH.getDictionary().reverseLookup(freqH.getValue(0)));
    assertEquals(0, freqH.getDictionary().reverseLookup(freqH.getValue(1)));
    assertEquals(2, freqH.getDictionary().reverseLookup(freqH.getValue(2)));

    //Validate freqU
    VolatileField freqU = sp_.getPooledField("freqU", Integer.class);
    assertEquals(0, freqU.getDictionary().reverseLookup(freqU.getValue(0)));
    assertEquals(0, freqU.getDictionary().reverseLookup(freqU.getValue(1)));
    assertEquals(1, freqU.getDictionary().reverseLookup(freqU.getValue(2)));

    //Validate totalCalls
    VolatileField totalCalls = sp_.getPooledField("totalCalls", Integer.class);
    assertEquals(3, totalCalls.getDictionary().reverseLookup(totalCalls.getValue(0)));
    assertEquals(3, totalCalls.getDictionary().reverseLookup(totalCalls.getValue(1)));
    assertEquals(2, totalCalls.getDictionary().reverseLookup(totalCalls.getValue(2)));

    //Validate callRate
    VolatileField callRate = sp_.getPooledField("callRate", Integer.class);
    assertEquals(1.0d, callRate.getDictionary().reverseLookup(callRate.getValue(0)));
    assertEquals(1.0d, callRate.getDictionary().reverseLookup(callRate.getValue(1)));
    assertEquals(0.6666666666666666d, callRate.getDictionary().reverseLookup(callRate.getValue(2)));

    //Validate MAF
    VolatileField maf = sp_.getPooledField("maf", Double.class);
    assertEquals(0.5, maf.getDictionary().reverseLookup(maf.getValue(0)));
    assertEquals(0.3333333333333333, maf.getDictionary().reverseLookup(maf.getValue(1)));
    assertEquals(0.5, maf.getDictionary().reverseLookup(maf.getValue(2)));

    //Validate Hardy-Weinberg
    VolatileField hw = sp_.getPooledField("hw", Integer.class);
    assertEquals(0.3333333333333333d, hw.getDictionary().reverseLookup(hw.getValue(0)));
    assertEquals(3.0d, hw.getDictionary().reverseLookup(hw.getValue(1)));
    assertEquals(2.0d, hw.getDictionary().reverseLookup(hw.getValue(2)));

    //Validate heterozygosity
    VolatileField heterozygosity = sp_.getPooledField("heterozygosity", Integer.class);
    assertEquals(0.3333333333333333d, heterozygosity.getDictionary().reverseLookup(heterozygosity.getValue(0)));
    assertEquals(0d, heterozygosity.getDictionary().reverseLookup(heterozygosity.getValue(1)));
    assertEquals(1.0d, heterozygosity.getDictionary().reverseLookup(heterozygosity.getValue(2)));
  }

  //Test some values when applying a filter
  public void testCalculationWithFilter() {
    BitVector bv = new BitVector(sp_.getGenotypingRecordStore().getStore().getCapacity());
    bv.set(1);
    bv.set(2);
    BitVectorQueryResult qr = new BitVectorQueryResult(bv);
    sp_.setTransposedMask(qr);
    sp_.calculate();

    VolatileField maskedFreqA = sp_.getPooledField("freqA", Integer.class);
    assertEquals(0, maskedFreqA.getDictionary().reverseLookup(maskedFreqA.getValue(0)));
    assertEquals(1, maskedFreqA.getDictionary().reverseLookup(maskedFreqA.getValue(1)));
    assertEquals(0, maskedFreqA.getDictionary().reverseLookup(maskedFreqA.getValue(2)));

    VolatileField maskedMaf = sp_.getPooledField("maf", Double.class);
    assertEquals(0.25, maskedMaf.getDictionary().reverseLookup(maskedMaf.getValue(0)));
    assertEquals(0.5, maskedMaf.getDictionary().reverseLookup(maskedMaf.getValue(1)));
    assertEquals(0.5, maskedMaf.getDictionary().reverseLookup(maskedMaf.getValue(2)));
  }

  public void testCalculateStatsWhenNoCalls() {
    MockBitwiseStore bsAssay = new MockBitwiseStore("mock_assay");
    MockBitwiseStore bsSample = new MockBitwiseStore("mock_sample");
    MockGenotypingStore store = new MockGenotypingStore(bsSample, bsAssay);
    StatsRunDefinition srd = new StatsRunDefinition();
    srd.addStatistic(new AssayFrequencies());
    StatsPool sp = new StatsPool(store.getAssayRecordStore(), srd);
    sp.calculate();
    assertStatValue(sp, new Integer(0), "freqA", "freqB", "freqH", "freqU", "totalCalls");
    assertStatValue(sp, new Double(0), "callRate", "maf", "heterozygosity");
  }

  private void assertStatValue(StatsPool sp, Object value, String... statNames) {
    for(int s = 0; s < statNames.length; s++) {
      AbstractField statField = sp.getPooledField(statNames[s]);
      assertNotNull(statField);
      for(int i = 0; i < statField.getSize(); i++) {
        assertEquals("Stat [" + statNames[s] + "] for record " + i, value,
            statField.getDictionary().reverseLookup(statField.getValue(i)));
      }
    }
  }
}
