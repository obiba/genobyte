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
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A collection of static methods to manage files and directories.
 */
public final class FileUtil {
  
  /**
   * Delete a directory and its sub-files
   * @param directory the directory to delete
   * @throws IOException if  any
   */
  public static void deltree(final String directory) throws IOException {
    deltree(new File(directory));
  }


  /**
   * Delete a directory and its sub-files
   * @param dir the directory to delete
   * @throws IOException if  any
   * @throws FileNotFoundException if  any
   */
  public static void deltree(final File dir) throws IOException {
    if (dir == null) {
      return;
    }

    if (!dir.exists()) {
      throw new FileNotFoundException("The dir=[" + dir + "] doesnt exist");
    }

    if (!dir.isDirectory()) {
      return;
    }

    File[] allFiles = dir.listFiles();

    for (int i = 0; i < allFiles.length; i++) {
      File currentFile = allFiles[i];

      if (currentFile.isDirectory()) {
        deltree(currentFile);
      } else {
        if (!currentFile.delete()) {
          System.err.println("Cant delete the file=[" + currentFile.getAbsolutePath() + "]");
        }
      }
    }

    if (!dir.delete()) {
      System.err.println("Cant delete the dir=[" + dir.getAbsolutePath() + "]");
    }
  }

}
