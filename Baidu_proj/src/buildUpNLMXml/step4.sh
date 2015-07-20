#! /bin/bash
root=/remote/curtis/rgoutam/baidu_data/prescription/
file=../25_batches/batch${2}.txt
if [ ! -d "xml_files" ]
then
	mkdir xml_files
fi
javac Step4_BuildUpXml.java
while IFS=$'\n' read -r line
do
        java Step4_BuildUpXml ${root}${line} xml_files_${1}/${line} ../step3/compress_${1}/mapping_${1}
done < ${file}