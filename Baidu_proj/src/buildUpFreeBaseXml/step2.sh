#!/bin/bash
cat ../step1/out/* > ../step1/out/total
sort -k1,1 -t $'\t' ../step1/out/total | java PipeLine | sort -nrk2,2 -t $'\t' > ../step1/out/aggregate &