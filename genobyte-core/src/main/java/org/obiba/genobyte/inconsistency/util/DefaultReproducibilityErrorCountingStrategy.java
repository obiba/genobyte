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
import org.obiba.genobyte.inconsistency.ReproducibilityErrorCountingStrategy;
import org.obiba.genobyte.inconsistency.ReproducibilityErrors;
import org.obiba.genobyte.model.DefaultGenotypingField;

/**
 * A counting strategy that counts the reproducibility errors as-is (no filtering or conditional counting).
 */
public class DefaultReproducibilityErrorCountingStrategy<K, TK> implements ReproducibilityErrorCountingStrategy<K> {

  private GenotypingRecordStore<K, ?, TK> store_ = null;

  private GenotypingRecordStore<TK, ?, K> transposedStore_ = null;

  private Field reproField_ = null;

  private Field testField_ = null;

  private Field transposedReproField_ = null;

  private Field transposedTestField_ = null;

  public DefaultReproducibilityErrorCountingStrategy(DefaultGenotypingField field, DefaultGenotypingField testField,
                                                     GenotypingRecordStore<K, ?, TK> store, GenotypingRecordStore<TK, ?, K> transposedStore) {
    store_ = store;
    transposedStore_ = transposedStore;

    reproField_ = store_.getGenotypingField(field.getName(), null);
    testField_ = store_.getGenotypingField(testField.getName(), null);
    IntegerFieldHelper.setAll(reproField_, 0);
    IntegerFieldHelper.setAll(testField_, 0);

    transposedReproField_ = transposedStore_.getGenotypingField(field.getName(), null);
    transposedTestField_ = transposedStore_.getGenotypingField(testField.getName(), null);
    IntegerFieldHelper.setAll(transposedReproField_, 0);
    IntegerFieldHelper.setAll(transposedTestField_, 0);
  }

  public void countInconsistencies(ReproducibilityErrors<K> errors) {
    int errorCount = errors.getInconsistencies().count();
    int tests = errors.getTests().count();
    IntegerFieldHelper.add(reproField_, errors.getReferenceIndex(), errorCount);
    IntegerFieldHelper.add(testField_, errors.getReferenceIndex(), tests);
    IntegerFieldHelper.add(reproField_, errors.getReplicateIndex(), errorCount);
    IntegerFieldHelper.add(testField_, errors.getReplicateIndex(), tests);
    IntegerFieldHelper.increment(transposedReproField_, errors.getInconsistencies());
    IntegerFieldHelper.increment(transposedTestField_, errors.getTests());
  }

}
