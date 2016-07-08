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
package org.obiba.genobyte.statistic.util;

import java.util.Map;

import org.obiba.bitwise.VolatileField;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.genobyte.statistic.AbstractStatistic;
import org.obiba.genobyte.statistic.FieldStatistic;
import org.obiba.genobyte.statistic.StatsPool;


public class Maf extends AbstractStatistic implements FieldStatistic {

  public Maf() {
    inputParams_.add("freqA");
    inputParams_.add("freqB");
    inputParams_.add("freqH");
    inputParams_.add("totalCalls");
   
    outputParams_.add("maf");
  }

  public void calculate(StatsPool<?,?> pPool, Map<String, Object> pFields, QueryResult pFilter) {
    VolatileField freqA = pPool.getPooledField("freqA", Integer.class);
    VolatileField freqB = pPool.getPooledField("freqB", Integer.class);
    VolatileField freqH = pPool.getPooledField("freqH", Integer.class);
    VolatileField totalCalls = pPool.getPooledField("totalCalls", Integer.class);
    
    for (int i=pFilter.next(0); i!=-1; i=pFilter.next(i+1)) {
      int aa = (Integer)freqA.getDictionary().reverseLookup(freqA.getValue(i));
      int bb = (Integer)freqB.getDictionary().reverseLookup(freqB.getValue(i));
      int ab = (Integer)freqH.getDictionary().reverseLookup(freqH.getValue(i));
      int tc = (Integer)totalCalls.getDictionary().reverseLookup(totalCalls.getValue(i));
      
      double maf = (2d * Math.min(aa,bb) + ab) / (2d*tc);
      maf = Double.isNaN(maf) ? 0.0 : maf;
      
      pPool.setPoolResult("maf", i, maf);
    }
  }

}
