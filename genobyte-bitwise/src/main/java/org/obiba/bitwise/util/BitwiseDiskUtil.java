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
package org.obiba.bitwise.util;

import org.obiba.bitwise.BitwiseStoreUtil;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Utility class for manipulating bitwise stores directly on disk.
 */
public class BitwiseDiskUtil {

  /**
   * The configuration key for the root directory of bitwise stores.
   */
  public static final String ROOT_DIR_PROPERTY = "bitwise.dir.root";

  /**
   * Returns the root directory where all bitwise store instances are stored under. This
   * is the value of the {@link BitwiseDiskUtil#ROOT_DIR_PROPERTY} configuration key.
   *
   * @return the root directory of all bitwise stores.
   */
  public static String getRoot() {
    return BitwiseStoreUtil.getInstance().getConfigurationPropertiesProvider().getDefaultProperties()
        .getProperty(ROOT_DIR_PROPERTY);
  }

  /**
   * Returns the size (in bytes) of the specified bitwise store instance.
   *
   * @param name the unique name of the bitwise store
   * @return the size in bytes of the store
   */
  public static long getSize(String name) {
    long storeSize = 0;
    String rootName = getRoot();
    File store = new File(rootName + "/" + name);
    if (store.exists()) {
      File[] files = store.listFiles();
      for (File file : files) {
        storeSize += file.length();
      }
    }
    return storeSize;
  }

  /**
   * Removes the specified bitwise stores from disk. This method will call
   * {@link BitwiseDiskUtil#deleteStore(String)} for each name specified.
   *
   * @param names the unique names of the bitwise stores to remove
   */
  public static void deleteStores(String... names) {
    if (names == null) return;
    for (String name : names) {
      deleteStore(name);
    }
  }

  /**
   * Removes the specified bitwise store from disk. This method effectively removes the
   * directory that contains the specified bitwise store. The deletion is done within
   * an exclusive lock (using {@link BitwiseStoreUtil#lock(String, Runnable))}.
   *
   * @param names the unique name of the bitwise to remove
   */
  public static void deleteStore(final String name) {
    BitwiseStoreUtil.getInstance().lock(name, new Runnable() {
      public void run() {
        String rootName = getRoot();
        File store = new File(rootName + "/" + name);
        if (store.exists()) {
          try {
            FileUtil.deltree(store);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      }
    });
  }

  /**
   * Writes multiple bitwise stores to a zip file. The output file will be
   * overwritten.
   *
   * @param zipFile the zip file to create and write to.
   * @param names   an array of unique bitwise stores to add to the zip file
   * @throws IOException when an error occurs
   */
  public static void zipStores(File zipFile, String... names) throws IOException {
    ZipOutputStream zos = null;
    try {
      zos = new ZipOutputStream(new FileOutputStream(zipFile));
      zos.setLevel(ZipOutputStream.STORED);
      for (String name : names) {
        zipStore(name, zos);
      }
    } finally {
      if (zos != null) zos.close();
    }
  }

  /**
   * Adds the specified store to an existing zip file stream.
   *
   * @param name   the unique name of the bitwise store.
   * @param stream the zip file stream to wrtie to.
   */
  public static void zipStore(final String name, final ZipOutputStream stream) {
    BitwiseStoreUtil.getInstance().lock(name, new Runnable() {
      public void run() {
        String rootName = getRoot();
        File store = new File(rootName + "/" + name);
        try {
          outputToZip(store, stream);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    });
  }

  /**
   * Utility method for zipping a bitwise store to an existing zip file.
   *
   * @param store the bitwise store to add
   * @param zos   the zip file to use for output
   * @throws IOException when a problem occurs
   */
  private static void outputToZip(File store, ZipOutputStream zos) throws IOException {
    String rootName = store.getName() + "/";
    zos.putNextEntry(new ZipEntry(rootName));
    zos.closeEntry();

    if (store.exists()) {
      File[] files = store.listFiles();
      for (File file : files) {
        zos.putNextEntry(new ZipEntry(rootName + file.getName()));
        copyFile(file, zos);
        zos.closeEntry();
      }
    }
  }

  /**
   * Utility method to copy a file into an {@link java.io.OutputStream}.
   *
   * @param inputFile the file to copy
   * @param os        the stream to copy into
   * @throws IOException when an error occurs during the copy.
   */
  private static void copyFile(File inputFile, OutputStream os) throws IOException {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(inputFile);
      // A 1Mb data buffer
      byte[] buffer = new byte[1024 * 1024];
      int n = 0;
      while ((n = fis.read(buffer)) != -1) {
        os.write(buffer, 0, n);
      }
    } finally {
      if (fis != null) fis.close();
    }
  }
}
