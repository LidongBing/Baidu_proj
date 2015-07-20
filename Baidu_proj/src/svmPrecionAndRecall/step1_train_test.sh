#!/bin/bash
percents=('25.0' '50.0' '75.0' '100.0')
top_sols=(100 200 400 800 1200 2000 3000 5000)
labels=('1' '2' '3')
vect_path=../../generate_feature_vector/diff-runs

if [ ! -d "../model" ]; then
  mkdir ../model
fi
if [ ! -d "../sys_print" ]; then
  mkdir ../sys_print
fi
if [ ! -d "../test_out" ]; then
  mkdir ../test_out
fi
for ((run=${1}; run<=${2}; run++))
do
  for i in ${percents[@]}
  do
    echo "run$run percents$i"
    for top in ${top_sols[@]}
    do
      for label in ${labels[@]}
      do
          /remote/curtis/baidu/package/libsvm-3.20/./svm-train -t 0 ${vect_path}/${run}/FB_seed_${i}p/top${top}_sol_single_class_posi${label}_ALL.vec ../model/run${run}_per${i}p_top${top}_posi${label}.model >> ../sys_print/train
          echo run${run}_per${i}p_top${top}_posi${label} >> ../sys_print/run${run}
          /remote/curtis/baidu/package/libsvm-3.20/./svm-predict ${vect_path}/${run}/FB_seed_forTestList/FB_seed_${i}p/top500_train_${top}_sol_single_class_posi${label}_ALL.vec ../model/run${run}_per${i}p_top${top}_posi${label}.model ../test_out/run${run}_per${i}p_top${top}_posi${label}.out >> ../sys_print/run${run}
      done
    done
  done
done