#!/bin/bash
javac ExtractRelationSeed.java
if [ ! -d "../seed_file" ]
then
        mkdir ../seed_file
fi
if [ ! -d "../seed_file/seed_extracted" ]
then
        mkdir ../seed_file/seed_extracted
fi
factPath=/remote/curtis/baidu/mingyanl/freebase/freebase-easy-14-04-14/facts.txt
list=("Used To Treat" "Condition This May Prevent" "Physiologic effect")
reverse_list=("Side effect of")
string=""
for item in "${list[@]}"
do
        string="${string}"",""${item}"
        grep "${item}" ${factPath} | java ExtractRelationSeed "${item}" false ../seed_file/seed_extracted/
done
for item in "${reverse_list[@]}"
do
        string="${string}"",""${item}"
        grep "${item}" ${factPath} | java ExtractRelationSeed "${item}" true ../seed_file/seed_extracted/
done
cat ../seed_file/seed_extracted/* > ../seed_file/seed_extracted/allSeedUnEdit