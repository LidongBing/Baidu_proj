#! /bin/bash
root=/remote/curtis/rgoutam/baidu_data/prescription/
file=/remote/curtis/baidu/mingyanl/0629/code/25_batches/batch${1}.txt
while IFS=$'\n' read -r line
do
        java -jar FilterRun.jar ${root}${line} mapping >> out/output${1}
done < ${file}