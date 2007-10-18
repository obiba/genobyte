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
package org.obiba.illumina.bitwise.client;

import org.obiba.bitwise.Field;
import org.obiba.genobyte.GenotypingRecordStore;
import org.obiba.genobyte.model.SnpAllele;
import org.obiba.genobyte.report.AbstractPedFileReport;
import org.obiba.genobyte.report.AbstractPedFileReport.Column;
import org.obiba.illumina.bitwise.model.Sample.Gender;


/**
 * Implementation of the ped file report for the infinium data model.
 */
public class PedFileReport extends AbstractPedFileReport {

  public PedFileReport() {
    // We don't link the FAMILY_ID Column

    linkColumnToField(Column.INDIVIDUAL_ID, "id");
    linkColumnToField(Column.PATERNAL_ID, "parent1");
    linkColumnToField(Column.MATERNAL_ID, "parent2");
    linkColumnToField(Column.GENDER, "gender");
    linkColumnToField(Column.AFFECTION_STATUS, "group");
  }
  
  @Override
  protected SnpAllele[] getAssayAlleles(GenotypingRecordStore assays, int index) {
    Field alleleA = assays.getStore().getField("alleleA");
    Field alleleB = assays.getStore().getField("alleleB");
    SnpAllele[] alleles = new SnpAllele[2];
    alleles[0] = (SnpAllele) alleleA.getDictionary().reverseLookup(alleleA.getValue(index));
    alleles[1] = (SnpAllele) alleleB.getDictionary().reverseLookup(alleleB.getValue(index));
    return alleles;
  }

  @Override
  protected Object convertColumnValue(Column c, Object value) {
    switch (c) {
      case GENDER:
        Gender g = (Gender)value;
        switch(g) {
          case Male: return "1";
          case Female: return "2";
          default: return "0";
        }
      case AFFECTION_STATUS:
        String group = (String)value;
        if(group != null && group.equalsIgnoreCase("AFFECTED")) {
          return "2";
        }
        if(group != null && group.equalsIgnoreCase("NOT_AFFECTED")) {
          return "1";
        }
        return c.getMissingValueToken();
    }
    return value;
  }

}