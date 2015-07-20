#! /bin/bash
percents=('25.0' '50.0' '75.0' '100.0')
top_sols=(100 200 400 800 1200 2000 3000 5000)
labels=('1' '2' '3')
vect_path=../../generate_feature_vector/diff-runs
	
for i in ${percents[@]}
do
  for top in ${top_sols[@]}
  do
  	filepath=../pre_recall/pre${i}_top${top}
	echo pre${i}_top${top} >> ../pre_recall/pre_recall_av
	awk 'BEGIN{pre=0;rec=0;f=0} {if($1=="pre:") pre+=$2; else if($1=="rec:") rec+=$2; else if($1=="F1:") f+=$2;} END{print "pre_av:", pre/FNR*4; print "rec_av:", rec/FNR*4; print "f_av:",f/FNR*4;}' ${filepath} >> ../pre_recall/pre_recall_av
  done
done