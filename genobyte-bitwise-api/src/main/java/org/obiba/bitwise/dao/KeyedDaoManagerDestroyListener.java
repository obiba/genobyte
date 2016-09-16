/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.obiba.bitwise.dao;

/**
 * Listener interface for events sent when a DaoManager is being destroyed.
 */
public interface KeyedDaoManagerDestroyListener {

  /**
   * Called during the destruction of com.ibatis.dao.client.DaoManager instances. This method
   * is called when the instance is no longer accessible through the <tt>KeyedDaoManager</tt>.
   */
  void destroying();

}
