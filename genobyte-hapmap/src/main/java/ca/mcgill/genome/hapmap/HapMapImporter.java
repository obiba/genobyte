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
package ca.mcgill.genome.hapmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.obiba.genobyte.model.Chromosome;
import org.obiba.genobyte.model.DefaultGenotypingField;
import org.obiba.genobyte.model.Orientation;
import org.obiba.genobyte.model.SnpAllele;
import org.obiba.genobyte.model.SnpCall;
import org.obiba.genobyte.model.SnpGenotype;


public class HapMapImporter {

  String population_ = null;
  HapMapGenotypingProject store_ = null;

  public HapMapImporter(String population) {
    super();
    population_ = population;
  }

  public void open() {
    store_ = HapMapGenotypingProject.open(population_);
  }
  
  public void close() {
    store_.close();
  }

  public void createPopulation(File file) throws FileNotFoundException, IOException {
    HapMapGenotypeFileParser parser = new HapMapGenotypeFileParser(file);
    store_ = HapMapGenotypingProject.create(population_);
    store_.ensureSampleCapacity(parser.sampleCount());
    store_.ensureAssayCapacity(parser.assayCount());
    try {
      store_.startTransaction();
      List<String> samples = parser.getSamples();
      for (Iterator iter = samples.iterator(); iter.hasNext();) {
        String sampleName = (String) iter.next();
        HapMapSample s = new HapMapSample();
        s.setName(sampleName);
        store_.getSampleManager().insert(s);
      }
      store_.commitTransaction();
    } finally {
      store_.endTransaction();
    }
  }
  

  public void importFile(File file) {
    try {
      store_.startTransaction();
      HapMapGenotypeFileParser parser = new HapMapGenotypeFileParser(file);
      store_.ensureSampleCapacity(parser.sampleCount());
      int newAssayCount = parser.assayCount();
      int processedAssayCount = 0;
      int skipped = 0;
      store_.ensureAssayCapacity(store_.getAssayCount() + newAssayCount);

      Iterator<HapMapGenotypeFileParser.Assay> assays = parser.iterator();
      while (assays.hasNext()) {
        HapMapGenotypeFileParser.Assay assay =  assays.next();
        printProgress(processedAssayCount++, newAssayCount);
        if(assay.getAlleles().length() != 3) {
//          System.err.println("Skipping snp=["+assay.getRs()+"]: expected exactly two alleles ["+assay.getAlleles()+"]");
          skipped++;
          continue;
        }

        String alleleA = assay.getAlleles().substring(0,1);
        String alleleB = assay.getAlleles().substring(2);

        HapMapAssay a = new HapMapAssay();
        try {
          a.setAlleleA(SnpAllele.valueOf(alleleA));
          a.setAlleleB(SnpAllele.valueOf(alleleB));
        } catch (IllegalArgumentException e) {
//          System.err.println("Skipping snp=["+assay.getRs()+"]: found at least one invalid alleles in ["+assay.getAlleles()+"]");
          skipped++;
          continue;
        }

        a.setSnp(assay.getRs());
        a.setOrientation(Orientation.parseSign(assay.getStrand()));
        a.setProtLsid(assay.getProtLsid());
        a.setAssayLsid(assay.getAssayLsid());
        a.setPanelLsid(assay.getPanelLsid());
        a.setQcCode(QcCode.parse(assay.getQcCode()));
        a.setCenter(assay.getCenter());
        a.setChromosome(Chromosome.valueOf(assay.getChrom()));
        a.setPosition(assay.getPos());
        a.setMap(Map.valueOf(assay.getGenome_build()));

        for (Iterator iter = assay.getGenotypes().iterator(); iter.hasNext();) {
          HapMapGenotypeFileParser.Genotype genotype = (HapMapGenotypeFileParser.Genotype) iter.next();
          SnpAllele allele1 = SnpAllele.valueOf(genotype.getGenotype().substring(0,1));
          SnpAllele allele2 = SnpAllele.valueOf(genotype.getGenotype().substring(1));
          SnpGenotype<String> ag = new SnpGenotype<String>();
          SnpCall call = SnpCall.U;
          if(allele1 != allele2) {
            call = SnpCall.H;
          } if(allele1 == a.getAlleleA() && allele2 == a.getAlleleA()) {
            call = SnpCall.A;
          } if(allele1 == a.getAlleleB() && allele2 == a.getAlleleB()) {
            call = SnpCall.B;
          }
          ag.setTransposedKey(genotype.getSampleName());
          ag.setValue(call);
          a.addGenotype(ag);
        }

        if(a.getGenotypes() != null) {
          try {
            store_.getAssayManager().insert(a);
            store_.getAssayRecordStore().setTransposedValues(DefaultGenotypingField.CALLS.getName(), a.getId(), a.getGenotypes());
          } catch (RuntimeException e) {
            System.err.println("Assay ["+a.getSnp()+"] has errors.");
            throw e;
          }
        } else {
          System.err.println("Assay ["+a.getSnp()+"] has no genotypes");
        }
      }
      store_.commitTransaction();
      System.out.println("\r100% Done.");
      System.out.println("Skipped " + skipped + " assays due to wierd alleles.");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      store_.endTransaction();
    }
  }
  
  private void printProgress(int done, int todo) {
    int p = (int)(done / (double)todo * 100);
    System.out.print("\r" + p + "%");
  }

  public static void main(String[] args) throws FileNotFoundException, IOException {
    List<String> filenames = new LinkedList<String>();
    String populationName = null;
    boolean importData = false;
    boolean create = false;
    boolean stats = false;
    boolean mendel = false;
    boolean repro = false;

    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      if(arg.equalsIgnoreCase("--import")) {
        importData = true;
      } else if(arg.equalsIgnoreCase("--create")) {
        create = true;
      } else if(arg.equalsIgnoreCase("--pop")) {
        populationName = args[++i];
      } else if(arg.equalsIgnoreCase("--stats")) {
        stats = true;
      } else if(arg.equalsIgnoreCase("--mendel")) {
        mendel = true;
      } else if(arg.equalsIgnoreCase("--repro")) {
        repro = true;
      } else {
        filenames.add(args[i]);
      }
    }

    if(populationName == null) {
      throw new IllegalArgumentException("No population name specified");
    }
    if(filenames.size() == 0 && importData == true) {
      throw new IllegalArgumentException("No file to import.");
    }

    HapMapImporter importer = new HapMapImporter(populationName);
    if(importData == true) {
      if(create == true) {
        importer.createPopulation(new File(filenames.get(0)));
      } else {
        importer.open();
      }
      for (String filename : filenames) {
        System.out.println("Importing file=["+filename+"]");
        importer.importFile(new File(filename));
      }
    }
    
    if(stats == true || mendel == true || repro == true) {
      importer.open();
    }

    if(stats == true) {
      importer.store_.updateStats();
    }
    if(mendel == true) {
      importer.store_.mendel();
    }
    if(repro == true) {
      importer.store_.reproDna();
      importer.store_.reproAssay();
    }
    importer.close();
  }
}
