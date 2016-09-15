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

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.bitwise.util.BitVectorQueryResult;
import org.obiba.genobyte.GenotypingRecordStore;
import org.obiba.genobyte.inconsistency.MendelianRecordTrioProvider;

/**
 * An implementation of {@link MendelianRecordTrioProvider} that uses three fields that share unique individual IDs to build trios:
 * <ul>
 * <li>a mother individual ID field</li>
 * <li>a father individual ID field</li>
 * <li>a individual ID field</li>
 * </ul>
 * <p/>
 * A child is any record that has a non-null value in either the father or mother field. Mother and father records are identified
 * using the unique individual ID field.
 */
public class IndividualIdFieldTrioProvider implements MendelianRecordTrioProvider {

  private GenotypingRecordStore store_;

  private Field individualIdField_ = null;

  private Field motherIdField_ = null;

  private Field fatherIdField_ = null;

  private QueryResult emptyResult_ = null;

  public IndividualIdFieldTrioProvider(GenotypingRecordStore store, String individualIdField, String motherIdField,
                                       String fatherIdField) {
    store_ = store;
    individualIdField_ = store_.getStore().getField(individualIdField);
    motherIdField_ = store_.getStore().getField(motherIdField);
    fatherIdField_ = store_.getStore().getField(fatherIdField);
    emptyResult_ = new BitVectorQueryResult(new BitVector(store_.getStore().getCapacity()));
  }

  public QueryResult getChildRecords() {
    // Find all records that have at least a motherId NOT NULL or a fatherId NOT NULL
    QueryResult hasMother = motherIdField_.query(motherIdField_.getDictionary().lookup(null)).not();
    QueryResult hasFather = fatherIdField_.query(fatherIdField_.getDictionary().lookup(null)).not();
    return hasMother.or(hasFather);
  }

  public QueryResult getFatherRecords(int childRecord) {
    // Find all records that have the individual ID == child record's father ID
    BitVector fatherId = fatherIdField_.getValue(childRecord);
    if (fatherId == null) {
      return emptyResult_;
    }
    return individualIdField_.query(fatherId);
  }

  public QueryResult getMotherRecords(int childRecord) {
    // Find all records that have the individual ID == child record's mother ID
    BitVector motherId = motherIdField_.getValue(childRecord);
    if (motherId == null) {
      return emptyResult_;
    }
    return individualIdField_.query(motherId);
  }

}
