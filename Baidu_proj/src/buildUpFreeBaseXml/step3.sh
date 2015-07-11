#!/bin/bash
#counts=(2 5 10 100 200 500 1000)
#for count in ${counts[@]}
#do
count=$1
  java -jar CompressRun.jar /remote/curtis/baidu/mingyanl/0629/code/step1/out/aggregate ${count} compress/mapping_${count} compress/count_${count} ../step1/uniq
  sort -t $'\t' -rnk2,2 compress/count_${count} > compress/sort_${count}
#done