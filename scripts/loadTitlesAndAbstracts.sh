#!/usr/bin/env bash

urlTitles="http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/en/labels_en.nt.bz2"
urlAbstracts="http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/en/long_abstracts_en.nt.bz2"


# curl -# $urlTitles | cat | bzip2 -d | sed '1d' | head -n -1 > /tmp/titles.nt
# curl -# $urlAbstracts | cat | bzip2 -d | sed '1d' | head -n -1 > /tmp/abstracts.nt

# cat /tmp/titles.nt /tmp/abstracts.nt > /tmp/titlesAndAbstracts.nt

mkdir -p $(dirname $0)/../db/abstracts
rm -rf $(dirname $0)/../db/abstracts
curl --silent $urlAbstracts | bzip2 -d | sed -e '1d' -e '$d' -e 's/\\u\w{4}//' | tdbloader2 --loc $(dirname $0)/../db/abstracts

# mkdir -p $(dirname $0)/../db/titles
# rm -rf $(dirname $0)/../db/titles
# curl --silent $urlTitles | bzip2 -d | sed -e '1d' -e '$d' | iconv -c -f utf-8 -t ascii | tdbloader2 --loc $(dirname $0)/../db/titles

# { curl --silent $urlTitles | bzip2 -d | sed -e '1d' -e '$d'; curl --silent $urlAbstracts | bzip2 -d | sed -e '1d' -e '$d'; } | iconv -c -f utf-8 -t ascii | tdbloader2 --loc $(dirname $0)/../db/textEvidence
# { cat /tmp/abstracts.nt | sed '$d' ; cat /tmp/titles.nt | sed '$d'; } | tdbloader2 --loc $(dirname $0)/../db/textEvidence

# rm /tmp/titlesAndAbstracts.nt
