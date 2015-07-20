#!/bin/bash
percents=('25.0' '50.0' '75.0' '100.0')
top_sols=(100 200 400 800 1200 2000 3000 5000)
labels=('1' '2' '3')
vect_path=../../generate_feature_vector/diff-runs

if [ ! -d "../pre_recall" ]; then
  mkdir ../pre_recall
fi
for ((run=0; run<10; run++))
do
  for i in ${percents[@]}
  do
    for top in ${top_sols[@]}
    do
      for label in ${labels[@]}
      do
        test_file=${vect_path}/${run}/FB_seed_forTestList/FB_seed_${i}p/top500_train_${top}_sol_single_class_posi${label}_ALL.vec
        out_file=../test_out/run${run}_per${i}p_top${top}_posi${label}.out
        echo run${run}_per${i}p_top${top}_posi${label} >> ../pre_recall/pre${i}_top${top}
        awk 'BEGIN{TP=0;FN=0;FP=0} NR==FNR{a[FNR]=$1+0;next} {if(a[FNR]==1 && $1==1) TP++; else if(a[FNR]==1&& $1==-1) FN++; else if(a[FNR]==-1&& $1==1) FP++;} END{if(TP==0)p=0; else p=TP/(TP+FP); if(TP==0)r=0; else r=TP/(TP+FN); if(p==0&&r==0)f=0; else f=2*p*r/(p+r); print "pre:", p; print "rec:",r; print "F1:",f}' ${test_file} ${out_file} >> ../pre_recall/pre${i}_top${top}
        fi
      done
    done
  done
done