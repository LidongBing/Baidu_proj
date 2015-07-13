export PROPPR=/remote/curtis/baidu/ProPPR/ProPPR-srw2.0
export PATH=$PATH:$PROPPR/scripts

percents=('2.5' '7.5' '12.5' '25.0' '50.0' '75.0' '100.0')
type=('condition_this_may_prevent' 'disease_or_condition_caused' 'symptom_of' 'used_to_treat')

run_from=$1
run_to=$2


if [ ! -d "./diff-runs" ]; then
  mkdir "./diff-runs"
fi

for ((run=$run_from; run<=$run_to; run++))
do
  if [ ! -d "./diff-runs/${run}" ]; then
    mkdir "./diff-runs/${run}"
  fi

  for i in ${percents[@]} 
  do
# make dir
    echo "making directory"
    if [ ! -d "./diff-runs/${run}/FB_seed_${i}p" ]; then
      mkdir "./diff-runs/${run}/FB_seed_${i}p"
    fi
# merge seeds files of different types
    echo "merging seed files"
    cat ./single_runs/${run}/${type[0]}_single_devel_seed_for_train_${i}p \
      ./single_runs/${run}/${type[1]}_single_devel_seed_for_train_${i}p  \
      ./single_runs/${run}/${type[2]}_single_devel_seed_for_train_${i}p  \
      ./single_runs/${run}/${type[3]}_single_devel_seed_for_train_${i}p \
      > ./diff-runs/${run}/FB_seed_${i}p/seeds.cfacts

# copy the program to different directories
    echo "copy the ppr program and test files"
    cp ssl_LP.ppr ./diff-runs/${run}/FB_seed_${i}p/
    cp test.examples ./diff-runs/${run}/FB_seed_${i}p/
    cp testList.examples ./diff-runs/${run}/FB_seed_${i}p/ 
    cp testBoth.examples ./diff-runs/${run}/FB_seed_${i}p/
# run proppr
    echo "running proppr"

  #cd ./diff-runs/FB_seed_${i}p/
    proppr compile ./diff-runs/${run}/FB_seed_${i}p/ssl_LP.ppr 
    proppr set --programFiles ./diff-runs/${run}/FB_seed_${i}p/ssl_LP.wam:./diff-runs/${run}/FB_seed_${i}p/seeds.cfacts:/remote/curtis/baidu/mingyanl/pipe_line/cfacts_files/hasItem.cfacts:/remote/curtis/baidu/mingyanl/pipe_line/cfacts_files/inList.cfacts:/remote/curtis/baidu/mingyanl/pipe_line/cfacts_files/featureOf.cfacts:/remote/curtis/baidu/mingyanl/pipe_line/cfacts_files/hasFeature.cfacts
    proppr answer ./diff-runs/${run}/FB_seed_${i}p/testList.examples --apr eps=1e-8 --threads 5 # --duplicateCheck -1
  done

  if [ ! -d "./diff-runs/${run}/FB_seed_forTestList" ]; then
    mkdir "./diff-runs/${run}/FB_seed_forTestList"
  fi

  echo "merging seed files"
  cat ./single_runs/${run}/${type[0]}_single_devel_seed_for_test \
      ./single_runs/${run}/${type[1]}_single_devel_seed_for_test  \
      ./single_runs/${run}/${type[2]}_single_devel_seed_for_test  \
      ./single_runs/${run}/${type[3]}_single_devel_seed_for_test \
     > ./diff-runs/${run}/FB_seed_forTestList/seeds.cfacts

  echo "copy the ppr program and test files"
  cp ssl_LP.ppr ./diff-runs/${run}/FB_seed_forTestList/
  cp test.examples ./diff-runs/${run}/FB_seed_forTestList/
  cp testList.examples ./diff-runs/${run}/FB_seed_forTestList/
  cp testBoth.examples ./diff-runs/${run}/FB_seed_forTestList/

  echo "running proppr"
  proppr compile ./diff-runs/${run}/FB_seed_forTestList/ssl_LP.ppr
  proppr set --programFiles ./diff-runs/${run}/FB_seed_forTestList/ssl_LP.wam:./diff-runs/${run}/FB_seed_forTestList/seeds.cfacts:/remote/curtis/baidu/mingyanl/pipe_line/cfacts_files/hasItem.cfacts:/remote/curtis/baidu/mingyanl/pipe_line/cfacts_files/inList.cfacts:/remote/curtis/baidu/mingyanl/pipe_line/cfacts_files/featureOf.cfacts:/remote/curtis/baidu/mingyanl/pipe_line/cfacts_files/hasFeature.cfacts
#  proppr answer ./diff-runs/FB_seed_${i}p/test.examples --apr eps=1e-8 --threads 5
  proppr answer ./diff-runs/${run}/FB_seed_forTestList/testList.examples --apr eps=1e-8 --threads 5

done
