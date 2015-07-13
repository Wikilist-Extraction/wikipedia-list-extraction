#!/usr/bin/env bash

typesUrl="http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/en/instance_types_en.nt.bz2"

mkdir -p $(dirname $0)/../db/types
rm -rf $(dirname $0)/../db/types
curl --silent "$typesUrl" | bzip2 -d | tee >(./typeCount.sh) | sed -n '1!p' | tdbloader2 --loc $(dirname $0)/../db/types
