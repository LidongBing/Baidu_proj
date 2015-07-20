#!/bin/bash
# get uniq mapping file
sort ../step1/mapping_repl | uniq > mapping_uniq
# put all count file into one
cat ../step1/out/* > ../step1/count_sparse
# map reduce count file
sort -k1,1 -t $'\t' ../step1/count_sparse | java Step2_AggregateCount | sort -nrk2,2 -t $'\t' > count_reduced