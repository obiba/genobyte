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

import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.Field;
import org.obiba.bitwise.VolatileField;
import org.obiba.bitwise.query.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Offers basic methods to <tt>StatsDigester</tt> implementations to persist data in a <tt>GenotypingStore</tt>.
 */
public abstract class PersistenceStatsDigester implements StatsDigester {

  private final Logger log = LoggerFactory.getLogger(PersistenceStatsDigester.class);

  /**
   * Persists a field from the <tt>StatsPool</tt> into the designated store field.
   *
   * @param pPool       the pool from where to extract the field to be copied.
   * @param pPoolField  the name of the field to be copied.
   * @param pStoreField the name of the destination field in the store.
   * @param pMask       the mask expressing which records in the field should be persisted.
   */
  protected void persistField(StatsPool<?, ?> pPool, String pPoolField, String pStoreField, QueryResult pMask) {
    // Get calculated field from pool
    VolatileField field = (VolatileField) pPool.getPool().get(pPoolField);
    if (field == null) {
      return;
    }

    //Get store from pool
    BitwiseStore store = pPool.getGenotypingRecordStore().getStore();

    log.debug("Persisting [{}] values for field [{}] in store [{}].",
        new Object[]{pMask.count(), pPoolField, store.getName()});

    // Copy values from VolatileField to persisted field.
    Field destination = store.getField(pStoreField);
    if (destination != null) {
      destination.copyValues(field, pMask);
    }
  }

}
