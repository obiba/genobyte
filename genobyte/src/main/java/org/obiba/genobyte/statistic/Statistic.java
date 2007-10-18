/*******************************************************************************
 * Copyright 2007(c) G�nome Qu�bec. All rights reserved.
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

/**
 * Defines the basic interface that all calculated value in a {@link StatsPool} should offer.
 */
public interface Statistic {

  /**
   * Returns the list of parameter names needed by the statistic, to be fetched from the data pool. 
   * @return the list of required input parameters.
   */
  public List<String> getInputParams();


  /**
   * Returns the list of field names needed by the statistic, to be fetched from the <tt>GenotypingStore</tt> fields.
   * @return the list of required input fields.
   */
  public List<String> getInputFields();


  /**
   * Returns the list of parameters generated by the calculation of this statistic.
   * @return the list of generated parameters.
   */
  public List<String> getOutputParams();

}
