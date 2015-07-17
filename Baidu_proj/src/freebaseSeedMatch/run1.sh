if [ ! -d "../seed_file/seed_matched/" ]
then
  mkdir ../seed_file/seed_matched/
fi
split -d -l 1000 ../seed_file/seed_extracted/allSeedUnEdit ../seed_file/seed_matched/
Files=../seed_file/seed_matched/*
javac SeedCfactsEditDistance.java
for f in ${Files}
do
echo ${f}
java SeedCfactsEditDistance ${f} ../../cfacts/inList.cfacts ${f}.seed ${f}.mapping &
done