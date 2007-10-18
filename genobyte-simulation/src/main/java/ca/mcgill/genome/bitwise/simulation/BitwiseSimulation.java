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
package ca.mcgill.genome.bitwise.simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.obiba.bitwise.FieldValueIterator;
import org.obiba.bitwise.util.BdbPropertiesProvider;
import org.obiba.bitwise.util.StringUtil;
import org.obiba.genobyte.GenotypingRecordStore;
import org.obiba.genobyte.cli.BitwiseCli;
import org.obiba.genobyte.model.Chromosome;
import org.obiba.genobyte.model.DefaultGenotypingField;
import org.obiba.genobyte.model.Orientation;
import org.obiba.genobyte.model.SnpAllele;
import org.obiba.genobyte.model.SnpCall;
import org.obiba.genobyte.model.SnpGenotype;
import org.obiba.genobyte.statistic.DefaultAssayStatsRunDefinition;
import org.obiba.genobyte.statistic.StatsPool;
import org.obiba.genobyte.statistic.StatsRunDefinition;

import ca.mcgill.genome.util.ProgressIndicator;
import ca.mcgill.genome.util.ThroughputCalculator;
import ca.mcgill.genome.util.ThroughputDisplay;

public class BitwiseSimulation {

  SimulatedStore project_ = null;
  
  List<SnpGenotype<Integer>> genotypeList = null;

  public BitwiseSimulation() {
    super();
  }

  public void open() {
    project_ = SimulatedStore.open();
    if(project_ == null) {
      System.err.println("Cannot open store");
    }
  }

  public void close() {
    if(project_ != null) project_.close();
  }

  public void updateStats() {
    System.out.println("Updating assay stats");
    project_.getAssayRecordStore().updateStats();
    System.out.println("Updating sample stats");
    project_.getSampleRecordStore().updateStats();
  }
  
  public void create(File assayFile, int sampleSize) throws IOException {
    MarkerFileParser markers = MarkerFileParser.parse(assayFile);
    int assaySize = markers.getSize();
    try {
      project_ = SimulatedStore.create(sampleSize, assaySize);
      project_.startTransaction();
      int i = 0;
      long start = System.currentTimeMillis();
      System.out.println("Creating "+assaySize+" assays.");
      List<MarkerFileParser.MarkerFileEntry> parser = markers.entries();
      for (MarkerFileParser.MarkerFileEntry entry : parser) {
        project_.getAssayManager().insert(readAssay(entry));
        printProgress(++i, assaySize);
      }
      project_.flush();
      System.out.println(" done in " + (System.currentTimeMillis() - start)/1000d + " seconds");
      project_.commitTransaction();
    } finally {
      if(project_ != null) project_.endTransaction();
    }
  }

  public void load(File sampleFile, Genomes genomes, Genomes replicateGenomes) throws IOException {
    LinkageFileParser samples = LinkageFileParser.parse(sampleFile);
    samples.setGenomes(genomes);
    samples.setReplicateGenomes(replicateGenomes);
    try {
      int sampleSize = samples.getSize();
      int currentSize = project_.getSampleRecordStore().getStore().getSize();
      project_.startTransaction();

      int sampleCount = sampleSize + currentSize;
      Progress p = new Progress();
      p.todo = sampleSize;
      ThroughputCalculator calc = new ThroughputCalculator(p);
      calc.setSamplingRate(2);
      calc.setSamplingWindow(60);
      ThroughputDisplay d = new ThroughputDisplay(calc, System.out);
      System.out.println("New sample count will be "+ sampleCount);
      project_.ensureSampleCapacity(sampleCount);

      System.out.println("Creating samples.");
      calc.start(0);
      d.start();
      List<LinkageFileParser.LinkageFileEntry> entries = samples.entries();
      int i = 0;
      for (LinkageFileParser.LinkageFileEntry entry : entries) {
        SimulatedSample s = readSample(currentSize, entry);
        project_.getSampleManager().insert(s);
        project_.getSampleRecordStore().setTransposedValues(DefaultGenotypingField.CALLS.getName(), s.getId(), s.getGenotypes());
        p.done = ++i;
        if(i % 100 == 0) {
          project_.flush();
        }
      }
      calc.stop();
      d.stop();
      project_.commitTransaction();
    } finally {
      project_.endTransaction();
    }
  }

  private void transpose(int from, int to) {
    Set<Integer> samples = new TreeSet<Integer>();
    FieldValueIterator<Integer> fvi = project_.getSampleManager().keys();
    while(fvi.hasNext()) {
      FieldValueIterator<Integer>.FieldValue fv = fvi.next();
      if(fv.getIndex() >= from && fv.getIndex() < to) {
        Integer sampleKey = fv.getValue();
        samples.add(sampleKey);
      }
    }

    try {
      project_.startTransaction();
      project_.getSampleRecordStore().tranpose(samples);
      project_.commitTransaction();
    } finally {
      project_.endTransaction();
    }
  }

  private SimulatedSample readSample(int offset, LinkageFileParser.LinkageFileEntry entry) {
    SimulatedSample s = new SimulatedSample();

    s.setId(offset+entry.getIndex());
    s.setName(entry.getSampleName());
    s.setIndividualName(entry.getIndividualName());
    s.setMotherName(entry.getMotherName());
    s.setFatherName(entry.getFatherName());
    s.setDiseaseStatus(entry.isCase() ? DiseaseStatus.AFFECTED : DiseaseStatus.NOT_AFFECTED);

    String genotypeString = entry.getGenotypes();
    StringCharacterIterator sci = new StringCharacterIterator(genotypeString);
    char c = sci.first();
    if(genotypeList == null) {
      genotypeList = new ArrayList<SnpGenotype<Integer>>(genotypeString.length());
      for(int i = 0; i < genotypeString.length(); i++) {
        genotypeList.add(new SnpGenotype<Integer>());
      }
    }
    int i = 0;
    while(c != StringCharacterIterator.DONE) {
      SnpGenotype<Integer> sg = genotypeList.get(i);
      sg.setTransposedKey(i++);
      sg.setValue(SnpCall.valueOf(c));
      c = sci.next();
    }
    s.setGenotypes(this.genotypeList);
    return s;
  }

  private SimulatedAssay readAssay(MarkerFileParser.MarkerFileEntry entry) {
    SimulatedAssay a = new SimulatedAssay();
    a.setId(entry.getIndex());
    a.setName(entry.getName());
    a.setSnp(entry.getMarkerName());
    if(StringUtil.isEmptyString(entry.getGenes()) == false) {
      a.setGenes(entry.getGenes());
    }
    try {
      a.setAlleleA(SnpAllele.valueOf(entry.getAlleleA()));
      a.setAlleleB(SnpAllele.valueOf(entry.getAlleleB()));
    } catch (IllegalArgumentException e) {
//      System.err.println("Invalid allele for assay=["+a.getName()+"]: " + e.getMessage());
      a.setAlleleA(SnpAllele.C);
      a.setAlleleB(SnpAllele.T);
    }
    if(entry.getStrand() != null) a.setOrientation(Orientation.parseSign(entry.getStrand()));

    Chromosome chr = Chromosome.chrBad;;
    try {
      chr = Chromosome.valueOf("chr"+entry.getChromosome());
    } catch (RuntimeException e) {}
    a.setChromosome(chr);
    a.setPosition(entry.getPosition());
    return a;
  }


  NumberFormat pct = null;
  private void printProgress(int done, int todo) {
    if(pct == null) {
      pct = NumberFormat.getPercentInstance();
      pct.setMinimumFractionDigits(2);
      pct.setMaximumFractionDigits(2);
    }
    double p = (done / (double)todo);
    System.out.print("\r" + pct.format(p) + " ("+done+")");
  }
  
  
  private static class Progress implements ProgressIndicator {
    long done = 0;
    long todo = 0;
    public long getProcessedItems() {
      return done;
    }
    public long getTotalItems() {
      return todo;
    }
  }


  /**
   * Prepares a bogus report with various data from the store.
   */
  private void generateReport() {
    GenotypingRecordStore rs = project_.getAssayRecordStore();
    
    StatsRunDefinition runDef = new DefaultAssayStatsRunDefinition();
    StatsPool<Integer,Integer> pool = new StatsPool<Integer,Integer>(rs, runDef);
   
//    //Prepare record filter
//    BitVector recordMask = new BitVector(rs.getStore().getSize());
//    recordMask.set(1);
//    recordMask.set(5);
//    recordMask.set(10);
//    recordMask.set(99);
//    pool.setRecordMask(new BitVectorQueryResult(recordMask));
//   
//    //Prepare transposed record filter
//    BitVector transposedMask = new BitVector(rs.getStore().getSize());
//    transposedMask.set(0);
//    transposedMask.set(1);
//    transposedMask.set(2);
//    transposedMask.set(3);
//    transposedMask.set(4);
//    pool.setTransposedMask(new BitVectorQueryResult(transposedMask));
   
    pool.calculate();
    
    //Print the report into a CSV file.
    PrintStream printer;
    try {
      printer = new PrintStream("wow_report.csv");
    }
    catch (Exception e) {
      throw new RuntimeException("Didn't work.");
    }
    FrequenciesReport digester = new FrequenciesReport();
    digester.setOutput(printer);
    digester.digest(pool);
  }


  public static void main(String[] args) throws FileNotFoundException, IOException {
    boolean create = false;
    boolean stats = false;
    boolean mendel = false;
    boolean repro = false;
    boolean query = false;
    boolean transpose = false;
    boolean report = false;
    int from = -1;
    int to = -1;

    String samplefile = null;
    int sampleSize = 0;
    String markerfile = null;
    String genomefile = null;
    String repGenomefile = null;

    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      if(arg.equalsIgnoreCase("--create")) {
        create = true;
      } else if(arg.equalsIgnoreCase("--stats")) {
        stats = true;
      } else if(arg.equalsIgnoreCase("--mendel")) {
        mendel = true;
      } else if(arg.equalsIgnoreCase("--repro")) {
        repro = true;
      } else if(arg.equalsIgnoreCase("--query")) {
        query = true;
      } else if(arg.equalsIgnoreCase("--load")) {
        samplefile = args[++i];
      } else if(arg.equalsIgnoreCase("--sampleSize")) {
        sampleSize = Integer.parseInt(args[++i]);
      } else if(arg.equalsIgnoreCase("--marker")) {
        markerfile = args[++i];
      } else if(arg.equalsIgnoreCase("--genomes")) {
        genomefile = args[++i];
      } else if(arg.equalsIgnoreCase("--repGenomes")) {
        repGenomefile = args[++i];
      } else if(arg.equalsIgnoreCase("--transpose")) {
        transpose = true;
        from = Integer.parseInt(args[++i]);
        to = Integer.parseInt(args[++i]);
      } else if(arg.equalsIgnoreCase("--report")) {
        report = true;
      } 
    }

    BdbPropertiesProvider.setAsProvider();

    BitwiseSimulation importer = new BitwiseSimulation();

    if(create == true) {
      File m = null;
      if(markerfile != null) {
        m = new File(markerfile);
      }
      if(m == null || m.exists() == false) {
        throw new IllegalArgumentException("Marker file ["+markerfile+"] does not exist.");
      }
      importer.create(m, sampleSize);
    } else {
      importer.open();
    }

    if(StringUtil.isEmptyString(samplefile) == false) {
      Genomes g = new Genomes(new File(genomefile));
      Genomes r = new Genomes(new File(repGenomefile));
      importer.load(new File(samplefile), g, r);
    }

    if(stats == true) {
      importer.project_.updateStats();
    }
    if(mendel == true) {
      importer.project_.mendel();
    }
    if(repro == true) {
      importer.project_.reproDna();
      importer.project_.reproAssay();
    }

    if(transpose == true) {
      importer.transpose(from, to);
    }
    
    if(report == true) {
      importer.generateReport();
    }

    if(query == true) {
      BitwiseCli cli = new BitwiseCli();
//      cli.registerCommand(new OpenSimulationStoreCommand());
      cli.execute();
    }

    importer.close();
  }
  
//  static private class OpenSimulationStoreCommand extends OpenCommand {
//    @Override
//    protected GenotypingStore<?, ?, ?, ?> openStore(BitwiseStore samples, BitwiseStore assays) {
//      return new SimulatedStore(samples, assays);
//    }
//  }

}
