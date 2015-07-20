1. Filter out all section title and get mapping and count files
   Input file: /remote/curtis/rgoutam/baidu_data/prescription/*
   Get output: mapping_repl, out/*
   Code: FilterRun.java, step1.sh
   Run with: step1.sh [para1] (para1: number 0-24 to run 25 files same time eg. ste1.sh 0)

2. Get unique mapping result, compress section count files, aggregate count.
   Input file: mapping_repl, out/*
   Get output: mapping_uniq, count_sparse, count_reduced
   Code: Step2_AggregateCount.java, step2.sh
   Run with: step2.sh

3. Compress section titles and get new mapping and count file
   Input files: ../step2/count_reduced ../step2/mapping_uniq
   Get output: compress_${1}/count_sorted_${1}, compress_${1}/mapping_${1}
   Code: Step3_CompressGenerator.java, step3.sh
   Run with: step3.sh [para1] (para1: the lower bound of count eg. step3.sh 28)

4. Build up xml based on new section title and arragement
   Input files:/remote/curtis/rgoutam/baidu_data/prescription/*, compress_${1}/mapping_${1}
   Get output: xml_files_${1}/${line}
   Code: Step4_BuildUpXml.java, step4.sh
   Run with: step4.sh [para1] (para1: the lower bound of count eg. step4.sh 28)