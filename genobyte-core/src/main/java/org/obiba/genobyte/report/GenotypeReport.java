/**
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
 */
/**
 *
 */
package org.obiba.genobyte.report;

import java.io.File;

import org.obiba.bitwise.query.QueryResult;
import org.obiba.genobyte.GenotypingStore;

/**
 * Defines the common interface for outputing genotype reports.
 * <p/>
 * Implementations need to provide a unique report type and short report type values. These are used
 * to reference the implementation using a unique name (or unique char).
 * <br/>
 * Reports may output multiple files, specified by the {@link GenotypeReport#getNumberOfOutputFiles()} method. Each 
 * output file may have a default/preferred name which is fetched using the
 * {@link GenotypeReport#getDefaultFileName(int)} method.
 */
public interface GenotypeReport {

  /**
   * The unique name of the implementation.
   * @return a unique string that may be used to reference the implementation.
   */
  public String getReportType();

  /**
   * A char version of the unique name.
   * @return a unique char that may be used to reference the implementation. 
   */
  public char getShortReportType();

  /**
   * Returns the number of files this implementation produces.
   *
   * @return the number of files this implementation produces.
   */
  public int getNumberOfOutputFiles();

  /**
   * Returns the preferred/default filename of the <tt>i</tt>th output file (where i >= 0 and < {@link GenotypeReport#getNumberOfOutputFiles()}).
   *
   * @param i the output file index
   * @return the default/preferred filename of the <tt>i</tt>th output file.
   */
  public String getDefaultFileName(int i);

  /**
   * Generates the report using the specified store as the source of data. The mask parameters can be used
   * to output subsets of samples and assays in the genotype report. The last parameter is used to pass the output
   * files prepared by the calling mechanism.
   *
   * @param gs the store from which to obtain samples, assays and genotypes.
   * @param sampleMask the samples to include in the report.
   * @param assayMask the assays to include in the report.
   * @param outputFiles the array of files to use for outputing the report.
   */
  public void generateReport(GenotypingStore gs, QueryResult sampleMask, QueryResult assayMask, File... outputFiles);

}
