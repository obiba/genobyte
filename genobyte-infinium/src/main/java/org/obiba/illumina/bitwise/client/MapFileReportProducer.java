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

import java.io.PrintStream;

import org.obiba.bitwise.FieldValueIterator;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.bitwise.util.BitVectorQueryResult;
import org.obiba.genobyte.cli.CliContext;
import org.obiba.genobyte.cli.CliContext.QueryExecution;
import org.obiba.genobyte.cli.ReportCommand.ReportProducer;
import org.obiba.genobyte.model.Chromosome;
import org.obiba.illumina.bitwise.AssayStore;
import org.obiba.illumina.bitwise.InfiniumGenotypingStore;


/**
 * Produces a SNP report in the mapfile format compatible with plink
 */
public class MapFileReportProducer implements ReportProducer {

  public String getReportType() {
    return "mapfile";
  }

  public boolean requiresOpenStore() {
    return true;
  }

  public void generateReport(CliContext context, String[] parameters, PrintStream output) {
    AssayStore store = ((InfiniumGenotypingStore)context.getStore()).getAssayRecordStore();
    QueryExecution qe = ReportProducerUtil.resolveParameter(context, parameters, 0);
    QueryResult assays = null;
    if(qe == null) {
      assays = new BitVectorQueryResult(store.getStore().all());
    } else {
      assays = qe.getResult();
    }

    context.getOutput().println("Producing mapfile report on "+assays.count()+" assays.");

    FieldValueIterator<Chromosome> chromosomes = new FieldValueIterator<Chromosome>(store.getStore().getField("chromosome"), assays);
    FieldValueIterator<String> names = new FieldValueIterator<String>(store.getStore().getField("snpName"), assays);
    FieldValueIterator<Integer> positions = new FieldValueIterator<Integer>(store.getStore().getField("mapInfo"), assays);

    StringBuilder sb = new StringBuilder();
    while(chromosomes.hasNext()) {
      Chromosome chr = chromosomes.next().getValue();
      String name = names.next().getValue();
      Integer position = positions.next().getValue();
      sb.append(outputChromosome(chr)).append(' ')
        .append(name).append(' ')
        .append(position);
      output.println(sb.toString());
      sb.setLength(0);
    }
    output.flush();
  }

  private String outputChromosome(Chromosome chr) {
    if(chr == Chromosome.chrBad) return "0";
    return chr.toString().substring(3);
  }

}
