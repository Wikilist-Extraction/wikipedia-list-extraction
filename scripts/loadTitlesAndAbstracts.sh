#!/usr/bin/env bash

urlTitles="http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/en/labels_en.nt.bz2"
urlAbstracts="http://download.lodlaundromat.org/b3268495b5c7a407852844c07ed72a49"

mkdir -p $(dirname $0)/../db/abstracts
rm -rf $(dirname $0)/../db/abstracts
curl --silent $urlAbstracts | gzip -d | tdbloader2 --loc $(dirname $0)/../db/abstracts

mkdir -p $(dirname $0)/../db/titles
rm -rf $(dirname $0)/../db/titles
curl --silent $urlTitles | bzip2 -d | tdbloader2 --loc $(dirname $0)/../db/titles
