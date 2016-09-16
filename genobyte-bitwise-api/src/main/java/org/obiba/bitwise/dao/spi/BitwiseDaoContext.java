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

/**
 * Bitwise DAO configuration interface.
 */
public interface BitwiseDaoContext {

  String getName();

  Class getTransaction();

  Class getBitVectorDtoDao();

  Class getFieldDtoDao();

  Class getDictionaryDtoDao();

  Class getBitwiseStoreDtoDao();

}
