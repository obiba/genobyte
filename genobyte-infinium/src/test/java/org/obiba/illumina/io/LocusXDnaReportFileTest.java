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
import java.util.Calendar;
import java.util.Iterator;

public class LocusXDnaReportFileTest extends AbstractFileParserTest {

  public void testParseEmptyFileAsInputStream() {
    try {
      LocusXDnaReportFile lxd = new LocusXDnaReportFile(loadEmptyFileAsFile());
      assertTrue("Expected IOException", false);
    } catch (IOException e) {
      assertTrue(true);
    }
  }
  
  public void testParseValidFileAsFile() {
    LocusXDnaReportFile lxd = null;
    try {
      lxd = new LocusXDnaReportFile(loadResourceAsFile("locusXDna.csv"));
      validateLdr(lxd);
    } catch (IOException e) {
      e.printStackTrace();
      fail(e.getMessage());
    } finally {
      lxd.close();
    }
  }

  private void validateLdr(LocusXDnaReportFile lxd) {
    
    String[] sampleIds = {"1996", "1997", "1998", "1999", "2000", "NA12236.22"};
    
    assertEquals("HumanHap300_(v1.0.0)", lxd.getOligoPoolId());
    assertEquals(new Integer(6), lxd.getNumberDna());
    assertEquals(new Integer(1000), lxd.getNumberLoci());
    assertEquals(new Integer(0), lxd.getProjectId());
    assertEquals("", lxd.getProjectName());
    assertEquals(1000, lxd.getGtsLocusId().length);

    String[] records = {"Gentrain Scores", "ilmnIds", "ilmnStrand", "locusIds", "locusNames", "olicodeNames", "snps"};
    for (int i = 0; i < records.length; i++) {
      String recordType = records[i];
      try {
        assertEquals("RecordType: "+recordType, 1000, lxd.getOligoPoolRecordData(recordType).length);
      } catch (IOException e) {
        fail("Failed fetching record data for type ["+recordType+"]");
        return;
      }
    }

    Calendar c = Calendar.getInstance();
    // March 21s 2006
    c.set(2006, 2, 21, 0, 0, 0);
    c.clear(Calendar.MILLISECOND);
    long march21st2006 = c.getTimeInMillis();

    try {
      Iterator<LocusXDnaReportSampleDataEntry> it = lxd.getEntries();
      for (int i = 0; i < lxd.getNumberDna(); i++) {
        assertTrue(it.hasNext());
        LocusXDnaReportSampleDataEntry entry = it.next();
        if(i < lxd.getNumberDna()-1) {
          assertTrue(it.hasNext());
        } else {
          assertFalse(it.hasNext());
        }

        assertNotNull(entry);
        assertEquals(sampleIds[i], entry.getInstituteLabel());
        assertEquals(march21st2006, entry.getImageDate().getTime());
        assertEquals(1000, entry.getCalls().length);
        assertEquals(1000, entry.getScoreCalls().length);
      }
    } catch (IOException e) {
      fail("Failed fetching next sample entry: " + e.getMessage());
      return;
    }
  }

}
