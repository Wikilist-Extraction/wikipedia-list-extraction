#!/usr/bin/env bash

yagoTypesUrl="http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/links/yago_types.nt.bz2"

rm $(dirname $0)/../src/main/resources/typeCount.csv
{ cat $1 | sed -n '1!p' | cut -f3 -d ' '; curl --silent $yagoTypesUrl | bzip2 -d | sed -n '1!p' | cut -f3 -d ' ';} | awk '{count[$1]++}END{for(j in count) print count [j]", " j }' > $(dirname $0)/../src/main/resources/typeCount.csv
