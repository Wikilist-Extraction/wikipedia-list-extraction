#!/usr/bin/env bash

EXPECTED_ARGS=2
E_BADARGS=65

if [ $# -ne $EXPECTED_ARGS ];
then
  echo "Usage: `basename $0` txt-list xml-dump"
  exit $E_BADARGS
fi

titleList=$1
outputXml=$2

curl \
  -d "&action=submit&pages=$(cat $titleList| hexdump -v -e '/1 "%02x"' | sed 's/\(..\)/%\1/g' )" \
  http://en.wikipedia.org/w/index.php\?title\=Special:Export \
  -o "$outputXml" \
  --compressed -H 'Accept-Encoding: gzip,deflate'
