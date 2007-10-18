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
package org.obiba.illumina.io;

import java.io.IOException;
import java.util.Iterator;


public class ManifestFileTest extends AbstractFileParserTest {

  public void testParseEmptyFileAsInputStream() {
    try {
      ManifestFile manifest = new ManifestFile(loadEmptyFileAsStream());
      assertTrue("Expected IOException", false);
    } catch (IOException e) {
      assertTrue(true);
    }
  }
  
  public void testParseValidFileAsInputStream() {
    try {
      validateHumapHap300v1(new ManifestFile(loadResourceAsStream("HumanHap300v1.csv")));
    } catch (IOException e) {
      e.printStackTrace();
      super.fail(e.getMessage());
    }
  }

  public void testParseValidFileAsFile() {
    try {
      validateHumapHap300v1(new ManifestFile(loadResourceAsFile("HumanHap300v1.csv")));
    } catch (IOException e) {
      e.printStackTrace();
      super.fail(e.getMessage());
    }
  }

  public void testParseValidFileAsFile2() {
    try {
      validateHumapHap300v2(new ManifestFile(loadResourceAsFile("HumanHap300v2_A.csv")));
    } catch (IOException e) {
      e.printStackTrace();
      super.fail(e.getMessage());
    }
  }
  
  private void validateHumapHap300v1(ManifestFile manifest) {
    assertEquals("HumanHap300_(v1.0.0)", manifest.getHeaderValue("Descritpor File Name(s)"));
    assertEquals("Infinium II", manifest.getHeaderValue("Assay Format"));
    assertEquals("20", manifest.getHeaderValue("SNP Count"));
    assertEquals(20, manifest.getSnpCount());

    int i = 0;
    Iterator<ManifestFileAssayEntry> iter = manifest.entries();
    while(iter.hasNext()) {
      ManifestFileAssayEntry entry = iter.next();
      i++;
    }
    assertEquals(20, i);
  }

  private void validateHumapHap300v2(ManifestFile manifest) {
    assertEquals("HumanHap300v2_A.csv", manifest.getHeaderValue("Descriptor File Name"));
    assertEquals("Infinium II", manifest.getHeaderValue("Assay Format"));
    assertEquals("20", manifest.getHeaderValue("Loci Count"));
    assertEquals(20, manifest.getSnpCount());

    int i = 0;
    Iterator<ManifestFileAssayEntry> iter = manifest.entries();
    while(iter.hasNext()) {
      ManifestFileAssayEntry entry = iter.next();
      assertNotNull(entry.getGenomeBuild());
      assertTrue(entry.getGenomeBuild().length() > 0);
      i++;
    }
    assertEquals(20, i);
  }

}
