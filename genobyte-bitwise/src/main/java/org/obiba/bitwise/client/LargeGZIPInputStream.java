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
package org.obiba.bitwise.client;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class LargeGZIPInputStream extends GZIPInputStream {
  private long nbReadBytes_ = 0;

  public LargeGZIPInputStream(InputStream in) throws IOException {
    super(in);
  }

  public LargeGZIPInputStream(InputStream in, int size) throws IOException {
    super(in, size);
  }

  @Override
  public int read(byte[] buf, int off, int len) throws IOException {
    int retVal = -1; //Can't be 0...you cannot read 0 bytes it's >0 or <0 never =
    try {
      retVal = super.read(buf, off, len);
      nbReadBytes_ += retVal;
    } catch (IOException e) {
      // Ignore the error if it's a corrupted trailer for a file larger than 4Gb
      if (!(nbReadBytes_ > 4 * 1024 * 1024 * 1024 && e.getMessage() != null &&
          e.getMessage().startsWith("Corrupt GZIP trailer"))) throw e;
    }
    return retVal;
  }

  @Override
  public void close() throws IOException {
    nbReadBytes_ = 0;
    super.close();
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    BufferedInputStream buffer = null;

    for (int idx = 0; idx < args.length; idx++) {
      String file = args[idx];
      System.out.println("Testing: " + file);
      System.out.println("Reading normally");
      try {
        buffer = new BufferedInputStream(new GZIPInputStream(new FileInputStream(args[0])));
        byte[] bytes = new byte[4 * 1024];
        while (true) {
          int read = buffer.read(bytes);
          if (read < 0) break;
        }
      } catch (Exception e) {
        System.out.println("Normally caught: " + e.getMessage());
      } finally {
        if (buffer != null) {
          try {
            buffer.close();
          } catch (Exception e) {
            // don't care
          }
        }
        buffer = null;
      }
      System.out.println("Done Reading normally");

      System.out.println("Reading with new impl");
      try {
        buffer = new BufferedInputStream(new LargeGZIPInputStream(new FileInputStream(file)));
        byte[] bytes = new byte[4 * 1024];
        while (true) {
          int read = buffer.read(bytes);
          if (read < 0) break;
        }
      } catch (Exception e) {
        System.out.println("New impl caught: " + e.getMessage());
      } finally {
        if (buffer != null) {
          try {
            buffer.close();
          } catch (Exception e) {
            // don't care
          }
        }
        buffer = null;
      }
      System.out.println("Done Reading with new imply");

      System.out.println("Comparing");
      BufferedInputStream buffer2 = null;
      long bytesRead = 0;
      try {
        buffer = new BufferedInputStream(new LargeGZIPInputStream(new FileInputStream(file)));
        buffer2 = new BufferedInputStream(new GZIPInputStream(new FileInputStream(file)));
        ;
        byte[] bytes = new byte[4 * 1024];
        byte[] bytes2 = new byte[4 * 1024];
        while (true) {
          int read = buffer.read(bytes);
          int read2 = buffer2.read(bytes2);
          if (read != read2) System.out.println("Read different. New Impl: " + read + " >original: " + read2);

          if (read < 0) break;

          bytesRead += read;
          for (int i = 0; i < read; i++) {
            if (bytes[i] != bytes2[i]) System.out
                .println("Bytes at " + read + " are different. New Impl: " + bytes[i] + " >original: " + bytes2[i]);
          }
        }
      } catch (Exception e) {
        System.out.println("Compare caught: " + e.getMessage());
      } finally {
        if (buffer != null) {
          try {
            buffer.close();
          } catch (Exception e) {
            // don't care
          }
        }
        buffer = null;
        if (buffer2 != null) {
          try {
            buffer2.close();
          } catch (Exception e) {
            // don't care
          }
        }
        buffer2 = null;
      }
      System.out.println("Bytes read: " + bytesRead);
      System.out.println("Done comparing");
    }
  }
}
