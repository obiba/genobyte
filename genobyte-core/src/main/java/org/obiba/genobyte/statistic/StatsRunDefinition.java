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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The statistics calculation controller, which defines what will be computed and in which order.
 */
//TODO: Improve this description.
public class StatsRunDefinition {
  List<Iteration> statByLevel_ = new LinkedList<Iteration>();

  Map<String, Integer> poolDictionary_ = new HashMap<String, Integer>();

  /**
   * Adds a <tt>RecordStatistic</tt> statistic to the calculation run.
   *
   * @param pStat
   * @param pFieldSources
   */
  public void addStatistic(RecordStatistic pStat) {
    int itNumber = chooseIteration(pStat);
    Iteration it = getIteration(itNumber);
    it.addRecordStats(pStat);
    addToPoolDictionary(pStat, itNumber);
  }

  /**
   * Adds a <tt>FieldStatistic</tt> statistic to the calculation run.
   *
   * @param pStat
   * @param pFieldSources
   */
  public void addStatistic(FieldStatistic pStat) {
    int itNumber = chooseIteration(pStat);
    Iteration it = getIteration(itNumber);
    it.addFieldStat(pStat);
    addToPoolDictionary(pStat, itNumber);
  }

  /**
   * Determines the iteration level of the statistic from the list of parameters it fetches from the pool.
   *
   * @param pPoolSources the list of parameters needed from the pool.
   * @return the iteration level required to be able to get these parameters.
   */
  protected int chooseIteration(Statistic pStat) {
    List<String> input = pStat.getInputParams();
    int maxLevel = 0;
    for (String param : input) {
      if (poolDictionary_.containsKey(param)) {
        //The statistic has dependencies calculated by other Statistic objects.
        int currentLevel = poolDictionary_.get(param);
        if (maxLevel <= currentLevel) {
          //The newly added stat will be one iteration higher than the highest iteration dependency.
          maxLevel = currentLevel + 1;
        }
      }
    }

    return maxLevel;
  }

  private void addToPoolDictionary(Statistic pStat, Integer pIter) {
    for (String outputParam : pStat.getOutputParams()) {
      if (poolDictionary_.containsKey(outputParam)) {
        throw new RuntimeException("Duplicate parameter specified by Statistic output.");
      }
      poolDictionary_.put(outputParam, pIter);
    }

  }

  private Iteration getIteration(int pIter) {
    Iteration it;

    //The iteration level of the new stat hasn't been created yet.
    if (statByLevel_.size() <= pIter) {
      it = new Iteration();
      statByLevel_.add(pIter, it);
    }
    //Iteration already exists. Fetch it.
    else {
      it = statByLevel_.get(pIter);
    }

    return it;
  }

  /**
   * Returns all statistics using transposed fields for one iteration.
   */
  public List<RecordStatistic> getRecordStats(Integer pIteration) {
    return statByLevel_.get(pIteration).getRecordStat();
  }

  /**
   * Returns all statistics using non-transposed fields for one iteration.
   */
  public List<FieldStatistic> getFieldStats(Integer pIteration) {
    return statByLevel_.get(pIteration).getFieldStats();
  }

  /**
   * Returns the number of the highest iteration level in this <tt>StatsRunDefinition</tt>.
   *
   * @return
   */
  public int getMaxIteration() {
    return statByLevel_.size() - 1;
  }

  /**
   * Organizes statistics in a run iteration.
   */
  private class Iteration {
    private List<RecordStatistic> recordStat_;

    private List<FieldStatistic> fieldStat_;

    public Iteration() {
      recordStat_ = new LinkedList<RecordStatistic>();
      fieldStat_ = new LinkedList<FieldStatistic>();
    }

    public List<RecordStatistic> getRecordStat() {
      return recordStat_;
    }

    public List<FieldStatistic> getFieldStats() {
      return fieldStat_;
    }

    public void addRecordStats(RecordStatistic pStat) {
      recordStat_.add(pStat);
    }

    public void addFieldStat(FieldStatistic pStat) {
      fieldStat_.add(pStat);
    }
  }

}
