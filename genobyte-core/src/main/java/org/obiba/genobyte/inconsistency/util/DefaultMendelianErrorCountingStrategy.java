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
package org.obiba.genobyte.inconsistency.util;

import org.obiba.bitwise.Field;
import org.obiba.bitwise.util.IntegerFieldHelper;
import org.obiba.genobyte.GenotypingRecordStore;
import org.obiba.genobyte.inconsistency.MendelianErrorCountingStrategy;
import org.obiba.genobyte.inconsistency.MendelianErrors;
import org.obiba.genobyte.model.DefaultGenotypingField;

/**
 * A counting strategy that counts the mendelian errors as-is (no filtering or conditional counting).
 *
 * @param <K>  the unique key in the bitwise store where computation is made (usually the sample store).
 * @param <TK> the unique key for the transposed bitwise store (usually the assay store).
 */
public class DefaultMendelianErrorCountingStrategy<K> implements MendelianErrorCountingStrategy<K> {

  private GenotypingRecordStore<K, ?, ?> samples_ = null;

  private GenotypingRecordStore<?, ?, K> assays_ = null;

  private Field mendelField_ = null;

  private Field testField_ = null;

  private Field transposedMendelField_ = null;

  private Field transposedTestField_ = null;

  public DefaultMendelianErrorCountingStrategy(GenotypingRecordStore<K, ?, ?> samples,
                                               GenotypingRecordStore<?, ?, K> assays) {
    samples_ = samples;
    assays_ = assays;

    mendelField_ = samples_.getGenotypingField(DefaultGenotypingField.MENDEL.getName(), null);
    testField_ = samples_.getGenotypingField(DefaultGenotypingField.MENDEL_TESTS.getName(), null);
    IntegerFieldHelper.setAll(mendelField_, 0);
    IntegerFieldHelper.setAll(testField_, 0);

    transposedMendelField_ = assays_.getGenotypingField(DefaultGenotypingField.MENDEL.getName(), null);
    transposedTestField_ = assays_.getGenotypingField(DefaultGenotypingField.MENDEL_TESTS.getName(), null);
    IntegerFieldHelper.setAll(transposedMendelField_, 0);
    IntegerFieldHelper.setAll(transposedTestField_, 0);
  }

  public void countInconsistencies(MendelianErrors<K> errors) {
    int errorCount = errors.getInconsistencies().count();
    int tests = errors.getTests().count();

    IntegerFieldHelper.add(mendelField_, errors.getChildIndex(), errorCount);
    IntegerFieldHelper.add(testField_, errors.getChildIndex(), tests);
    if (errors.getMotherKey() != null) {
      IntegerFieldHelper.add(mendelField_, errors.getMotherIndex(), errorCount);
      IntegerFieldHelper.add(testField_, errors.getMotherIndex(), tests);
    }
    if (errors.getFatherKey() != null) {
      IntegerFieldHelper.add(mendelField_, errors.getFatherIndex(), errorCount);
      IntegerFieldHelper.add(testField_, errors.getFatherIndex(), tests);
    }
    IntegerFieldHelper.increment(transposedMendelField_, errors.getInconsistencies());
    IntegerFieldHelper.increment(transposedTestField_, errors.getTests());
  }

}
