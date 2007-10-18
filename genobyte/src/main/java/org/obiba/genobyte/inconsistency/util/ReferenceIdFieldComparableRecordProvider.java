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
package org.obiba.genobyte.inconsistency.util;

import java.util.Set;
import java.util.TreeSet;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.Dictionary;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.FieldValueIterator;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.bitwise.util.BitVectorQueryResult;
import org.obiba.genobyte.GenotypingRecordStore;
import org.obiba.genobyte.inconsistency.ComparableRecordProvider;


/**
 * Implements {@link ComparableRecordProvider} based on two fields: 
 * <ul>
 *   <li>a unique ID field</li>
 *   <li>a reference ID field</li>
 * </ul>
 * <p/>
 * The unique ID field should contain the records unique keys. The reference ID field should contain the
 * ID of another record with which this record may be compared to.
 */
public class ReferenceIdFieldComparableRecordProvider implements ComparableRecordProvider {

  private GenotypingRecordStore store_;
  private Field referenceIdField_;
  private Field idField_;

  public ReferenceIdFieldComparableRecordProvider(GenotypingRecordStore store, String idField, String referenceIdField) {
    store_ = store;
    BitwiseStore bs = store_.getStore();
    referenceIdField_ = bs.getField(referenceIdField);
    if(referenceIdField_ == null) {
      throw new IllegalArgumentException("Field ["+referenceIdField+"] does not exist.");
    }
    idField_ = bs.getField(idField);
    if(idField_ == null) {
      throw new IllegalArgumentException("Field ["+idField+"] does not exist.");
    }
  }

  public QueryResult getComparableRecords(int reference) {
    // Get the unique ID of the reference record
    BitVector referenceId = idField_.getValue(reference);
    
    // Find all records that have referenceId as the value
    return referenceIdField_.query(referenceId);
  }

  public QueryResult getComparableReferenceRecords() {

    // If both fields use the same dictionary, we don't have to convert the BitVector to the actual key value
    boolean lookupValues = true;
    Dictionary referenceIdDictionary = referenceIdField_.getDictionary();
    Dictionary idDictionary = idField_.getDictionary();
    if(referenceIdDictionary.isOrdered() && idDictionary.isOrdered() && referenceIdDictionary.getName().equals(idDictionary.getName())) {
      lookupValues = false;
    }

    // Field all records that have a value in the referenceId field (the records that are comparable to a reference)
    BitVector recordsWithReference = referenceIdField_.getNulls().not();

    // Build a set of unique reference IDs (contains unique referenceIds)
    Set<Object> referenceSampleIds = new TreeSet<Object>();

    // Add all values to the set of reference IDs
    FieldValueIterator fvi = new FieldValueIterator<String>(referenceIdField_, new BitVectorQueryResult(recordsWithReference));
    while(fvi.hasNext()) {
      FieldValueIterator.FieldValue fv = fvi.next();

      Object value = null;
      if(lookupValues == true) {
        value = fv.getValue();
      } else {
        value = fv.getBitValue();
      }
      // Sanity check... This may happen with faulty dictionary?
      if(value == null) {
        continue;
      }
      referenceSampleIds.add(value);
    }

    // For every unique referenceId, find the reference record and build vector out of them
    QueryResult references = null;
    for (Object referenceId : referenceSampleIds) {
      BitVector value = null;
      if(lookupValues == true) {
        value = idField_.getDictionary().lookup(referenceId);
      } else {
        value = (BitVector)referenceId;
      }
      QueryResult reference = idField_.query(value);
      if(references == null) {
        references = reference;
      } else {
        references.or(reference);
      }
    }

    return references;
  }
}
