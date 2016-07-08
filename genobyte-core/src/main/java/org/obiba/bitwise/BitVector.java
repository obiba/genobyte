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
package org.obiba.bitwise;

import java.util.Arrays;

/**
 * Holds a vector of bits and offers many convenient methods to manipulate it.
 */
public final class BitVector implements Comparable {

  /** Bytes used to hold the bits */
  private long[] bits_;
  /** Bitvector's capacity */
  private int size_;
  /** Number of set bits */
  private int count_ = -1;
  /** Used to clear trailing (extra) bits in the last byte */
  private long tailMask_ = 0l;

  
  /** 
   * Constructs a vector capable of holding <code>n</code> bits. After construction, all n bits are
   * initialized to zero. 
   * @param n the number of bits to hold in this vector
   */
  public BitVector(int n) {
    size_ = n;
    bits_ = new long[(size_ >> 6) + 1];
    // We don't need to fill the bytes with zeroes since the VM does that for us (Java Reference 4.5.5)
//    Arrays.fill(bits, (byte)0x00);
    tailMask_ = tailMask();
  }

  
  /**
   * Constructs a <tt>BitVector</tt> and copies the content of another <tt>BitVector</tt>.
   * @param v the vector from which values should be copied.
   */
  public BitVector(BitVector v) {
    size_ = v.size_;
    bits_ = new long[v.bits_.length];
    System.arraycopy(v.bits_, 0, bits_, 0, bits_.length);
    tailMask_ = tailMask();
  }

  
  /**
   * Internal constructor use to initialize an instance directly.
   * @param size the size of the vector.
   * @param bits the bit array to use.
   */
  BitVector(int size, long[] bits) {
    bits_ = bits;
    size_ = size;
    tailMask_ = tailMask();
  }

  
  /**
   * Returns the array of bits this vector holds.
   * @return the array of bits this vector holds.
   */
  long[] getBits() {
    return bits_;
  }

  
  /**
   * Grows this <code>BitVector</code> to accomodate <code>newSize</code> bits. All new bits are initialized to 0.
   * The value for <code>newSize</code> must be larger or equal to the original size.
   * @param newSize the size of the <code>BitVector</code> after the method call.
   * @return this
   * @throws IllegalArgumentException when <code>newSize</code> is smaller than the current size.
   */
  public BitVector grow(int newSize) {
    if(newSize < size_) {
      throw new IllegalArgumentException("Cannot grow: the argument newSize is smaller than the original size.");
    }

    size_ = newSize;
    tailMask_ = tailMask();

    int newLength = (newSize >> 6) + 1;
    if(newLength == bits_.length) {
      return this;
    }

    long[] tmp = new long[newLength];
    System.arraycopy(bits_, 0, tmp, 0, bits_.length);
    // We don't need to fill the remaining bytes with zeroes since the VM does that for us (Java Reference 4.5.5)
//    Arrays.fill(tmp, bits.length, tmp.length, (byte)0x00);
    bits_ = tmp;
    return this;
  }

  
  /**
   * Computes the bitwise AND operation on two BitVectors. The BitVectors have to be of the same dimension. This method
   * does not guarantee any specific behaviour if BitVectors operands are of different dimensions. A possible but not guaranteed
   * behaviour is to throw an <b>ArrayIndexOutOfBoundsException</b>.  
   * @param v is the second BitVector operand for this operation, along with this BitVector.
   * @return a BitVector that is the result of (this & v).
   */
  public final BitVector and(BitVector v) {
    int end = bits_.length;
    for (int i = 0; i < end; i++)
      bits_[i] &= v.bits_[i];
    count_ = -1;
    return this;
  }

  
  /**
   * Computes the bitwise NOT operation on this BitVector.
   * @return a BitVector that is the result of (~v).
   */
  public final BitVector not() {
    int end = bits_.length;
    for (int i = 0; i < end; i++)
      bits_[i] = ~bits_[i];
    clearTrailingBits();
    count_ = -1;
    return this;
  }

  
  /**
   * Computes the bitwise AND NOT operation on two BitVectors. The BitVectors have to be of the same dimension. This method
   * does not guarantee any specific behaviour if BitVectors operands are of different dimensions. A possible but not guaranteed
   * behaviour is to throw an <b>ArrayIndexOutOfBoundsException</b>.  
   * @param v is the second BitVector operand for this operation, along with this BitVector.
   * @return a BitVector that is the result of (this & ~v).
   */
  public final BitVector andNot(BitVector v) {
    int end = bits_.length;
    for (int i = 0; i < end; i++)
      bits_[i] &= ~v.bits_[i];
    count_ = -1;
    return this;
  }

  
  /**
   * Computes the bitwise OR operation on two BitVectors. The BitVectors have to be of the same dimension. This method
   * does not guarantee any specific behaviour if BitVectors operands are of different dimensions. A possible but not guaranteed
   * behaviour is to throw an <b>ArrayIndexOutOfBoundsException</b>.  
   * @param v is the second BitVector operand for this operation, along with this BitVector.
   * @return a BitVector that is the result of (this | v).
   */
  public final BitVector or(BitVector v) {
    int end = bits_.length;
    for (int i = 0; i < end; i++)
      bits_[i] |= v.bits_[i];
    count_ = -1;
    return this;
  }

  
  /**
   * Computes the bitwise XOR operation on two BitVectors. The BitVectors have to be of the same dimension. This method
   * does not guarantee any specific behaviour if BitVectors operands are of different dimensions. A possible but not guaranteed
   * behaviour is to throw an <b>ArrayIndexOutOfBoundsException</b>.  
   * @param v is the second BitVector operand for this operation, along with this BitVector.
   * @return a BitVector that is the result of (this ^ v).
   */
  public final BitVector xor(BitVector v) {
    int end = bits_.length;
    for (int i = 0; i < end; i++)
      bits_[i] ^= v.bits_[i];
    clearTrailingBits();
    count_ = -1;
    return this;
  }

  
  /**
   * Sets the value of a specified bit to one.
   * @param bit the bit to set.
   */
  public final void set(int bit) {
    bits_[bit >> 6] |= 1l << (bit & 63);
    count_ = -1;
  }

  
  /**
   * Sets the value of all bits to one.
   * @return this <tt>BitVector</tt>.
   */
  public final BitVector setAll() {
    Arrays.fill(bits_, 0xFFFFFFFFFFFFFFFFl);
    clearTrailingBits();
    count_ = size_;
    return this;
  }

  
  /**
   * Sets the value of a specified bit to zero.
   * @param bit the bit to set.
   */
  public final void clear(int bit) {
    bits_[bit >> 6] &= ~(1l << (bit & 63));
    count_ = -1;
  }
  
  
  /**
   * Sets the value of every bit to zero.
   * @return this <tt>BitVector</tt>.
   */
  public final BitVector clearAll() {
    Arrays.fill(bits_, 0x00l);
    count_ = 0;
    return this;
  }

  
  /**
   * Returns <tt>true</tt> if the bit at specified position is set to one and <tt>false</tt> if it is set to
   * zero.
   * @return whether the specified bit is one or zero.
   */
  public final boolean get(int bit) {
    return (bits_[bit >> 6] & (1l << (bit & 63))) != 0;
  }

  
  /**
   * Returns the index of the next set bit (whose value is one) starting from <code>bit</code> (inclusive). The method
   * returns -1 if there are no more set bits.
   * @param bit the index of the first bit to test
   * @return the index of the first set bit or -1 if none are found
   */
  public final int nextSetBit(int bit) {
    if(bit >= size()) {
      return -1;
    }
    int argByteIndex = (bit >> 6);
    for(int byteIndex = argByteIndex; byteIndex < bits_.length; byteIndex++) {
      long v = bits_[byteIndex];
      if(byteIndex == argByteIndex) {
        // Clear all the bits before <code>bit</code>
        v &= (0xFFFFFFFFFFFFFFFFl << ((bit & 63)));
      }
      if(v != 0) {
        int zeroes = Long.numberOfTrailingZeros(v);
        return byteIndex * 64 + zeroes;
      }
    }
    return -1;
  }

  
  /**
   * Returns the index of the next clear bit (whose value is zero) starting from <code>bit</code> (inclusive). The method
   * returns -1 if there are no more clear bits.
   * @param bit the index of the first bit to test
   * @return the index of the first clear bit or -1 if none are found
   */
  public final int nextClearBit(int bit) {
    if(bit >= size()) {
      return -1;
    }
    int argByteIndex = (bit >> 6);
    for(int byteIndex = argByteIndex; byteIndex < bits_.length; byteIndex++) {
      long v = ~bits_[byteIndex];
      if(byteIndex == bits_.length -1) {
        // Clear extra zeroes
        v &= tailMask_;
      }
      if(byteIndex == argByteIndex) {
        // Clear all the bits before <code>bit</code>
        v &= (0xFFFFFFFFFFFFFFFFl << ((bit & 63)));
      }
      int zeroes = Long.numberOfTrailingZeros(v);
      if(zeroes != 64) {
        return byteIndex * 64 + zeroes;
      }
    }
    return -1;
  }
  

  /**
   * Returns the number of bits in this vector.
   * @return the number of bits.
   */
  public final int size() {
    return size_;
  }

  
  /**
   * Returns the total number of bits set to one in this vector.
   * This number is cached effiency, in case the method is called repeatedly.
   * @return the number of bits set to one.
   */
  public final int count() {
    // if the vector has been modified
    if (count_ == -1) {
      int c = 0;
      int end = bits_.length;
      for (int i = 0; i < end; i++) {
        long v = bits_[i];
        if(v != 0) {
          c += BIT_COUNTS[((int)v & 0x000000FF)];
          c += BIT_COUNTS[((int)(v >> 8) & 0x000000FF)];
          c += BIT_COUNTS[((int)(v >> 16) & 0x000000FF)];
          c += BIT_COUNTS[((int)(v >> 24) & 0x000000FF)];
          c += BIT_COUNTS[((int)(v >> 32) & 0x000000FF)];
          c += BIT_COUNTS[((int)(v >> 40) & 0x000000FF)];
          c += BIT_COUNTS[((int)(v >> 48) & 0x000000FF)];
          c += BIT_COUNTS[((int)(v >> 56) & 0x000000FF)];
        }
      }
      count_ = c;
    }
    return count_;
  }

  
  /**
   * Converts this BigInteger to a <code>long</code>. This conversion is analogous to a narrowing primitive
   * conversion from long to int as defined in the Java Language Specification: if this BitVector is too big to fit in a long,
   * only the low-order 64 bits are returned.
   * @return this BitVector converted to a long.
   */
  public final long longValue() {
    return bits_[0];
  }


  /**
   * Compares this BitVector with the specified Object for equality.
   * @param o - Object to which this BitVector is to be compared.
   * @return true if both BitVectors are of the same size and have the same bit value.
   */
  @Override
  public boolean equals(Object o) {
    if(o instanceof BitVector) {
      BitVector rhs = (BitVector)o;
      return this.size_ == rhs.size_ && Arrays.equals(this.bits_, rhs.bits_);
    }
    return super.equals(o);
  }

  
  /**
   * Returns a hash code for this BitVector.
   * @return a hash code value for this object.
   */
  @Override
  public int hashCode() {
    return size_ ^ Arrays.hashCode(bits_);
  }

  
  /**
   * Compares two <code>BitVector</code> objects numerically.
   * @return the value 0 if this BitVector is equal to the argument BitVector; a value less than 0 if this BitVector is
   * numerically less than the argument Integer; and a value greater than 0 if this BitVector is numerically greater than
   * the argument BitVector (signed comparison).
   */
  public int compareTo(Object o) {
    BitVector rhs = (BitVector)o;
    
    //Make sure the the BitVectors are of the same size to respect the compareTo contract.
    if (size() != rhs.size()) {
      throw new RuntimeException("Size of both BitVectors must be equal.");
    }
    
    int s = this.bits_.length;
    for(int i = s-1; i >= 0; i--) {
      long v1 = bits_[i];
      long v2 = rhs.bits_[i];
      if(v1 != v2) {
        if(v1 > v2) {
          return 1;
        } else if(v1 < v2) {
          return -1;
        }

      }
    }
    return 0;
  }

  
  /**
   * Returns a String object representing various information about this BitVector. More exactly, what will be produced
   * is a string following this model: <code>BitVector{_size_:[_numberOfLongsUsed_,_hashCode_]}</code>.
   */
  @Override
  public String toString() {
    StringBuffer b = new StringBuffer();
    b.append("BitVector{").append(size_).append(":[").append(bits_.length).append(",").append(hashCode()).append("]}");
    return b.toString();
  }
  
  
  /**
   * Returns a string representing all bits in the vector. Mostly usefull for debugging purposes.
   * @return A String with the sequence of zeros and ones as found in the vector.
   */
  public String toBitString() {
    StringBuilder result = new StringBuilder();
    for (int i=0; i<size_;i++) {
      result.append(get(i) ? "1" : "0");
    }
    return result.toString();
  }

  
  /**
   * The actual number of bits a BitVector may hold can be larger than <code>size</code>. This method
   * ensures that any trailing (extra) bits are set to zero.
   */
  private void clearTrailingBits() {
    // clear trailing (extra) bits
    bits_[bits_.length - 1] &= tailMask_;
  }
  
  
  private long tailMask() {
    return ~(0xFFFFFFFFFFFFFFFFl << (size_ & 63));
  }

  
  private static final byte[] BIT_COUNTS = {    // table of bits/byte
    0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4,
    1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
    1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
    2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
    1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
    2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
    2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
    3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
    1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
    2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
    2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
    3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
    2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
    3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
    3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
    4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8
  };

}
