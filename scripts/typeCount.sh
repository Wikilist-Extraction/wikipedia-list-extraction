#!/usr/bin/env bash

rm $(dirname $0)/../src/main/resources/typeCount.csv
cat $1 | sed -n '1!p' | cut -f3 -d ' ' | awk '{count[$1]++}END{for(j in count) print count [j]", " j }' > $(dirname $0)/../src/main/resources/typeCount.csv
