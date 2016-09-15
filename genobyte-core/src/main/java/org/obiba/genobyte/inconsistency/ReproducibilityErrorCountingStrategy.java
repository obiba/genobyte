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
package org.obiba.genobyte.inconsistency;

/**
 * Handles computed reproducibility errors.
 *
 * @param <K> the type of the compared records key
 */
public interface ReproducibilityErrorCountingStrategy<K>
    extends InconsistencyCountingStrategy<ReproducibilityErrors<K>> {

  /**
   * Called by the {@link ReproducibilityErrorCalculator} for every comparison that produced at least one error.
   *
   * @param errors the reported errors
   */
  public void countInconsistencies(ReproducibilityErrors<K> errors);

}
