export PROPPR=/remote/curtis/baidu/ProPPR/ProPPR-srw2.0
export PATH=$PATH:$PROPPR/scripts
run_path=../diff-runs/
seed_path=../seed/
cfacts_path=../cfacts/
percents=('2.5' '7.5' '12.5' '25.0' '50.0' '75.0' '100.0')
type=('condition_this_may_prevent' 'physiologic_effect' 'side_effect_of' 'used_to_treat')

run_from=$1
run_to=$2

if [ ! -d "${run_path}" ]; then
  mkdir "${run_path}"
fi

for ((run=$run_from; run<=$run_to; run++))
do
  if [ ! -d "${run_path}${run}" ]; then
    mkdir "${run_path}${run}"
  fi

  for i in ${percents[@]}
  do
# make dir
    echo "making directory"
    if [ ! -d "${run_path}${run}/FB_seed_${i}p" ]; then
      mkdir "${run_path}${run}/FB_seed_${i}p"
    fi
# merge seeds files of different types
    echo "merging seed files"
    cat ${seed_path}${run}/${type[0]}_single_devel_seed_for_train_${i}p \
      ${seed_path}${run}/${type[1]}_single_devel_seed_for_train_${i}p  \
      ${seed_path}${run}/${type[2]}_single_devel_seed_for_train_${i}p  \
      ${seed_path}${run}/${type[3]}_single_devel_seed_for_train_${i}p \
      > ${run_path}${run}/FB_seed_${i}p/seeds.cfacts

# check if facts file contains at lease one for every relation, else break;
    m=$(java CheckSeedInCfactsFile ${run_path}${run}/FB_seed_${i}p/seeds.cfacts ${cfacts_path}inList.cfacts)
    if [ "${m}" == 'wrong' ]
    then
	break
    fi
# copy the program to different directories
    echo "copy the ppr program and test files"
    cp ssl_LP.ppr ${run_path}${run}/FB_seed_${i}p/
    cp test.examples ${run_path}${run}/FB_seed_${i}p/
    cp testList.examples ${run_path}${run}/FB_seed_${i}p/
    cp testBoth.examples ${run_path}${run}/FB_seed_${i}p/
# run proppr
    echo "running proppr"

  #cd ./diff-runs/FB_seed_${i}p/
    proppr compile ${run_path}${run}/FB_seed_${i}p/ssl_LP.ppr
    proppr set --programFiles ${run_path}${run}/FB_seed_${i}p/ssl_LP.wam:${run_path}${run}/FB_seed_${i}p/seeds.cfacts:${cfacts_path}hasItem.cfacts:${cfacts_path}inList.cfacts:${cfacts_path}featureOf.cfacts:${cfacts_path}hasFeature.cfacts
    proppr answer ${run_path}${run}/FB_seed_${i}p/testList.examples --apr eps=1e-8 --threads 5 # --duplicateCheck -1
  done

  if [ ! -d "${run_path}${run}/FB_seed_forTestList" ]; then
    mkdir "${run_path}${run}/FB_seed_forTestList"
  fi

  echo "merging seed files"
  cat ${seed_path}${run}/${type[0]}_single_devel_seed_for_test \
      ${seed_path}${run}/${type[1]}_single_devel_seed_for_test  \
      ${seed_path}${run}/${type[2]}_single_devel_seed_for_test  \
      ${seed_path}${run}/${type[3]}_single_devel_seed_for_test \
     > ${run_path}${run}/FB_seed_forTestList/seeds.cfacts

  echo "copy the ppr program and test files"
  cp ssl_LP.ppr ${run_path}${run}/FB_seed_forTestList/
  cp test.examples ${run_path}${run}/FB_seed_forTestList/
  cp testList.examples ${run_path}${run}/FB_seed_forTestList/
  cp testBoth.examples ${run_path}${run}/FB_seed_forTestList/

  echo "running proppr"
  proppr compile ${run_path}${run}/FB_seed_forTestList/ssl_LP.ppr
  proppr set --programFiles ${run_path}${run}/FB_seed_forTestList/ssl_LP.wam:${run_path}${run}/FB_seed_forTestList/seeds.cfacts:${cfacts_path}hasItem.cfacts:${cfacts_path}inList.cfacts:${cfacts_path}featureOf.cfacts:${cfacts_path}hasFeature.cfacts
#  proppr answer ./diff-runs/FB_seed_${i}p/test.examples --apr eps=1e-8 --threads 5
  proppr answer ${run_path}${run}/FB_seed_forTestList/testList.examples --apr eps=1e-8 --threads 5

done