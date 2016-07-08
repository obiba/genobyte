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
package org.obiba.bitwise.dictionary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.List;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.util.FileLineIterator;
import org.obiba.bitwise.util.Huffman;
import org.obiba.bitwise.util.HuffmanSeedProvider;

/**
 * Provides encoding/decoding capabilities between a <tt>String</tt> of variable size value and a <tt>BitVector</tt>.
 * This implementation uses Huffman coding to transform characters into bits. An instance of this dictionary must have
 * a list of all possible characters to transform value to/from a <tt>BitVector</tt>. Therefore, before encoding
 * or decoding any value, the seed of the dictionary must be set. A seed is the list of all characters that can be found
 * in <tt>String</tt> values. 
 */
public class HuffmanDictionary implements WildcardDictionary<String> {

  private String name_ = null;

  private Huffman hm_ = null;

  private int dimension_ = 0;

  public HuffmanDictionary(String name) {
    super();
    name_ = name;
  }

  /**
   * Sets the Huffman coding seed of this dictionary by providing the path to a text file.
   * The file will be used as this dictionary seed.
   * @param filename the file to be used as a seed by this dictionary.
   */
  public void setSeedFile(String filename) {
    if(hm_ == null) {
      try {
        hm_ = new Huffman(new FileLineIterator(filename));
      } catch(IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * Sets the Huffman coding seed of this dictionary by providing a <tt>String</tt>.
   * The <tt>String</tt> will be used as this dictionary seed.
   * @param seed the <tt>String</tt> to be used as a seed by this dictionary.
   */
  public void setSeedString(String seed) {
    if(hm_ == null) {
      hm_ = new Huffman(Collections.singletonList(seed));
    }
  }

  /**
   * Sets the Huffman coding seed of this dictionary by providing a <tt>List</tt> of <tt>Strings</tt>.
   * All the <tt>Strings</tt> in the <tt>List</tt> will be used as this dictionary seed.
   * @param seed the <tt>String</tt> <tt>List</tt> to be used as a seed by this dictionary.
   */
  public void setSeed(List<String> seed) {
    if(hm_ == null) {
      hm_ = new Huffman(seed);
    }
  }

  /**
   * Sets the Huffman coding seed of this dictionary by providing an <tt>Object</tt> implementing the <tt>HuffmanSeedProvider</tt> interface.
   * The object will act as a provider for the characters to be used in this dictionary's Huffman coding.
   * @param className the name of the class to be used as a character provider.
   */
  public void setProvider(String className) {
    if(hm_ == null) {
      try {
        Class providerClass = Class.forName(className);
        if(HuffmanSeedProvider.class.isAssignableFrom(providerClass) == false) {
          throw new RuntimeException("Seed provider class [" + className + "] does not implement " +
              HuffmanSeedProvider.class.getSimpleName());
        }
        hm_ = new Huffman((HuffmanSeedProvider) providerClass.newInstance());
        return;
      } catch(ClassNotFoundException e) {
        throw new RuntimeException("Seed provider class [" + className + "] not found");
      } catch(InstantiationException e) {
        throw new RuntimeException("Seed provider class [" + className + "] cannot be instantiated.", e);
      } catch(IllegalAccessException e) {
        throw new RuntimeException("Seed provider class [" + className + "] cannot be instantiated.", e);
      }
    }
  }

  /*
   * @see org.obiba.bitwise.Dictionary#convert(java.lang.String)
   */
  public String convert(String value) {
    return value;
  }

  /*
   * @see org.obiba.bitwise.Dictionary#dimension()
   */
  public int dimension() {
    return dimension_;
  }

  /*
   * @see org.obiba.bitwise.Dictionary#getName()
   */
  public String getName() {
    return name_;
  }

  /*
   * @see org.obiba.bitwise.Dictionary#isOrdered()
   */
  public boolean isOrdered() {
    return false;
  }

  /*
   * @see org.obiba.bitwise.Dictionary#lookup(T)
   */
  public BitVector lookup(String key) {
    if(key == null) {
      return null;
    }
    BitVector v = hm_.encode(key);
    if(v != null) {
      dimension_ = dimension_ > v.size() ? dimension_ : v.size();
      if(v.size() < dimension_) {
        v.grow(dimension_);
      }
    }
    return v;
  }

  /*
   * @see org.obiba.bitwise.dictionary.WildcardDictionary#partialLookupLeft(T)
   */
  public BitVector partialLookupLeft(String key) {
    if(key == null) {
      return null;
    }
    BitVector v = hm_.encode(key, false);
    return v;
  }

  /*
   * @see org.obiba.bitwise.dictionary.WildcardDictionary#partialLookupRight(T)
   */
  public BitVector partialLookupRight(String key) {
    if(key == null) {
      return null;
    }
    BitVector v = hm_.encode(key, true);
    return v;
  }

  /*
   * @see org.obiba.bitwise.Dictionary#reverseLookup(org.obiba.bitwise.BitVector)
   */
  public String reverseLookup(BitVector v) {
    if(v == null) {
      return null;
    }
    return hm_.decode(v);
  }

  public boolean isVariableLength() {
    return true;
  }

  @Override
  public boolean equals(Object obj) {
    if(obj instanceof HuffmanDictionary) {
      HuffmanDictionary hd = (HuffmanDictionary) obj;
      return hd.hm_.equals(hd.hm_);
    }
    return super.equals(obj);
  }

  public void setRuntimeData(byte[] data) {
    if(data == null) {
      return;
    }
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(data);
      ObjectInputStream ois = new ObjectInputStream(bais);
      hm_ = (Huffman) ois.readObject();
      dimension_ = ois.readInt();
      ois.close();
    } catch(IOException e) {
      throw new RuntimeException(e);
    } catch(ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public byte[] getRuntimeData() {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(hm_);
      oos.writeInt(dimension_);
      oos.close();
      byte[] ret = baos.toByteArray();
      return ret;
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }

}
