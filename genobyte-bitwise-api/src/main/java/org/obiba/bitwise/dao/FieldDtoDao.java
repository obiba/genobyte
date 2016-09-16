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

import org.obiba.bitwise.dao.CrudDao;
import org.obiba.bitwise.dto.FieldDto;

import java.util.List;

/**
 * DAO interface for persisting instances of <tt>FieldDto</tt>.
 */
public interface FieldDtoDao extends CrudDao<FieldDto, String> {

  /**
   * Returns all the existing unique keys.
   *
   * @return a List of unique keys that are currently persisted.
   */
  List<String> keys();

  /**
   * Returns all the existing transfer objects.
   *
   * @return a List of currently persisted transfer objects
   */
  List<FieldDto> values();

}
