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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LocusXDnaReportSampleDataEntry {

  private static final SimpleDateFormat SDF = new SimpleDateFormat("MMM dd yyyy");
  
  private String instituteLabel_ = null;
  private String plateWell_ = null;
  private Date imageDate_ = null;
  private String oligoPoolId_ = null;
  private String bundleId_ = null;
  private String status_ = null;

  private String[] calls_ = null;
  private String[] scoreCalls_ = null;

  public LocusXDnaReportSampleDataEntry(String callLine, String scoreCallLine) {
    try {
      int end = callLine.indexOf(',');
      this.instituteLabel_ = callLine.substring(0, end);

      int start = end + 1;
      end = callLine.indexOf(',', start);
      this.plateWell_ = callLine.substring(start, end);

      start = end + 1;
      end = callLine.indexOf(',', start);
      this.imageDate_ = SDF.parse(callLine.substring(start, end));
      
      start = end + 1;
      end = callLine.indexOf(',', start);
      this.oligoPoolId_ = callLine.substring(start, end);

      start = end + 1;
      end = callLine.indexOf(',', start);
      this.bundleId_ = callLine.substring(start, end);

      start = end + 1;
      end = callLine.indexOf(',', start);
      this.status_ = callLine.substring(start, end);

      // This is the index of the start of the recordType column (which is the same in both lines)
      int recordTypeStart = start = end + 1;
      
      int callsStart = recordTypeStart + "calls,,".length();
      this.calls_ = callLine.substring(callsStart).split(",");

      int scoreStart = recordTypeStart + "Score_Call,,".length();
      this.scoreCalls_ = scoreCallLine.substring(scoreStart).split(",");
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }
  
  public String getInstituteLabel() {
    return instituteLabel_;
  }
  public String getPlateWell() {
    return plateWell_;
  }
  public Date getImageDate() {
    return imageDate_;
  }
  public String getOligoPoolId() {
    return oligoPoolId_;
  }
  public String getBundleId() {
    return bundleId_;
  }
  public String getStatus() {
    return status_;
  }
  public String[] getCalls() {
    return calls_;
  }
  public String[] getScoreCalls() {
    return scoreCalls_;
  }

}
