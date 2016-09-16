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
package org.obiba.genobyte;

import junit.framework.TestCase;
import org.obiba.bitwise.Field;
import org.obiba.genobyte.mock.MockBitwiseStore;
import org.obiba.genobyte.mock.MockGenotypingStore;
import org.obiba.genobyte.model.SnpCall;

public class GenotypingFieldTransposerTest extends TestCase {

  public void testMissingFirstSourceField() {

    // The GenotypingFieldValueTransposer used to NPE when the first calls field is missing. This reproduces the problem.

    MockBitwiseStore samples = new MockBitwiseStore("samples");
    MockBitwiseStore assays = new MockBitwiseStore("assays");

    // Create some genotypes for the second and third samples in the store (not for the first)... 
    for (int i = 0; i < assays.getSize(); i++) {
      samples.setCall(i, 1, SnpCall.A);
      samples.setCall(i, 2, SnpCall.H);
    }

    MockGenotypingStore store = new MockGenotypingStore(samples, assays);
    GenotypingStoreTransposer transposer = new GenotypingStoreTransposer(store.getSampleRecordStore());
    transposer.setDeleteDestinationStore(false);
    transposer.transpose();

    for (int i = 0; i < assays.getSize(); i++) {
      Field calls = store.getAssayRecordStore().getGenotypingField("calls", new Integer(i));
      assertNotNull(calls);
      assertNull(calls.getDictionary().reverseLookup(calls.getValue(0)));
      assertEquals(SnpCall.A, calls.getDictionary().reverseLookup(calls.getValue(1)));
      assertEquals(SnpCall.H, calls.getDictionary().reverseLookup(calls.getValue(2)));
    }
  }

}
