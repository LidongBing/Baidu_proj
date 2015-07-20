#! /bin/bash
root=/remote/curtis/rgoutam/baidu_data/prescription/
file=../25_batches/batch${1}.txt
javac Step1_FilterSectionTitle.java
if [ ! -d "out" ]
then
	mkdir out
fi
while IFS=$'\n' read -r line
do
        java Step1_FilterSectionTitle ${root}${line} mapping_repl >> out/output${1}
done < ${file}