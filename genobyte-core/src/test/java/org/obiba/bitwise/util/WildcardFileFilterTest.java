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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

public class WildcardFileFilterTest extends TestCase {

  public void testStar() {
    File f = new File(".");
    WildcardFileFilter filter = new WildcardFileFilter("pom*");
    File[] files = f.listFiles(filter);
    assertContains(new File(".", "pom.xml"), files);
  }

  public void testRecursive() {
    File[] files = WildcardFileFilter.listFiles(new File("."), "src/*/jav?");
    File[] required = { new File("./src/main", "java"), new File("./src/test", "java") };
    assertEquals(required.length, files.length);
    for(File file : required) {
      assertContains(file, files);
    }
  }

  public void testAbsolutePath() throws IOException {
    String absoluteCwd = new File(".").getCanonicalPath();
    File[] files = WildcardFileFilter.listFiles(new File("."), absoluteCwd + "/src/*/jav?");
    File[] required = { new File("./src/main", "java"), new File("./src/test", "java") };
    assertEquals(required.length, files.length);
    for(File file : required) {
      assertContains(file, files);
    }
  }

  public void testRelativePath() throws IOException {
    String absoluteCwd = new File(".").getCanonicalPath();
    File[] files = WildcardFileFilter.listFiles(new File("."), absoluteCwd + "/./src/main/../../src/./*/java");
    File[] required = { new File("./src/main", "java"), new File("./src/test", "java") };
    assertEquals(required.length, files.length);
    for(File file : required) {
      assertContains(file, files);
    }
  }

  public void testMatchSameFilesMultipleTimesPattern() throws IOException {
    String absoluteCwd = new File(".").getCanonicalPath();
    // This pattern will match the required files multiple times (*/../*)
    File[] files = WildcardFileFilter.listFiles(new File("."), absoluteCwd + "/./src/*/../*/java");
    File[] required = { new File("./src/main", "java"), new File("./src/test", "java") };
    assertEquals(required.length, files.length);
    for(File file : required) {
      assertContains(file, files);
    }
  }

  private void assertContains(File required, File[] files) {
    assertNotNull("File list is null", files);
    assertTrue("File list is empty", files.length > 0);
    for(File file : files) {
      try {
        if(required.getCanonicalFile().equals(file.getCanonicalFile())) {
          return;
        }
      } catch(IOException e) {
        throw new RuntimeException();
      }
    }
    assertTrue("Required file [" + required.getName() + "] not found within " + Arrays.toString(files), false);
  }
}
