#! /bin/bash
#split original seed file into develop and evalution part
javac HoldoutDevelop.java
java HoldoutDevelop
# split 1:4 for devel part
javac PropprSeedForTrainTestList.java
java PropprSeedForTrainTestList
#random percentage
javac DiffPropprSeedForTrainList.java
java DiffPropprSeedForTrainList