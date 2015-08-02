#!/bin/bash
cat ../seed_file/seed_matched/*.seed > ../seed_file/seed_matched/allSeed
cat ../seed_file/seed_matched/*.mapping > ../seed_file/seed_matched/mapping
javac CheckSeedInCfactsFile.java
echo "file in original sedd"
java CheckSeedInCfactsFile ../seed_file/seed_extracted/allSeedUnEdit ../../cfacts/inList.cfacts
echo "after match seed and graph"
java CheckSeedInCfactsFile ../seed_file/seed_matched.allSeed../../cfacts/inList.cfacts