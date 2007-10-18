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
package org.obiba.bitwise.dictionary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.List;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.Dictionary;
import org.obiba.bitwise.util.FileLineIterator;
import org.obiba.bitwise.util.Huffman;
import org.obiba.bitwise.util.HuffmanSeedProvider;


/**
 * Provides encoding/decoding capabilities between a <tt>Character</tt> value and a <tt>BitVector</tt>. This implementation uses
 * Huffman coding to transform characters into bits. An instance of this dictionary must have a list of all possible
 * characters to transform value to/from a <tt>BitVector</tt>. Therefore, before encoding or decoding any value,
 * the seed of the dictionary must be set. A seed is the list of all characters that can be found in <tt>Character</tt> values. 
 */
public class HuffmanCharacterDictionary implements Dictionary<Character> {

  private String name_ = null;
  private Huffman hm_ = null;
  private int dimension_ = 0;


  public HuffmanCharacterDictionary(String name) {
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
      } catch (IOException e) {
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
          throw new RuntimeException("Seed provider class ["+className+"] does not implement "+HuffmanSeedProvider.class.getSimpleName());
        }
        hm_ = new Huffman((HuffmanSeedProvider)providerClass.newInstance());
        return;
      } catch (ClassNotFoundException e) {
        throw new RuntimeException("Seed provider class ["+className+"] not found");
      } catch (InstantiationException e) {
        throw new RuntimeException("Seed provider class ["+className+"] cannot be instantiated.", e );
      } catch (IllegalAccessException e) {
        throw new RuntimeException("Seed provider class ["+className+"] cannot be instantiated.", e);
      }
    }
  }


  /*
   * @see org.obiba.bitwise.Dictionary#convert(java.lang.String)
   */
  public Character convert(String value) {
    return value.charAt(0);
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
  public BitVector lookup(Character key) {
    if(key == null) {
      return null;
    }
    BitVector v = hm_.encode(key.toString(), false);
    if(v != null) {
      dimension_ = dimension_ > v.size() ? dimension_ : v.size();
      if(v.size() < dimension_) {
        v.grow(dimension_);
      }
    }
    return v;
  }


  /*
   * @see org.obiba.bitwise.Dictionary#reverseLookup(org.obiba.bitwise.BitVector)
   */
  public Character reverseLookup(BitVector v) {
    if(v == null) {
      return null;
    }
    return hm_.decode(v).charAt(0);
  }


  public boolean isVariableLength() {
    return false;   //Fields using this dictionary always contain a single character.
  }


  public void setRuntimeData(byte[] data) {
    if(data == null) {
      return;
    }
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(data);
      ObjectInputStream ois = new ObjectInputStream(bais);
      hm_ = (Huffman)ois.readObject();
      dimension_ = ois.readInt();
      ois.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
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
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
