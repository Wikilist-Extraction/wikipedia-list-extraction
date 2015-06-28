#!/usr/bin/env bash


# TAKEN FROM: https://github.com/diegoceccarelli/json-wikipedia/blob/master/scripts/convert-xml-dump-to-json.sh

EXPECTED_ARGS=3
E_BADARGS=65

VERSION="1.0.0"
XMX="-Xmx8000m"
LOG=INFO
##LOG=DEBUG
LOGAT=1000
JAVA="java $XMX -Dlogat=$LOGAT -Dlog=$LOG -cp $(dirname $0)/../lib/json-wikipedia-$VERSION-jar-with-dependencies.jar "

export LC_ALL=C

if [ $# -ne $EXPECTED_ARGS ];
then
  echo "Usage: `basename $0` lang[en,it] xml-dump  json-dump "
  exit $E_BADARGS
fi

LANG=$1
WIKI_XML_DUMP=$2
WIKI_JSON_DUMP=$3


echo "converting mediawiki xml dump to json dump ($WIKI_JSON_DUMP)"
$JAVA  it.cnr.isti.hpc.wikipedia.cli.MediawikiToJsonCLI -input $WIKI_XML_DUMP -output $WIKI_JSON_DUMP -lang $LANG
