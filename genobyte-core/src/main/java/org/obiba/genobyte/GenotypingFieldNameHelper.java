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
package org.obiba.genobyte;

import java.text.StringCharacterIterator;

/**
 * Methods to generate a field name for genotyping fields. The name will be generated from a combination of
 * the general field name and the record index to which it belongs. Any method playing with genotype field names
 * should use the methods provided here to make names, to ensure naming scheme consistency.
 * <p/>
 * The current naming scheme is: <ul>
 * <li>Underscore "_" characters in the middle of the template name are removed and the following charater is put in capital letter.</li>
 * <li>Underscore "_" character at the end of the template name remains.</li>
 * <li>If the field is a transposed one (meaning it be stored in a transposed store, see GenotypingStore Javadoc),
 *     append an underscore "_" followed by the record unique key value.</li>
 * </ul>
 */
public class GenotypingFieldNameHelper {

  /**
   * Generates a complete field name for a genotyping field. If the field is a transposed one, the resulting field name will be unique for
   * a given record.
   * @param field the name of the genotyping field. In the case of transposed fields, this is the name of the template that will be used to
   * generate a unique name per records.
   * @param key the unique key to the current record.
   * @return the complete genotyping field name, ready to be used in the genotyping store.
   */
  static public String generateFieldName(GenotypingField field, Object key) {
    return generateFieldName(field.getName(), key, field.isTransposed());
  }

  static public String generateFieldName(String pName, Object key, boolean isTransposed) {
    if(pName == null) {
      throw new NullPointerException("field cannot be null");
    }
    StringBuilder name = new StringBuilder();
    StringCharacterIterator sci = new StringCharacterIterator(pName);
    char c = sci.first();
    while(c != StringCharacterIterator.DONE) {
      if(c == '_') {
        c = sci.next();
        if(c != StringCharacterIterator.DONE) {
          name.append(Character.toUpperCase(c));
        } else {
          name.append('_');
        }
      } else {
        name.append(c);
      }
      c = sci.next();
    }
    if(isTransposed) {
      if(key == null) {
        throw new IllegalArgumentException(
            "Error generating name: key cannot be null for transposed field [" + pName + "]");
      }
      name.append('_').append(key.toString());
    }
    return name.toString();
  }

}
