#!/bin/bash
factPath=/remote/curtis/baidu/mingyanl/freebase/freebase-easy-14-04-14/facts.txt

grep "${1}" ${factPath} | java ExtractRelationSeed "${1}"