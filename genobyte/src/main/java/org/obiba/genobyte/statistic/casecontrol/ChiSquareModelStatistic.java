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
package org.obiba.genobyte.statistic.casecontrol;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.obiba.bitwise.AbstractField;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.genobyte.statistic.AbstractStatistic;
import org.obiba.genobyte.statistic.FieldStatistic;
import org.obiba.genobyte.statistic.StatsPool;
import org.obiba.genobyte.statistic.util.ChiSquare;

public class ChiSquareModelStatistic extends AbstractStatistic implements FieldStatistic {

  List<CaseControlChiSquareModel> models = new LinkedList<CaseControlChiSquareModel>();

  public static final String CHI_SQUARE_SUFFIX = "ChiSquare";
  public static final String P_VALUE_SUFFIX = "PValue";

  public ChiSquareModelStatistic() {
    super.inputParams_.addAll(Arrays.asList(CaseControlFrequencies.PARAMETERS));
  }

  public void addModel(CaseControlChiSquareModel model) {
    models.add(model);
    super.outputParams_.add(model.getName() + CHI_SQUARE_SUFFIX);
    super.outputParams_.add(model.getName() + P_VALUE_SUFFIX);
  }

  public void calculate(StatsPool<?, ?> pool, Map<String, Object> fields, QueryResult filter) {
    for (int r = filter.next(0); r != -1; r = filter.next(r + 1)) {
      CaseControlGenotypeFrequency ccgf = getCcgf(r, pool);
      for (CaseControlChiSquareModel model : this.models) {
        AbstractField modelChiSquare = pool.getPooledField(model.getName() + CHI_SQUARE_SUFFIX, Double.class);
        AbstractField modelPValue = pool.getPooledField(model.getName() + P_VALUE_SUFFIX, Double.class);

        double chiSquare = model.calculateChiSquare(ccgf);
        double pvalue = Double.NaN;
        if (Double.isNaN(chiSquare) == false) {
          pvalue = ChiSquare.chiSquareQ(chiSquare, model.getFreedom());
        }
        modelChiSquare.setValue(r, modelChiSquare.getDictionary().lookup(
            chiSquare));
        modelPValue.setValue(r, modelPValue.getDictionary().lookup(pvalue));
      }
    }
  }

  private CaseControlGenotypeFrequency getCcgf(int r, StatsPool<?, ?> pool) {
    return new CaseControlGenotypeFrequency(getGenotypeFrequency(r, pool, "case"), getGenotypeFrequency(r, pool, "control"));
  }

  private GenotypeFrequency getGenotypeFrequency(int r, StatsPool<?, ?> pool, String prefix) {
    AbstractField freqA = (AbstractField) pool.getPooledField(prefix + "FreqA");
    if (freqA == null) {
      throw new NullPointerException("Field " + prefix + "FreqA is null");
    }
    AbstractField freqB = (AbstractField) pool.getPooledField(prefix + "FreqB");
    if (freqA == null) {
      throw new NullPointerException("Field " + prefix + "FreqB is null");
    }
    AbstractField freqH = (AbstractField) pool.getPooledField(prefix + "FreqH");
    if (freqA == null) {
      throw new NullPointerException("Field " + prefix + "FreqH is null");
    }
    AbstractField freqU = (AbstractField) pool.getPooledField(prefix + "FreqU");
    if (freqA == null) {
      throw new NullPointerException("Field " + prefix + "FreqU is null");
    }
    AbstractField callRate = (AbstractField) pool.getPooledField(prefix + "CallRate");
    if (freqA == null) {
      throw new NullPointerException("Field " + prefix + "CallRate is null");
    }

    GenotypeFrequency gf = new GenotypeFrequency();
    gf.aa = (Integer)freqA.getDictionary().reverseLookup(freqA.getValue(r));
    gf.bb = (Integer)freqB.getDictionary().reverseLookup(freqB.getValue(r));
    gf.ab = (Integer)freqH.getDictionary().reverseLookup(freqH.getValue(r));
    gf.uu = (Integer)freqU.getDictionary().reverseLookup(freqU.getValue(r));
    gf.callRate = (Double)callRate.getDictionary().reverseLookup(callRate.getValue(r));
    return gf;
  }
}
