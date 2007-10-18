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


public class SampleSheetFileTest extends AbstractFileParserTest {

  public void testParseEmptyFileAsInputStream() {
    try {
      SampleSheetFile ss = new SampleSheetFile(loadEmptyFileAsStream());
      assertTrue("Expected IOException", false);
    } catch (IOException e) {
      assertTrue(true);
    }
  }
  
  public void testValidSampleSheetAsStream() {
    try {
      SampleSheetFile ss = new SampleSheetFile(loadResourceAsStream("sampleSheet.csv"));
      assertEquals("Daniel Vincent", ss.getHeaderValue("Investigator Name"));
      assertEquals("Infinium II Training", ss.getHeaderValue("Project Name"));
      assertEquals("", ss.getHeaderValue("Experiment Name"));
      assertEquals("16/01/2006", ss.getHeaderValue("Date"));
      
      assertEquals(1, ss.getManifests().size());
      assertEquals("HumanHap300_(v1.0.0)", ss.getManifestFile("A"));
      
      assertEquals(24, ss.getSampleData().size());

      for (SampleSheetFileEntry entry : ss.getSampleData()) {
        if(entry.getSampleId().contains("_B")) {
          assertNotNull(entry.getSampleId()+" entry should have a non-null value in the \"Reference\" column.", entry.getReference());
        } else {
          assertNull(entry.getReference());
        }
      }
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }

  public void testSampleSheetWithoutHeaderSection() {
    try {
      SampleSheetFile ss = new SampleSheetFile(loadResourceAsStream("sampleSheetNoHeader.csv"));
      assertNull(ss.getHeaderValue("Investigator Name"));
      assertNull(ss.getHeaderValue("Project Name"));
      assertNull(ss.getHeaderValue("Experiment Name"));
      assertNull(ss.getHeaderValue("Date"));
      assertEquals(1, ss.getManifests().size());
      assertEquals("HumanHap300_(v1.0.0)", ss.getManifestFile("A"));
      assertEquals(24, ss.getSampleData().size());
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
