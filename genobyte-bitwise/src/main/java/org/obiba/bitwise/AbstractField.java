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

import org.obiba.bitwise.dto.FieldDto;
import org.obiba.bitwise.query.QueryResult;

import java.util.Arrays;

public abstract class AbstractField {
  BitwiseStore store_ = null;

  protected FieldDto data_ = null;

  protected Dictionary dictionary_ = null;

  protected BitVector vectors_[] = null;      //All the BitVector objects which hold together the value of the field.

  protected BitVector nulls_ = null;

  /** A BitVector of zeroes used to represent the non-initialized vector(s) */
  private BitVector EMPTY = null;

  BitVector[] getBitVectors() {
    return vectors_;
  }

  final FieldDto getDto() {
    return data_;
  }

  /**
   * Returns the name of this instance of <code>Field</code>. Every field in a <code>BitwiseStore</code>
   * has a unique name that identifies it. 
   * @return the name of this <code>Field</code> instance.
   */
  final public String getName() {
    return data_.getName();
  }

  /**
   * Returns the number of records that can currently be holded in this <code>Field</code> object.
   * @return the maximum number of records that can currently be holded in this <code>Field</code>.
   */
  final public int getSize() {
    return data_.getSize();
  }

  /**
   * Increases the number of records that can be holded in this <code>Field</code> object.
   * @param size the new number of records that can be holded in the <code>Field</code>. This number
   * is the new total number of records, not the number of records to increase. It must be equal or
   * higher than the current maximum number of records.
   */
  public void grow(int size) {
    data_.setSize(size);
    for(int i = 0; i < vectors_.length; i++) {
      BitVector v = vectors_[i];
      if(v != null) {
        v.grow(size);
      }
    }
    nulls_ = null;
    EMPTY = null;
  }

  /**
   * Returns the dictionary used to encode values from their native type to the Bitwise format and vice versa. 
   * @return the dictionary object used by this <code>Field</code>. 
   */
  @SuppressWarnings("unchecked")
  public <T> Dictionary<T> getDictionary() {
    return dictionary_;
  }

  /**
   * Returns a <code>ResultVector</code> with ones for every index that has the specified value. If
   * the dictionary lookup fails, the resulting vector will be all zeroes. If the specified
   * value is null, the result will contain ones for every index for which the value is null.
   * @param value the value, encoded into bits, to be searched for in all records.
   * @return the result of the query.
   */
  public QueryResult query(BitVector query) {
    BitVector result = new BitVector(data_.getSize());
    result.setAll();
    if(query == null) {
      //Find records that hold the null value (i.e. all bits holding the value are set to zero)
      for(int i = 0; i < vectors_.length; i++) {
        result.andNot(safeVector(i));
      }
    } else {
      int qSize = query.size();
      for(int i = 0; i < qSize; i++) {
        if(query.get(i)) {
          result.and(safeVector(i));
        } else {
          result.andNot(safeVector(i));
        }
      }
    }
    return new ResultVector(result, resultFilter(query != null), store_.getDeleted());
  }

  /**
   * Runs a bit to bit comparison starting from the right between the
   * <code>query</code> BitVector and the field vectors, for as many bits as
   * there are in the <code>query</code> BitVector.
   * @param query is the bit sequence searched by the user.
   * @param variableLength is true if field can be padded with zeros at the end to represent the absence of data.
   * @return A <code>ResultVector</code> with ones for every index that has the specified value. If the dictionary
   * lookup fails, the vector will be all zeroes. If the specified value is null, the result will contain ones for
   * every index for which the value is null.
   */
  public QueryResult queryRight(BitVector query, boolean variableLength) {
    BitVector result = new BitVector(data_.getSize());
    result.clearAll();
    if(query == null) {
      //Find records that hold the null value (i.e. all bits holding the value are set to zero)
      for(int i = 0; i < vectors_.length; i++) {
        result.andNot(safeVector(i));
      }
    } else {
      int qSize = query.size();
      int bitCount = vectors_.length;

      //If the data can have variable length (if the whole data might be stored
      //in less bits than there are in the field), the comparison will be reran
      //with an offset from the right to deal with the fact that a zero at the
      //end of the field might represent the absence of data. If all the records
      //are using the whole field length, no need to iterate throught zero offsets.
      int iterCount = 1;
      if(variableLength) {
        iterCount = bitCount - qSize + 1;
      }

      //Indicate which records can still be considered at each offset iteration. For one record, as long as we encounter zeros,
      //we don't know if it represents data or the absence of data. After ecountering a one bit, we know that the previous bits
      //for that record represent data. 
      BitVector toProcess = new BitVector(data_.getSize());
      toProcess.setAll();

      //offset is the number of bits we must skip from the right
      int offset = 0;
      while((offset < iterCount) && (toProcess.count() != 0)) {
        BitVector iterResult = new BitVector(data_.getSize());    //Vector with the current offset iteration results
        iterResult.setAll();
        int areaLastBit = bitCount - offset -
            1;                //Position of the last bit in the current offset iteration
        toProcess.andNot(
            safeVector(areaLastBit));          //All records with a one bit shouldn't be checked in following offsets

        //Check that all bits in the checked area correspond to the query bits
        for(int i = 0; i < qSize; i++) {
          if(query.get(qSize - i - 1)) {
            iterResult.and(safeVector(areaLastBit - i));
          } else {
            iterResult.andNot(safeVector(areaLastBit - i));
          }
        }
        result.or(iterResult);
        offset++;
      }
    }
    return new ResultVector(result, resultFilter(query != null), store_.getDeleted());
  }

  /**
   * Returns a <code>ResultVector</code> with ones for every index that has a 
   * value between <code>from</code> and <code>to</code> (inclusive). If
   * the dictionary lookup fails, the resulting vector will be all zeroes.
   * @param value
   * @return the result of th query.
   */
  public QueryResult rangeQuery(BitVector qFrom, BitVector qTo) {
    if(qFrom == null || qTo == null) {
      return new ResultVector(new BitVector(data_.getSize()), resultFilter(true), store_.getDeleted());
    }

    int k = k(qFrom, qTo);

    BitVector N = N(qFrom, k);
    BitVector L = L(qFrom, k);
//    while(l-- != 0) {
//      System.out.print("))");
//    }
//    System.out.println("");
    BitVector M = M(qTo, k);
//    while(m-- != 0) {
//      System.out.print("))");
//    }
//    System.out.println("\n");

    return new ResultVector(N.and(L.or(M)), resultFilter(true), store_.getDeleted());
  }

  /**
   * By comparing values from this field with <code>f2</code>, this method returns a <code>BitVector</code>
   * with ones for every record for which the value differs.
   * @param f2 the <code>Field</code> to compare.
   * @return a BitVector with bits set for every value that is different in both fields
   */
  public QueryResult diff(AbstractField f2) {
    if(dictionary_.getName().equals(f2.getDictionary().getName()) == false) {
      throw new IllegalArgumentException(
          "Cannot diff field [" + this.getName() + "] and [" + f2.getName() + "]: dictionaries are not the same.");
    }
    BitVector result = new BitVector(data_.getSize());

    int d = dictionary_.dimension();
    for(int i = 0; i < d; i++) {
      BitVector v1 = new BitVector(this.safeVector(i));
      BitVector v2 = f2.safeVector(i);
      result.or(v1.xor(v2));
    }
    return new ResultVector(result, resultFilter(true), store_.getDeleted());
  }

  /**
   * Set the value <code>o</code> at the index <code>record</code>. If the value is not null, it is looked up in 
   * the field's dictionary and its bit value is stored. If the value is null, the field will store the null 
   * representation at the index.
   * @param record the index at which to store the value.
   * @param value the value to store.
   */
  public void setValue(int record, BitVector value) {
    if(record >= getSize()) {
      throw new IndexOutOfBoundsException("Record index [" + record + "] >= " + getSize());
    }

    //Value to put at index "record" is the null value, represented by zeros in all BitVectors.
    if(value == null) {
      for(int i = 0; i < vectors_.length; i++) {
        BitVector v = vectors_[i];
        if(v != null) {
          v.clear(record);
        }
      }
      internalGetNulls().set(record);
      return;
    }

    //Value to put at index "record" is a non-null value.
    int dimensions = value.size();
    internalGetNulls().clear(record);
    for(int i = 0; i < dimensions; i++) {
      BitVector v = null;
      if(i < vectors_.length) {
        v = vectors_[i];
      }
      if(value.get(i)) {
        if(v == null) {
          // Either the dictionary grew or it's the first time we are inserting a one in this vector
          createDimension(i);
          v = vectors_[i];
        }
        v.set(record);
      } else {
        if(i < vectors_.length && v != null) {
          v.clear(record);
        }
      }
    }
  }

  /**
   * Gets the value found at a specific record index.
   * @param record the index of the record to be fetched.
   * @return a <code>BitVector</code> with the value encoded in a BitVector.
   */
  public BitVector getValue(int record) {
    if(record >= getSize()) {
      throw new IndexOutOfBoundsException("Record index [" + record + "] >= " + getSize());
    }

    // Quick nullness check.
    if(internalGetNulls().get(record)) {
      return null;
    }

    BitVector v = new BitVector(vectors_.length);
    for(int i = 0; i < vectors_.length; i++) {
      BitVector vector = vectors_[i];
      if(vector != null && vector.get(record)) {
        v.set(i);
      }
    }
    return v;
  }

  /**
   * Copy the value from sourceIndex in sourceField into this field at targetIndex.
   * @throws IllegalArgumentException when this field and the source field don't have the same dictionary.
   */
  public void copyValue(int targetIndex, int sourceIndex, AbstractField sourceField) {
    for(int i = 0; i < this.vectors_.length; i++) {
      BitVector targetVector = this.vectors_[i];
      BitVector sourceVector = null;
      // Source vector may be missing for dynamic dictionaries
      if(i < sourceField.vectors_.length) {
        sourceVector = sourceField.vectors_[i];
      }

      if(sourceVector != null && sourceVector.get(sourceIndex)) {
        if(targetVector == null) {
          createDimension(i);
          targetVector = vectors_[i];
        }
        targetVector.set(targetIndex);
      } else {
        if(targetVector != null) {
          targetVector.clear(targetIndex);
        }
      }
    }

    // Clear the null vector instead of checking nullness
    this.nulls_ = null;
  }

  /**
   * Returns true if the value for recrod <code>record</code> is null.
   * @param record the record index to test
   * @return true if the value for recrod <code>record</code> is null.
   */
  public boolean isNull(int record) {
    return internalGetNulls().get(record);
  }

  /**
   * Returns a String object representing various information about this BitVector. More exactly, what will be produced
   * is a string following this model: <code>Field[_fieldName_] dimension[_dimension_] size[_size_] bits[_BitVectors_index_encoded_to_Strings_] vectors[_BitVector_encoded_to_Strings_]</code>.
   */
  public String toString() {
    StringBuffer b = new StringBuffer();
    b.append(this.getClass().getName()).append("[").append(data_.getName()).append("] dimension[")
        .append(data_.getBitIndex().length).append("] size[").append(data_.getSize()).append("] bits[")
        .append(Arrays.toString(data_.getBitIndex())).append("] vectors[").append(Arrays.toString(vectors_))
        .append("]");
    return b.toString();
  }

  public abstract boolean equals(Object o);

  public abstract int hashCode();

  /**
   * Initializes the dimension <code>d</code> of the field.
   * @param d the index of the bit vector to initialize
   */
  private void createDimension(int d) {
    createDimension(d, new BitVector(data_.getSize()));
  }

  /**
   * Initializes the dimension <code>d</code> of the field with the specified {@link BitVector}
   * @param d the index of the bit vector to initialize
   * @param v the new dimension's vector
   */
  private void createDimension(int d, BitVector v) {
    if(vectors_.length <= d) {
      BitVector tempVectors[] = new BitVector[d + 1];
      System.arraycopy(vectors_, 0, tempVectors, 0, vectors_.length);
      vectors_ = tempVectors;

      long[] tempIndex = new long[d + 1];
      Arrays.fill(tempIndex, -1);
      System.arraycopy(data_.getBitIndex(), 0, tempIndex, 0, data_.getBitIndex().length);
      data_.setBitIndex(tempIndex);
    }
    vectors_[d] = v;
  }

  /**
   * Finds k such that f(k) != t(k) and f(i) == t(i) for every i > k
   * @param f the lower bound of the range query (inclusive)
   * @param t the upper bound of the range query (inclusive)
   * @return k
   */
  private int k(BitVector f, BitVector t) {
    BitVector v = new BitVector(f);
    // XOR : sets different bits to 1
    v.xor(t);

    // find k such that a(k) != b(k) and a(i) == b(i) for every i > k
    // k is the last set bit in vector v
    int k = 0;
    for(int i = k; i < v.size(); i++) {
      if(v.get(i)) {
        // bits are different
        k = i;
      }
    }
    return k;
  }

  /**
   * Returns N as described in the Taxir paper.
   * @param a the lower bound value of the range query
   * @param k the value returned by <code>k</code>
   * @return the N characteristic function
   */
  private BitVector N(BitVector a, int k) {
//    System.out.print("N = ONES");
    BitVector result = new BitVector(data_.getSize());
    result.setAll();
    int n = dictionary_.dimension();
    for(int i = k + 1; i < n; i++) {
      if(a.get(i)) {
//        System.out.print(" AND C" + i);
        result.and(safeVector(i));
      } else {
//        System.out.print(" AND NOT C" + i);
        result.andNot(safeVector(i));
      }
    }
//    System.out.println("");
    return result;
  }

  /**
   * Returns L as described in the Taxir paper.
   * @param a the lower bound value of the range query
   * @param k the value returned by <code>k</code>
   * @return the L characteristic function
   */
  private BitVector L(BitVector a, int k) {
    // from low order to high order bits
    // find q such that a(q) == 1, q < k and a(i) == 0 for all i < q
    int q = a.nextSetBit(0);
    if(q == -1 || q > k) {
      // special case
      q = 0;
    }

//    System.out.println("q = " + q);

//    System.out.print("L = NOT C" + k);
    BitVector L = new BitVector(safeVector(k));
    return L.not().and(rL(a, k - 1, q));
  }

  /**
   * Recursive method used to construct L
   * @param a the lower bound value of the range query
   * @param i the bit being tested
   * @param q the last bit to test
   * @return part of the L characteristic function for bit i
   */
  private BitVector rL(BitVector a, int i, int q) {
    if(i < q) {
//      System.out.print(" AND ONES");
      return new BitVector(data_.getSize()).setAll();
    }
    BitVector t = new BitVector(safeVector(i));

    if(a.get(i)) {
//      System.out.print(" AND C" + i);
      return t.and(rL(a, i - 1, q));
    }
//    l++;
//    System.out.print(" AND (C" + i + " OR (NOT C" + i);
    BitVector t1 = new BitVector(safeVector(i));
    return t.or(t1.not().and(rL(a, i - 1, q)));
  }

  /**
   * Returns M as described in the Taxir paper.
   * @param b the upper bound value of the range query
   * @param k the value returned by <code>k</code>
   * @return the M characteristic function
   */
  private BitVector M(BitVector b, int k) {
    // from high order to low order bits
    // find q such that b(q) == 0 and b(i) == 1 for all i < q
    int q = k;
    for(int i = k; i >= 0; i--) {
      if(b.get(i) == false) {
        q = i;
      }
    }
//    System.out.println("q = " + q);

//    System.out.print("M = C" + k);
    BitVector M = new BitVector(safeVector(k));
    return M.and(rM(b, k - 1, q));
  }

  /**
   * Recursive method used to construct M
   * @param b the upper bound value of the range query
   * @param i the bit being tested
   * @param q the last bit to test
   * @return part of the M characteristic function for bit i
   */
  private BitVector rM(BitVector b, int i, int q) {
    if(i < q) {
//      System.out.print(" AND ONES");
      return new BitVector(data_.getSize()).setAll();
    }
    BitVector t = new BitVector(safeVector(i));
    if(b.get(i) == false) {
//      System.out.print(" AND NOT C" + i);
      return t.not().and(rM(b, i - 1, q));
    }
//    m++;
//    System.out.print(" AND (NOT C" + i + " OR (C" + i);
    BitVector t1 = new BitVector(safeVector(i));
    return t.not().or(t1.and(rM(b, i - 1, q)));
  }

  /**
   * Creates a BitVector used to filter out nulls records. When filterNulls is true
   * the resulting vector will include records that contain the null value, otherwise the
   * resulting vector will be filled with zeros.
   * @return a <code>BitVector</code> used to filter query results.
   */
  BitVector resultFilter(boolean filterNulls) {
    if(filterNulls == true) {
      return internalGetNulls();
    }
    // Return an empty filter (all zeroes)
    return new BitVector(getSize());
  }

  /**
   * Get all records for which its value is null in this field.
   * @return BitVector with all records with a null value in this field.
   */
  public BitVector getNulls() {
    // Return a copy of the internal null vector so that external objects may not modify our internal state.
    return new BitVector(internalGetNulls());
  }

  /**
   * Internal method to lazily instantiate the null vector and return it.
   * @return the internal null vector
   */
  private BitVector internalGetNulls() {
    if(nulls_ == null) {
      nulls_ = new BitVector(getSize());
      nulls_.setAll();
      for(int i = 0; i < vectors_.length; i++) {
        nulls_.andNot(safeVector(i));
      }
    }
    return nulls_;
  }

  /**
   * Returns the bit vector i, or a vector of 0s if i is not initialized
   * @param i the vector index
   * @return bit vector i, or a vector of 0s if i is not initialized
   */
  BitVector safeVector(int i) {
    if(i >= vectors_.length || vectors_[i] == null) {
      if(EMPTY == null) {
        EMPTY = new BitVector(data_.getSize());
      }
      return EMPTY;
    }
    return vectors_[i];
  }

  /**
   * Copies the values from the source field into this one. The values considered for copying
   * can be masked using a {@link QueryResult}: only the records with their index set in the mask
   * will be copied.
   */
  public void copyValues(AbstractField pSource, QueryResult maskVector) {
    validateSourceField(pSource);

    BitVector mask = maskVector.bits();

    if(getDictionary().equals(pSource.getDictionary()) == true) {
      for(int i = 0; i < pSource.vectors_.length; i++) {
        BitVector sourceVector = pSource.safeVector(i);
        BitVector destVector = safeVector(i);
        BitVector newVector = new BitVector(sourceVector);

        //If there was no original value in the destination vector, copy desired values from source vector and leave the rest null
        if(destVector == EMPTY) {
          this.vectors_[i] = newVector.and(mask);
        }
        //If there were values in the destination vector, keep them when the source value is filtered.
        else {
          //If mask allows record copy, use source value. Otherwise, use existing value.
          // destVector AND NOT mask : sets all destination bits to 0
          // newVector AND mask : sets all undesired source bits to 0
          // ORing the results will set the destination bits to 1 where all source bits are 1
          destVector.andNot(mask).or(newVector.and(mask));
          this.vectors_[i] = destVector;
        }
      }
    } else {
      Dictionary sourceDict = pSource.getDictionary();
      Dictionary thisDict = getDictionary();
      for(int i = maskVector.next(0); i != -1; i = maskVector.next(i + 1)) {
        Object value = sourceDict.reverseLookup(pSource.getValue(i));
        setValue(i, thisDict.lookup(value));
      }
    }

    // Clear the null vector instead of checking nullness
    this.nulls_ = null;
  }

  /**
   * Copies the values from the source field into this one. This will overwrite all values in the current field 
   * with the ones from the original field. A copy of the original values is made so the source field may be modified
   * without affecting this one.
   * @param pSource the field from which the values are copied from. 
   */
  public void copyValues(AbstractField pSource) {
    validateSourceField(pSource);
    if(getDictionary().equals(pSource.getDictionary()) == false) {
      throw new IllegalArgumentException(
          "Destination field's dictionary is not equivalent to source's dictionary. Cannot copy values from source field.");
    }
    for(int i = 0; i < pSource.vectors_.length; i++) {
      BitVector sourceVector = pSource.safeVector(i);

      if(i >= this.vectors_.length) createDimension(i, new BitVector(sourceVector));
      else this.vectors_[i] = new BitVector(sourceVector);
    }

    if(pSource.vectors_.length < this.vectors_.length) {
      for(int i = pSource.vectors_.length; i < this.vectors_.length; i++) {
        this.vectors_[i] = new BitVector(getSize());
      }
    }

    // Clear the null vector instead of checking nullness
    this.nulls_ = null;
  }

  /**
   * Makes sure that it is possible to copy content from the source field to this field.
   * @param pSource the source field to validate.
   */
  private void validateSourceField(AbstractField pSource) {
    if(pSource == null) {
      throw new IllegalArgumentException("The source field cannot be null.");
    }
    //Make sure they have the same capacity.
    if(this.getSize() != pSource.getSize()) {
      throw new IllegalArgumentException("The source and destination fields must have the same size.");
    }
  }

}
