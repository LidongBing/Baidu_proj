#! /bin/bash
#split original seed file into develop and evalution part
seedPath=../seed_file/seed_seperated/
runPath=../seed_file/seed_seperated/
relations=condition_this_may_prevent,physiologic_effect,side_effect_of,used_to_treat

# seperate file into single and multi
if [ ! -d "${seedPath}" ]
then
        mkdir ${seedPath}
fi
javac SeperateSingleMultiRelation.java
java SeperateSingleMultiRelation ../seed_file/seed_matched/allSeed "${relations}" ${seedPath}

# put it into 10 runs
javac HoldoutDevelop.java
java HoldoutDevelop  "${seedPath}" "${runPath}" "${relations}"
# split 1:4 for devel part
javac PropprSeedForTrainTestList.java
java PropprSeedForTrainTestList "${runPath}" "${relations}"
#random percentage
javac DiffPropprSeedForTrainList.java
java DiffPropprSeedForTrainList "${runPath}" "${relations}"