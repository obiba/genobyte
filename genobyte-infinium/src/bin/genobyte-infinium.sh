#!/bin/bash

depdirs=".. ../lib . ./lib";
cp="";

cd ..
for depdir in $depdirs
do
  for dep in `ls $depdir/*.jar`
  do
    cp=$dep:$cp;
  done
done

java -Xmx1800M -cp $cp org.obiba.illumina.bitwise.InfiniumApp

