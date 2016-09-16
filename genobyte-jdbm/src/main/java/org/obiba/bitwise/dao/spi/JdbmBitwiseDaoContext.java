/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.bitwise.dao.spi;

import org.obiba.bitwise.dao.impl.jdbm.*;

public class JdbmBitwiseDaoContext implements BitwiseDaoContext {

  @Override
  public String getName() {
    return "jdbm";
  }

  @Override
  public Class getTransaction() {
    return JdbmTransactionManager.class;
  }

  @Override
  public Class getBitVectorDtoDao() {
    return BitVectorDtoDaoJdbmImpl.class;
  }

  @Override
  public Class getFieldDtoDao() {
    return FieldDtoDaoJdbmImpl.class;
  }

  @Override
  public Class getDictionaryDtoDao() {
    return DictionaryDtoDaoJdbmImpl.class;
  }

  @Override
  public Class getBitwiseStoreDtoDao() {
    return BitwiseStoreDtoDaoJdbmImpl.class;
  }

}
