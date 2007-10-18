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
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * Utility class to decode a filename pattern into an array of matching files.
 * <p/>
 * This class may be used to find files within a directory by using a filename pattern "à la" DOS and Unix.
 * An example pattern is "*.*" which would result in all files that contain a "dot" within its name. To handle
 * directory names within a pattern (such as "data/2007??/*.csv"), the static method {@link WildcardFileFilter#listFiles(File, String)} 
 * may be used to call the filter recursively.
 */
public class WildcardFileFilter implements FileFilter {

  /** The regex */
  private Pattern pattern;

  public WildcardFileFilter(String filter) {
    String regex = replaceWildcards(filter);
    this.pattern = Pattern.compile(regex);
  }
  
  public boolean accept(File f) {
    // Filter out "dot files" (files that start with a "."), but not directories
    // Accept (all directories OR files that don't start with ".") AND that match the specified pattern
    return (f.isDirectory() || f.getName().startsWith(".") == false) && pattern.matcher(f.getName()).matches();
  }

  /**
   * Calls the WildcardFileFilter recursively to handle directories within the filename pattern.
   * 
   * @param cwd the root of the recursive scan
   * @param filter the pattern
   * @return an array of matching File instances
   */
  public static File[] listFiles(File cwd, String filter) {
    int sep = filter.indexOf(File.separatorChar);
    if(sep != -1) {
      Set<File> result = new TreeSet<File>();
      if(sep == 0) {
        // It's an absolute path...
        cwd = new File("/");
        filter = filter.substring(1);
        sep = filter.indexOf(File.separatorChar);
      }

      String dirPattern = filter.substring(0, sep);
      String subFilter = filter.substring(sep+1, filter.length());

      if(dirPattern.equals(".") || dirPattern.equals("..")) {
        try {
          cwd = new File(cwd.getCanonicalPath() + File.separator + dirPattern).getCanonicalFile();
          // Recursive call after removing the relative portions of the cwd.
          File[] subFiles = listFiles(cwd, subFilter);
          if(subFiles != null && subFiles.length > 0) {
            result.addAll(Arrays.asList(subFiles));
          }
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      } else {
        WildcardFileFilter wff = new WildcardFileFilter(dirPattern);
        File[] subDirs = cwd.listFiles(wff);
        for (File subdir : subDirs) {
          File[] subFiles = listFiles(subdir, subFilter);
          if(subFiles != null && subFiles.length > 0) {
            result.addAll(Arrays.asList(subFiles));
          }
        }
      }
      return result.toArray(new File[result.size()]);
    } else {
      WildcardFileFilter fileFilter = new WildcardFileFilter(filter);
      return cwd.listFiles(fileFilter);
    }
  }

  /**
   * Converts a DOS/Unix filename pattern into a Java regular expression
   * 
   * @param wild the pattern to convert
   * @return a Java regex equivalent to the filename pattern
   */
  private static String replaceWildcards(String wild) {
    StringBuffer buffer = new StringBuffer();

    char[] chars = wild.toCharArray();

    for (int i = 0; i < chars.length; ++i) {
      if (chars[i] == '*')
        buffer.append(".*");
      else if (chars[i] == '?')
        buffer.append(".");
      else if ("+()^$.{}[]|\\".indexOf(chars[i]) != -1)
        buffer.append('\\').append(chars[i]);
      else
        buffer.append(chars[i]);
    }

    return buffer.toString();
  }

}
