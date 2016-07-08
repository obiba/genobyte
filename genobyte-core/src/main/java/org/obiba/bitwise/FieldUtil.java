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
package org.obiba.bitwise;

import java.util.Arrays;
import java.util.List;

import org.obiba.bitwise.dao.BitVectorDtoDao;
import org.obiba.bitwise.dao.FieldDtoDao;
import org.obiba.bitwise.dao.KeyedDaoManager;
import org.obiba.bitwise.dto.BitVectorDto;
import org.obiba.bitwise.dto.FieldDto;

/**
 * Utility class for persisting <code>Field</code> instances
 *
 * @author plaflamm
 */
class FieldUtil {

  BitwiseStore store_ = null;

  FieldUtil(BitwiseStore store) {
    super();
    store_ = store;
  }

  /**
   * List the name of all existing fields in the BitwiseStore.
   *
   * @return a <code>List&lt;String&gt;</code> of field names.
   */
  List<String> list() {
    FieldDtoDao fieldDtoDao = (FieldDtoDao) KeyedDaoManager.getInstance(store_.getDaoKey()).getDao(FieldDtoDao.class);
    return fieldDtoDao.keys();
  }

  /**
   * Clears (sets to 0) the bits specified by the <code>clear</code> <code>BitVector</code> 
   * for every field in the <code>BitwiseStore</code>
   *
   * @param clear a <code>BitVector</code> with bits set for every bit to clear
   */
  void clear(BitVector clear) {
    FieldDtoDao fieldDtoDao = (FieldDtoDao) KeyedDaoManager.getInstance(store_.getDaoKey()).getDao(FieldDtoDao.class);
    BitVectorDtoDao bitVectorDtoDao = (BitVectorDtoDao) KeyedDaoManager.getInstance(store_.getDaoKey())
        .getDao(BitVectorDtoDao.class);

    BitVector clearMask = new BitVector(clear);
    clearMask.not();

    List<FieldDto> values = fieldDtoDao.values();
    for(FieldDto dto : values) {
      long bits[] = dto.getBitIndex();
      for(int i = 0; i < bits.length; i++) {
        long id = bits[i];
        if(id != -1) {
          BitVector v = BitVectorUtil.toVector(bitVectorDtoDao.load(id));
          v.and(clearMask);
          BitVectorDto vectorDto = BitVectorUtil.toDto(id, v);
          bitVectorDtoDao.save(vectorDto);
        }
      }
    }
  }

  /**
   * Open the Field instance of the specified name.
   *
   * @param name the name of the field to open
   * @return the <code>Field</code> instance
   */
  Field open(String name) {
    FieldDtoDao fieldDtoDao = (FieldDtoDao) KeyedDaoManager.getInstance(store_.getDaoKey()).getDao(FieldDtoDao.class);
    BitVectorDtoDao bitVectorDtoDao = (BitVectorDtoDao) KeyedDaoManager.getInstance(store_.getDaoKey())
        .getDao(BitVectorDtoDao.class);
    FieldDto dto = fieldDtoDao.load(name);
    if(dto == null) {
      return null;
    }
    Dictionary dict = store_.getDictionary(dto.getDictionaryName());

    long bits[] = dto.getBitIndex();
    BitVector vectors[] = new BitVector[bits.length];
    for(int i = 0; i < bits.length; i++) {
      long bitIndex = bits[i];
      if(bitIndex != -1) {
        vectors[i] = BitVectorUtil.toVector(bitVectorDtoDao.load(bitIndex));
        if(vectors[i].size() != dto.getSize()) {
          throw new IllegalStateException(
              "Cannot open field [" + name + "]: vector [" + i + "] size [" + vectors[i].size() +
                  "] does not match field size [" + dto.getSize() + "]");
        }
      }
    }
    return new Field(store_, dto, dict, vectors);
  }

  /**
   * Creates a <code>Field</code> with the specified attributes.
   * The field returned is dirty and not persisted, use the <code>save</code> method to persist it.
   *
   * @param name the name of the field to create
   * @param dictName the associated dictionary name
   * @param capacity the initial field capacity
   * @return a new <code>Field</code> instance
   * @throws IllegalStateException when the field name already exists
   */
  Field create(String name, String dictName, int capacity) {
    FieldDtoDao fieldDtoDao = (FieldDtoDao) KeyedDaoManager.getInstance(store_.getDaoKey()).getDao(FieldDtoDao.class);
    if(fieldDtoDao.load(name) != null) {
      throw new IllegalStateException("Field [" + name + "] already exists");
    }
    Dictionary dict = store_.getDictionary(dictName);
    if(dict == null) {
      throw new IllegalStateException(
          "Cannot create field=[" + name + "]: dictionary=[" + dictName + "] does not exist.");
    }
    int bits = dict.dimension();
    long bitIndexes[] = new long[bits];
    Arrays.fill(bitIndexes, -1);
    FieldDto dto = new FieldDto();
    dto.setName(name);
    dto.setSize(capacity);
    dto.setBitIndex(bitIndexes);
    dto.setDictionaryName(dictName);
    Field newField = new Field(store_, dto, dict, new BitVector[bits]);
    newField.setDirty(true);
    return newField;
  }

  /**
   * Persists the <code>Field</code> <code>f</code> if it is dirty
   *
   * @param f the <code>Field</code> to persist
   */
  void save(Field f) {
    if(f.isDirty()) {
      FieldDtoDao fieldDtoDao = (FieldDtoDao) KeyedDaoManager.getInstance(store_.getDaoKey()).getDao(FieldDtoDao.class);
      BitVectorDtoDao bitVectorDtoDao = (BitVectorDtoDao) KeyedDaoManager.getInstance(store_.getDaoKey())
          .getDao(BitVectorDtoDao.class);
      BitVector[] vectors = f.getBitVectors();
      for(int i = 0; i < vectors.length; i++) {
        BitVector v = vectors[i];
        if(v != null) {
          if(v.size() != f.getSize()) {
            throw new IllegalStateException(
                "Field [" + f.getName() + "] vector id [" + f.getDto().getBitIndex()[i] + "] size [" + v.size() +
                    "] does not match field size [" + f.getSize() + "]");
          }
          BitVectorDto dto = BitVectorUtil.toDto(f.getDto().getBitIndex()[i], v);
          if(dto.getId() == -1) {
            bitVectorDtoDao.create(dto);
            if(dto.getId() <= 0) throw new IllegalStateException(
                "Invalid unique key returned after persisting bitvector [" + dto.getId() + "]");
            f.getDto().getBitIndex()[i] = dto.getId();
          } else {
            bitVectorDtoDao.save(dto);
          }
        }
      }
      fieldDtoDao.save(f.getDto());
      f.setDirty(false);
    }
  }

  void delete(String name) {
    BitVectorDtoDao bitVectorDtoDao = (BitVectorDtoDao) KeyedDaoManager.getInstance(store_.getDaoKey())
        .getDao(BitVectorDtoDao.class);
    FieldDtoDao fieldDtoDao = (FieldDtoDao) KeyedDaoManager.getInstance(store_.getDaoKey()).getDao(FieldDtoDao.class);
    FieldDto dto = fieldDtoDao.load(name);
    if(dto != null) {
      for(long vectorIndex : dto.getBitIndex()) {
        if(vectorIndex != -1) bitVectorDtoDao.delete(vectorIndex);
      }
      fieldDtoDao.delete(name);
    }
  }
}
