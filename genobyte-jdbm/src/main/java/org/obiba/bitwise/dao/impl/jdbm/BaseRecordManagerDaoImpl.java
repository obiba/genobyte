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
package org.obiba.bitwise.dao.impl.jdbm;

import com.ibatis.dao.client.DaoManager;
import jdbm.RecordManager;
import org.obiba.bitwise.dao.impl.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class BaseRecordManagerDaoImpl<T> extends BaseDaoJdbmImpl {

  static final Logger log = LoggerFactory.getLogger(BaseRecordManagerDaoImpl.class);

  protected boolean enableTiming_ = false;

  protected Timer timer_ = null;

  public BaseRecordManagerDaoImpl(DaoManager mgr) {
    super(mgr);
  }

  abstract protected String getManagerName();

  protected boolean managerExists() {
    return getContext().managerExists(getManagerName());
  }

  protected RecordManager getManager() {
    return getContext().getManager(getManagerName());
  }

}
