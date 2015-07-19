#!/usr/bin/env bash

typesUrl="http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/en/mappingbased_properties_cleaned_en.nt.bz2"

mkdir -p $(dirname $0)/../db/properties
rm -rf $(dirname $0)/../db/properties
curl --silent "$typesUrl" | bzip2 -d | tdbloader2 --loc $(dirname $0)/../db/properties
