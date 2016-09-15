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

import org.obiba.bitwise.VolatileField;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.genobyte.statistic.AbstractStatistic;
import org.obiba.genobyte.statistic.FieldStatistic;
import org.obiba.genobyte.statistic.StatsPool;

import java.util.Map;

public class TotalCalls extends AbstractStatistic implements FieldStatistic {

  public TotalCalls() {
    inputParams_.add("freqA");
    inputParams_.add("freqB");
    inputParams_.add("freqH");

    outputParams_.add("totalCalls");
  }

  public void calculate(StatsPool<?, ?> pPool, Map<String, Object> pFields, QueryResult pFilter) {
    VolatileField freqA = pPool.getPooledField("freqA", Integer.class);
    VolatileField freqB = pPool.getPooledField("freqB", Integer.class);
    VolatileField freqH = pPool.getPooledField("freqH", Integer.class);

    for (int i = pFilter.next(0); i != -1; i = pFilter.next(i + 1)) {
      int tc = (Integer) freqA.getDictionary().reverseLookup(freqA.getValue(i)) +
          (Integer) freqB.getDictionary().reverseLookup(freqB.getValue(i)) +
          (Integer) freqH.getDictionary().reverseLookup(freqH.getValue(i));

      pPool.setPoolResult("totalCalls", i, tc);
    }
  }

}
