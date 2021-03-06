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

import junit.framework.TestCase;
import org.obiba.genobyte.mock.MockBitwiseStore;
import org.obiba.genobyte.mock.MockGenotypingStore;

public class StatsPoolDigestorTest extends TestCase {

  public void testDigestEmptyPool() {
    // Create a store with samples, but without assays
    MockBitwiseStore bsAssay = new MockBitwiseStore("mock_samples");
    MockBitwiseStore bsSample = new MockBitwiseStore("mock_assays", 0);
    MockGenotypingStore mockStore = new MockGenotypingStore(bsSample, bsAssay);
    try {
      mockStore.getAssayRecordStore().updateStats();
      assertTrue(true);
    } catch (Exception e) {
      assertTrue(e.getMessage(), false);
    }
  }
}
