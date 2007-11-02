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

import org.obiba.genobyte.cli.CliContext;
import org.obiba.genobyte.cli.CliContext.QueryExecution;

class ReportProducerUtil {

  static QueryExecution resolveSampleQuery(CliContext context, String[] parameters, int index) {
    QueryExecution qe = resolveParameter(context, parameters, index);
    if(qe != null && qe.getStore() != context.getStore().getSampleRecordStore()) {
      throw new IllegalArgumentException("Invalid query reference: ["+parameters[index]+"] is not a query on the sample store.");
    }
    return qe;
  }

  static QueryExecution resolveAssayQuery(CliContext context, String[] parameters, int index) {
    QueryExecution qe = resolveParameter(context, parameters, index);
    if(qe != null && qe.getStore() != context.getStore().getAssayRecordStore()) {
      throw new IllegalArgumentException("Invalid query reference: ["+parameters[index]+"] is not a query on the assay store.");
    }
    return qe;
  }
  
  static QueryExecution resolveParameter(CliContext context, String[] parameters, int index) {
    if(parameters != null && index < parameters.length) {
      String reference = parameters[index];
      return context.getHistory().resolveQuery(reference);
    }
    return null;
  }

}
