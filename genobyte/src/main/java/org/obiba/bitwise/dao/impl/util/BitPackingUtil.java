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
package org.obiba.bitwise.dao.impl.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BitPackingUtil {

  private static final String STORED_ENCODING = "UTF-8";
  private static final ByteOrder STORED_BYTE_ORDER = ByteOrder.BIG_ENDIAN; 
  
  static public ByteBuffer allocate(int size) {
    return ByteBuffer.allocate(size).order(STORED_BYTE_ORDER);
  }

  static public ByteBuffer toByteBuffer(byte[] bytes) {
    return ByteBuffer.wrap(bytes).order(STORED_BYTE_ORDER);
  }

  static public ByteBuffer putString(String str, ByteBuffer bb) {
    if(str == null) {
      throw new NullPointerException("Argument str cannot be null");
    }
    if(bb == null) {
      throw new NullPointerException("Argument bb cannot be null");
    }
    if(str.length() > 255) {
      // We use a byte to store the length of the string...
      throw new IllegalArgumentException("Cannot store string: length greater than 255 characters.");
    }

    byte strBytes[];
    try {
      strBytes = str.getBytes(STORED_ENCODING);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    bb.put((byte)strBytes.length);
    bb.put(strBytes);
    return bb;
  }

  static public String readString(ByteBuffer bb) {
    int length = bb.get();
    byte strBytes[] = new byte[length];
    bb.get(strBytes);
    try {
      return new String(strBytes, STORED_ENCODING);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  static public ByteBuffer putIntArray(int ints[], ByteBuffer bb) {
    if(ints != null) {
      bb.putInt(ints.length);
      byte[] b = new byte[ints.length*4];
      int l = 0;
      for (int i = 0; i < b.length;) {
        int x = ints[l++];
        b[i++] = (byte)(x >> 24); 
        b[i++] = (byte)(x >> 16); 
        b[i++] = (byte)(x >> 8); 
        b[i++] = (byte)(x >> 0); 
      }
      bb.put(b);
    } else {
      bb.putInt(-1);
    }
    return bb;
  }

  static public int[] readIntArray(ByteBuffer bb) {
    int ints[] = null;
    int size = bb.getInt();
    if(size >= 0) {
      ints = new int[size];

      byte[] b = new byte[size*4];
      bb.get(b);
      int l = 0;

      // Transform the array of bytes into an array of ints. This is a BIG_ENDIAN transform, if STORED_BYTE_ORDER is not BIG_ENDIAN, this will break.
      for (int i = 0; i < b.length; i += 4) {
        ints[l++] = (  (((int) b[i] & 0xff) << 24)   |
                       (((int) b[i+1] & 0xff) << 16) |
                       (((int) b[i+2] & 0xff) << 8)  |
                       (((int) b[i+3] & 0xff) << 0) ); 
      }
    }
    return ints;
  }
  
  static public ByteBuffer putLongArray(long longs[], ByteBuffer bb) {
    if(longs != null) {
      bb.putInt(longs.length);
      byte[] b = new byte[longs.length*8];
      int l = 0;
      for (int i = 0; i < b.length;) {
        long x = longs[l++];
        b[i++] =   (byte)(x >> 56);
        b[i++] = (byte)(x >> 48); 
        b[i++] = (byte)(x >> 40); 
        b[i++] = (byte)(x >> 32); 
        b[i++] = (byte)(x >> 24); 
        b[i++] = (byte)(x >> 16); 
        b[i++] = (byte)(x >> 8); 
        b[i++] = (byte)(x >> 0); 
      }
      bb.put(b);
    } else {
      bb.putInt(-1);
    }
    return bb;
  }

  static public long[] readLongArray(ByteBuffer bb) {
    long longs[] = null;
    int size = bb.getInt();
    if(size >= 0) {
      longs = new long[size];

      // It has been shown that using a bulk read operation is much faster than calling bb.readLong() for every expected long value.
      // This is due to the underlying HeapByteBuffer implementation that calls the get() method for each byte of each long.
      // This quickly gets out of hand: a vector of 500 000 bits would call get() 62,504 times. A field of dimension 3 would call it 187,512 times. 
      // Loading such a field from a store of 2000 records would call it 375,024,000 times...!

      byte[] b = new byte[size*8];
      bb.get(b);
      int l = 0;
      
      // Transform the array of bytes into an array of longs. This is a BIG_ENDIAN transform, if STORED_BYTE_ORDER is not BIG_ENDIAN, this will break.
      for (int i = 0; i < b.length;) {
        longs[l++] = ( (((long) b[i++] & 0xff) << 56)   |
                       (((long) b[i++] & 0xff) << 48) |
                       (((long) b[i++] & 0xff) << 40) |
                       (((long) b[i++] & 0xff) << 32) |
                       (((long) b[i++] & 0xff) << 24) |
                       (((long) b[i++] & 0xff) << 16) |
                       (((long) b[i++] & 0xff) << 8)  |
                       (((long) b[i++] & 0xff) << 0) ); 
      }
    }
    return longs;
  }

}
