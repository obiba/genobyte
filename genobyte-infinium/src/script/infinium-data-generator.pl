#!/usr/bin/perl

use strict;
use POSIX;

my $SEED = "2";
my $OUTDIR = "../data";

# SNP count
# 300k v2
#my $NB_MARKER = 318237;
# 300k v1
my $NB_MARKER = 317503;

# Genome constants
my $SAMPLE_REPLICATE_PERCENTAGE = 0.03;
my $SAMPLE_FAILED_PERCENTAGE = 0.02;
my $MARKER_FAILED_PERCENTAGE = 0.01;
my $FAILED_SAMPLE_U_THRESHOLD = 0.4;
my $FAILED_MARKER_U_THRESHOLD = 0.4;
my $SAMPLE_REPRO_ERROR_PERCENTAGE = 0.01;
my $CALL_RATE = 0.995;
my $GENCALL_THRESHOLD = 0.25;

my $GENERATE_LOCUS_X_DNA_REPORT = 1;

# Sample Sheet constants
my $PLATE_BARCODE_PREFIX = "SIM";
my $SAMPLES_PER_PLATE = 95;
my $CONTROLS_PER_PLATE = 1;

my @COLUMNS = ("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12");
my @ROWS = ("A", "B", "C", "D", "E", "F", "G", "H");
my @WELLS = ();
for(my $r = 0; $r < @ROWS; $r++) {
   for(my $c = 0; $c < @COLUMNS; $c++) {
     push(@WELLS, $ROWS[$r].$COLUMNS[$c]);
   }
}

# Create an array of failed markers
my $rA_failedMarker = GetFailedMarker();
# Create a genome used to create replicates
my ($rA_REFERENCE_GENOME, $rA_REFERENCE_SCORE) = GetGenotypeArray(1, $rA_failedMarker) if($GENERATE_LOCUS_X_DNA_REPORT == 1);

main(@ARGV);

sub main {
   srand($SEED);
   my $numSamples = shift;
   
   mkdir $OUTDIR;

   my $numPlates = ceil($numSamples/$SAMPLES_PER_PLATE);

   print "Generating $numPlates plates\n";
   my $firstSampleId = 0;
   for(my $i = 0; $i < $numPlates; $i++) {
      my $lastSampleId = $firstSampleId + $SAMPLES_PER_PLATE;
      $lastSampleId = $lastSampleId > $numSamples ? $numSamples : $lastSampleId;
      GeneratePlate($i, $firstSampleId, $lastSampleId);
      $firstSampleId += $SAMPLES_PER_PLATE;
   }
}

sub GeneratePlate() {
   my $plateId = shift;
   my $firstId = shift;
   my $lastId = shift;

   print "Generating plate " . ($plateId+1) . "\n";

   my $plateUpc=$PLATE_BARCODE_PREFIX . int(rand() * 10000);

   open (my $fh, ">$OUTDIR/samples-" . ($plateId+1) . ".csv") || die();
   open (my $ldr, ">$OUTDIR/genotypes-" . ($plateId+1) . ".csv") || die();

   # Print plate header
   print $fh "[Manifests]\n";
   print $fh "A,HumanHap300_(v1.0.0)\n";
   print $fh "[Data]\n";
   print $fh "Sample_ID,Sample_Plate,Sample_Well,SentrixBarcode_A,SentrixPosition_A,Gender,Sample_Group,Reference,Parent1,Parent2\n";

   # Print LocusXDNA report header
   if($GENERATE_LOCUS_X_DNA_REPORT == 1) {
      my $numSamples = $lastId - $firstId + $CONTROLS_PER_PLATE;
      open (my $ldr_header, 'ldr_header.txt') || die("Cannot open ldr_header.txt file.");
      while(<$ldr_header>) {
        s/\<NUM_SAMPLES\>/$numSamples/;
        print $ldr $_;
      }
      close $ldr_header;
   }

   for(my $i = $firstId; $i < $lastId; $i++) {
      my $r = rand();
      my $sampleId = $i + 1;
      my $chipUpc=int($r * 10000000000);
      my $gender=$r < 0.5 ? "Female" : "Male";
      my $ds=rand() < 0.5 ? "AFFECTED" : "NOT_AFFECTED";
      my $mother="";
      my $father="";
      my $well=@WELLS[$i-$firstId];
      my $reference="";
   
      my $state = (rand() <= $SAMPLE_FAILED_PERCENTAGE) ? 0 : 1;
      my ($rA_genotype, $rA_score) = GetGenotypeArray($state, $rA_failedMarker) if($GENERATE_LOCUS_X_DNA_REPORT == 1);

      print $fh "$sampleId,$plateUpc,$well,$chipUpc,A,$gender,$ds,$reference,$mother,$father\n";
      print $ldr "$sampleId,$plateUpc$well,Mar 21 2006,HumanHap300_(v1.0.0),${chipUpc}_A,0,calls,," . join(",", @$rA_genotype) . "\n" if($GENERATE_LOCUS_X_DNA_REPORT == 1);
      print $ldr "$sampleId,$plateUpc$well,Mar 21 2006,HumanHap300_(v1.0.0),${chipUpc}_A,0,Score_Call,," . join(",", @$rA_score) . "\n" if($GENERATE_LOCUS_X_DNA_REPORT == 1);
   }

   my $controlId = "NA12236." . ($plateId + 1);
   my $chipUpc = int(rand() * 10000000000);
   # create a replicate of the first control on the subsequent plates
   my $isReplicate = $plateId > 0;
   my $reference = $isReplicate == 1 ? "NA12236.1" : "";

   my ($rA_genotype, $rA_score) = ($rA_REFERENCE_GENOME, $rA_REFERENCE_SCORE) if($GENERATE_LOCUS_X_DNA_REPORT == 1 && $isReplicate == 0);
   ($rA_genotype, $rA_score) = GetReplicateGenotype($rA_REFERENCE_GENOME) if ($GENERATE_LOCUS_X_DNA_REPORT == 1 && $isReplicate == 1);

   print $fh "$controlId,$plateUpc,H12,$chipUpc,A,Male,CONTROL,$reference,,\n";
   print $ldr "$controlId,${plateUpc}H12,Mar 21 2006,HumanHap300_(v1.0.0),${chipUpc}_A,0,calls,," . join(",", @$rA_genotype) . "\n" if($GENERATE_LOCUS_X_DNA_REPORT == 1);
   print $ldr "$controlId,${plateUpc}H12,Mar 21 2006,HumanHap300_(v1.0.0),${chipUpc}_A,0,Score_Call,," . join(",", @$rA_score) . "\n" if($GENERATE_LOCUS_X_DNA_REPORT == 1);

   close $fh;
   close $ldr;
}

# Get an array of genotypes and an array of scores per genotype with the specified failed marker set.
sub GetGenotypeArray {
  my $sampleState = shift;
  my $rA_failedMarker = shift;
  
  my $probOfU;
  my @genotype;
  my @score;

  # U distribution according to sample state
  if ($sampleState == 0) {
     $probOfU = 1 - rand($FAILED_SAMPLE_U_THRESHOLD);
  } else {
     $probOfU = rand(1-$CALL_RATE);
  }

  for (my $i = 0; $i < $NB_MARKER; $i++) {
     ($genotype[$i], $score[$i]) = GetAllele($probOfU);
  }
  
  # Failed markers
  my $nbFailedMarker = @$rA_failedMarker;
  for (my $j = 0; $j < $nbFailedMarker; $j++) {
     $genotype[$$rA_failedMarker[$j]] = "U" if (rand() >= $FAILED_MARKER_U_THRESHOLD);
  }
  return (\@genotype, \@score);
}


#   A, B, H, U calls
#   p(A) = p(B) = 2*p(H)
sub GetAllele {
   my $probOfU = shift;
   my $tmp = rand();
   my $score = sprintf("%.4f", rand($GENCALL_THRESHOLD));
   return "U",$score if ($tmp <= $probOfU);
   
#  Good score is >= $GENCALL_THRESHOLD
   $score = 1 - $score;

#  Remove the "U" portion of the probability
   $tmp -= $probOfU;
   return ("H",$score) if ($tmp <= (1-$probOfU)/2);
   return ("A",$score) if ($tmp <= 3*(1-$probOfU)/4);
   return ("B",$score);  
}


#   Get a list of failed markers
sub GetFailedMarker {
   my @failedMarker = ();

   for (my $i = 0; $i < $NB_MARKER; $i++) {
      push(@failedMarker, $i) if (rand() <= $MARKER_FAILED_PERCENTAGE);
   } 
   return \@failedMarker;
}

# Generate a replicate genome with reproducibility errors
sub GetReplicateGenotype {
   my $rA_refGenotype = shift;
   my @genotype = ();
   my @score = ();
   
   foreach my $allele (@$rA_refGenotype) {
      my $repAllele = rand() <= $SAMPLE_REPRO_ERROR_PERCENTAGE ? GetMissMatchAllele($allele): $allele;
      my $score = sprintf("%.4f", rand($GENCALL_THRESHOLD));
      my $repScore = $repAllele eq "U" ? $score : 1 - $score;
      push (@genotype, $repAllele);
      push (@score, $repScore);
   }
   return (\@genotype, \@score);
}

# Returns a genotype that doesn't match the parameter
sub GetMissMatchAllele {
   my $allele = shift; 
   return "B" if ($allele eq "A");
   return "A" if ($allele eq "B");   
   return "B" if ($allele eq "H");
   return "A" if ($allele eq "U");
}
