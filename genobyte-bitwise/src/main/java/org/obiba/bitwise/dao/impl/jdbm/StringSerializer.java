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

import jdbm.helper.Serializer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

class StringSerializer implements Serializer {

  private static final long serialVersionUID = -6666635301794063338L;

  private static final String STORED_ENCODING = "UTF-8";

  public Object deserialize(byte[] bytes) throws IOException {
    return new String(bytes, STORED_ENCODING);
  }

  public byte[] serialize(Object str) throws IOException {
    if(str == null) {
      throw new NullPointerException("Argument str cannot be null");
    }
    try {
      return ((String) str).getBytes(STORED_ENCODING);
    } catch(UnsupportedEncodingException e) {
      throw new JdbmRuntimeException(e);
    }
  }

}
