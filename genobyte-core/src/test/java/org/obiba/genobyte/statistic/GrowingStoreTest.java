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

import java.util.ArrayList;
import java.util.List;

import org.obiba.genobyte.mock.MockBitwiseStore;
import org.obiba.genobyte.mock.MockGenotypingStore;
import org.obiba.genobyte.model.SnpCall;
import org.obiba.genobyte.model.SnpGenotype;

import junit.framework.TestCase;

public class GrowingStoreTest extends TestCase {

  /**
   * There are no assertions in this test because it was created to 
   * reproduce a RuntimeException.
   */
  public void testCalculateStatsAfterStoreGrows() {

    // Create a store of default size
    MockGenotypingStore store = new MockGenotypingStore();

    // Make it grow
    ((MockBitwiseStore) store.getSampleRecordStore().getStore()).setSize(2000);
    ((MockBitwiseStore) store.getAssayRecordStore().getStore()).setSize(317503);

    // Create some calls
    List<SnpGenotype<Integer>> calls = new ArrayList<SnpGenotype<Integer>>(317503);
    for(int i = 0; i < 317503; i++) {
      SnpGenotype<Integer> call = new SnpGenotype<Integer>();
      call.setTransposedKey(i);
      call.setValue(SnpCall.A);
      calls.add(call);
    }

    // Populate the store with calls
    store.getSampleRecordStore().setTransposedValues("calls", new Integer(1901), calls);

    // Calculate the stats for the sample. This used to throw and ArrayOutOfBoundsException
    try {
      store.getSampleRecordStore().updateStats(new Integer(1901));
    } catch(RuntimeException e) {
      fail(e.getMessage());
    }
  }
}
