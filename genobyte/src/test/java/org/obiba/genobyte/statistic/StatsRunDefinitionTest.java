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
package org.obiba.genobyte.statistic;

import java.util.List;

import org.obiba.bitwise.dao.BaseBdbDaoTestCase;
import org.obiba.genobyte.statistic.FieldStatistic;
import org.obiba.genobyte.statistic.RecordStatistic;
import org.obiba.genobyte.statistic.StatsRunDefinition;
import org.obiba.genobyte.statistic.util.CallRate;
import org.obiba.genobyte.statistic.util.Frequencies;
import org.obiba.genobyte.statistic.util.Maf;
import org.obiba.genobyte.statistic.util.TotalCalls;


/**
 * Tests AbstractField methods by using a persisted field on a dummy bitwise store.
 */
public class StatsRunDefinitionTest extends BaseBdbDaoTestCase {

  /**
   * Tests if statistics are placed in the correct iterations depending on their parameter requirements.
   */
  public void testIterations() {
    MockStatsRunDefinition srd = new MockStatsRunDefinition();
    srd.addStatistic(new Frequencies());
    srd.addStatistic(new TotalCalls());
    srd.addStatistic(new Maf());
    srd.addStatistic(new CallRate());
    
    //There should be three iterations
    assertEquals(2, srd.getMaxIteration());
    
    //Frequencies should be the only statistic in the first iteration, of the RecordStatistic type.
    List<RecordStatistic> it0 = srd.getRecordStats(0);
    assertEquals(1, it0.size());
    assertTrue(it0.get(0).getOutputParams().contains("freqA"));
    assertEquals(0, srd.getFieldStats(0).size());
    
    //TotalCall should be the only statistic in the second iteration, of the FieldStatistic type.
    List<FieldStatistic> it1 = srd.getFieldStats(1);
    assertEquals(1, it1.size());
    assertTrue(it1.get(0).getOutputParams().contains("totalCalls"));
    assertEquals(0, srd.getRecordStats(1).size());
    
    //Maf and CallRate should be in the third iteration
    List<FieldStatistic> it2 = srd.getFieldStats(2);
    assertEquals(2, it2.size());
    assertTrue(it2.get(0).getOutputParams().contains("maf"));
    assertTrue(it2.get(1).getOutputParams().contains("callRate"));
    assertEquals(0, srd.getRecordStats(2).size());
  }
  
  private class MockStatsRunDefinition extends StatsRunDefinition {}

}
