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
package org.obiba.bitwise.util;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.StringCharacterIterator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.obiba.bitwise.BitVector;


/**
 * Implementation of an Huffman encoder.
 * <br/>
 * The seed should be representative of the actual data to be encoded. Also,
 * any character not present in the seed cannot be encoded. The result of encoding
 * a string that contains an unknown character is not specified.
 */
public class Huffman implements Serializable {

  private static final long serialVersionUID = -2212767101424067314L;

  private Node root_ = null;

  transient private BitVector[] codes_ = new BitVector[Character.MAX_VALUE];
  transient private BitVector endCode_ = null;


  /**
   * Builds an Huffman codec instance with the specified file as a seed. File is expected to have
   * one seed string per line.
   * @param file is a File instance to use as seeding data.
   */
  public Huffman(File file) throws IOException {
    seed(new FileLineIterator(file));
  }


  public Huffman(List<String> seed) { 
    super();
    seed(seed.iterator());
  }

  
  public Huffman(String seed) {
    this(Collections.singletonList(seed));
  }

  /**
   * Builds an Huffman codec instance with the specified seed provider implementation
   * @param seed used as codec seed
   */
  public Huffman(HuffmanSeedProvider provider) {
    super();
    seed(provider.getSeed());
  }

  @Override
  public boolean equals(Object obj) {
    if(obj instanceof Huffman) {
      Huffman h = (Huffman)obj;
      return Arrays.equals(codes_, h.codes_) && endCode_.equals(h.endCode_);
    }
    return super.equals(obj);
  }

  /**
   * Encodes the specified string into a series of unique codes
   * @param key the value to encode. If null is specified, returns null.
   * @return a encoded version of the string or null if <code>key</code> was null or if <code>key</code> contains a character not present in the seed.
   */
  public BitVector encode(String key) {
    BitVector v = this.encode(key, true);
    return v;
  }


  /**
   * Encodes the specified string into a series of unique codes.
   * @param key The value to encode. If null is specified, returns null.
   * @param addEOS If true, add an end of field character to the end of the string.
   * @return
   */
  public BitVector encode(String key, boolean addEOS) {
    if(key == null) {
      return null;
    }
    BitVector v = new BitVector(0);
    StringCharacterIterator sci = new StringCharacterIterator(key);
    char c = sci.first();
    while(c != StringCharacterIterator.DONE) {
      BitVector codePart = codes_[c];
      if(codePart == null) {
        // c is not part of original alphabet (seed)
        return null;
      }
      append(v, codePart);
      c = sci.next();
    }
    if (addEOS) {
      append(v, endCode_);
    }
    return v; 
  }


  /**
   * Decodes the specified code into its original value.
   * @param v the encoded string value
   * @return the original string
   */
  public String decode(BitVector v) {
    StringBuilder b = new StringBuilder();
    int i = 0;
    Node node = root_;
    while(i < v.size()) {
      if(v.get(i++)) {
        node = node.right;
      } else {
        node = node.left;
      }
      if(node.isLeaf()) {
        if(node.c == StringCharacterIterator.DONE) {
          // We hit the end of string code
          break;
        }
        b.append(node.c);
        node = root_;
      }
    }
    if(b.length() > 0) {
      return b.toString();
    }
    return null;
  }


  /**
   * Deserializes the object. Overriden to restore codes from the serialized tree.
   * @param in
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    // Have the VM load this
    in.defaultReadObject();

    // Initialize transient fields
    codes_ = new BitVector[Character.MAX_VALUE];
    makeCodes();
  }


  /**
   * Seeds this Huffman codec.
   * In other words, from a list of strings, counting the occurences of each character in the text.
   * The frequency for each character will be used by the Huffman code generator to decide which
   * character goes where in the tree.
   * 
   * @param seed strings used to seed the tree
   */
  private void seed(Iterator<String> seed) {
    int lines = 0;
    long[] freq = new long[Character.MAX_VALUE];
    while(seed.hasNext()) {
      StringCharacterIterator sci = new StringCharacterIterator(seed.next());
      lines++;
      char c = sci.first();
      while(c != StringCharacterIterator.DONE) {
        freq[c]++;
        c = sci.next();
      }
    }

    TreeSet<Node> nodes = new TreeSet<Node>();
    for (int i = 0; i < freq.length; i++) {
      long l = freq[i];
      if(l > 0) {
        nodes.add(new Node((char)i, l));
      }
    }

    while (nodes.size() > 1) {
      Iterator<Node> i = nodes.iterator();
      Node n1 = i.next();
      i.remove();
      Node n2 = i.next();
      i.remove();
      nodes.add(new Node(n1,n2));
    }
    root_ = nodes.first();
    createEndOfStringCode();
    makeCodes();
  }


  /**
   * Splits the left-most leaf (only composed of zeros) to add an End Of String character. This
   * character will contain zeros for all but the last bit.
   */
  private void createEndOfStringCode() {
    Node parent = null;
    Node node = root_;
    // Go left until we hit the leaf
    while(node.isLeaf()==false) {
      parent = node;
      node = node.left;
    }
    // No nodes
    if(parent == null) {
      return;
    }

    //Split leaf node in two:
    //  -Assign leaf to left branch of new node
    //  -Assign end-of-string code to right branch to make the end of string code a sequence of 0 ended with a 1.
    Node endOfString = new Node(StringCharacterIterator.DONE, 0);
    Node merged = new Node(node, endOfString);
    parent.left = merged;
  }


  /**
   * Gets the <tt>BitVector</tt> that contains the End Of String character code in this Huffman code.
   * @return the End Of String code.
   */
  public BitVector getEndOfStringCode() {
    return endCode_;
  }


  /**
   * Generates Huffman codes, from the character frequency previously obtained
   */
  private void makeCodes() {
    Node node = root_;
    BitVector code = new BitVector(0);
    code(node, code, 0);
  }


  /**
   * Generates codes in a recursive way.
   * @param n the current node
   * @param v the prefix of the code this method will generate
   * @param i the index of the current bit value this method must determine
   */
  private void code(Node n, BitVector v, int i) {
    BitVector code = new BitVector(v);
    if(n.isLeaf()) {
      if(n.c != StringCharacterIterator.DONE) {
        codes_[n.c] = code;
      } else {
        endCode_ = code;
      }
      return;
    }
    code.grow(i+1);
    if(n.left != null) {
      // Going left: encode a zero at i (no need since original vector is all zeroes)
      // code.clear(i);
      code(n.left, code, i+1);
    }

    if(n.right != null) {
      // Going right: encode a 1 at i
      code.set(i);
      code(n.right, code, i+1);
    }
  }


  /**
   * Appends vector <code>v2</code> to <code>v</code>. This effectively grows vector <code>v</code> of <code>v2.size()</code> 
   * and copies the contents of <code>v2</code> into the newly allocated space.
   * @param v the vector to append to
   * @param v2 the data to append
   */
  private void append(BitVector v, BitVector v2) {
    int offset = v.size();
    v.grow(v.size() + v2.size());
    for(int i = v2.nextSetBit(0); i != -1; i = v2.nextSetBit(i+1)) {
      v.set(offset + i);
    }
  }


  /**
   * A node in the binary Huffman code tree.
   */
  private class Node implements Comparable<Node>, Serializable  {

    private static final long serialVersionUID = -1145824693521402867L;

    private long weight = 0;
    private char c = StringCharacterIterator.DONE;
    private Node left = null;
    private Node right = null;

    public Node(char c, long w) {
      this.c = c;
      this.weight = w;
    }

    public Node(Node left, Node right) {
      this.weight = left.weight + right.weight;
      this.left = left;
      this.right = right;
    }

    public boolean isLeaf() {
      return left == null && right == null;
    }

    public int compareTo(Node other) {
      if (this.weight == other.weight)
        return -1;

      return (int)(this.weight-other.weight);
    }
  }

}
