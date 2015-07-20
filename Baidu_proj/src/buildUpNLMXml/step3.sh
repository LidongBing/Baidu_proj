#!/bin/bash
#counts=(2 5 10 100 200 500 1000)
#for count in ${counts[@]}
#do
if [ ! -d "compress_${1}" ]
then
  mkdir "compress_${1}"
fi
javac Step3_CompressGenerator.java
java Step3_CompressGenerator ${1} ../step2/count_reduced ../step2/mapping_uniq compress_${1}/mapping_${1} compress_${1}/count_${1}
sort -t $'\t' -rnk2,2 compress_${1}/count_${1} > compress_${1}/count_sorted_${1}
#done