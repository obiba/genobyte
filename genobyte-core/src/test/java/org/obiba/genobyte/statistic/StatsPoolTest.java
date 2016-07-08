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
package org.obiba.genobyte.statistic;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.VolatileField;
import org.obiba.bitwise.dao.BaseBdbDaoTestCase;
import org.obiba.bitwise.mock.MockBitwiseStore;
import org.obiba.bitwise.util.BitVectorQueryResult;
import org.obiba.genobyte.mock.MockGenotypingStore;
import org.obiba.genobyte.model.SnpCall;
import org.obiba.genobyte.statistic.util.CallRate;
import org.obiba.genobyte.statistic.util.Frequencies;
import org.obiba.genobyte.statistic.util.Maf;
import org.obiba.genobyte.statistic.util.TotalCalls;

/**
 * Tests the <tt>StatsPool</tt> class, by calculating statistics with a <tt>StatsRunDefinition</tt> of
 * multiple iterations.
 */
public class StatsPoolTest extends BaseBdbDaoTestCase {

  StatsPool<Integer, Integer> sp_ = null;

  MockGenotypingStore store = null;

  protected void setUp() throws Exception {
    super.setUp();

    StatsRunDefinition srd = new StatsRunDefinition();
    srd.addStatistic(new Frequencies());
    srd.addStatistic(new TotalCalls());
    srd.addStatistic(new Maf());
    srd.addStatistic(new CallRate());

    MockBitwiseStore bsAssay = new MockBitwiseStore("mock_assay");
    MockBitwiseStore bsSample = new MockBitwiseStore("mock_sample");
    store = new MockGenotypingStore(bsSample, bsAssay);

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
    VolatileField freqA = sp_.getPooledField("freqA", Integer.class);
    assertEquals(1, freqA.getDictionary().reverseLookup(freqA.getValue(0)));
    assertEquals(2, freqA.getDictionary().reverseLookup(freqA.getValue(1)));
    assertEquals(0, freqA.getDictionary().reverseLookup(freqA.getValue(2)));

    VolatileField maf = sp_.getPooledField("maf", Double.class);
    assertEquals(0.5, maf.getDictionary().reverseLookup(maf.getValue(0)));
    assertEquals(1 / 3d, maf.getDictionary().reverseLookup(maf.getValue(1)));
    assertEquals(0.5, maf.getDictionary().reverseLookup(maf.getValue(2)));

    //Empty the pool (and make sure it is really empty)
    sp_.empty();
    assertEquals(0, sp_.getPool().size());
  }

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

  public void testCalculateWithDeleted() {
    // Delete a sample.
    store.getSampleRecordStore().getStore().delete(0);

    // Reset the pool's record masks. This needs to be done after calling delete()...
    sp_.resetMasks();

    // Calculate statistics
    sp_.calculate();

    // Validate the results obtained from statistics calculation considering that one record has been deleted
    VolatileField freqA = sp_.getPooledField("freqA", Integer.class);
    assertEquals(0, freqA.getDictionary().reverseLookup(freqA.getValue(0)));
    assertEquals(1, freqA.getDictionary().reverseLookup(freqA.getValue(1)));
    assertEquals(0, freqA.getDictionary().reverseLookup(freqA.getValue(2)));
  }

}
