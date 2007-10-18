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
package org.obiba.bitwise.dao.impl.bdb;

import java.util.LinkedList;
import java.util.List;

public class Timer {

  public static final List<Timer> TIMERS = new LinkedList<Timer>();

  private String name_ = null;
  private String key_ = null;
  private long start_; 
  private long time_ = 0;
  
  public Timer(String name, String key) {
    super();
    name_ = name;
    key_ = key;
    TIMERS.add(this);
  }
  
  public String getName() {
    return name_;
  }
  
  public String getKey() {
    return key_;
  }
  
  public long getTime() {
    return time_;
  }

  public void start() {
    start_ = System.currentTimeMillis();
  }

  public void end() {
    time_ += System.currentTimeMillis() - start_;
  }
  
  static public void reset() {
    for (Timer timer : TIMERS) {
      timer.time_ = 0;
    }
  }
}
