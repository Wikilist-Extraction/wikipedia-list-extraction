#!/usr/bin/env bash


# TAKEN FROM: https://github.com/diegoceccarelli/json-wikipedia/blob/master/scripts/convert-xml-dump-to-json.sh

EXPECTED_ARGS=2
E_BADARGS=65

JAR_PATH=$(dirname $0)/../target/scala-2.11/wikipedia-list-extraction_2.11-1.0-one-jar.jar
VERSION="1.0.0"
XMX="-Xmx8000m"
LOG=INFO
#LOG=DEBUG
LOGAT=1000
JAVA="java $XMX -Dlogat=$LOGAT -Dlog=$LOG -jar $JAR_PATH"

export LC_ALL=C
if [ ! -e $JAR_PATH ]; then
  echo "Jar does not exist. $JAR_PATH"
  echo "Run"
  echo "sbt one-jar"
  echo "and select runnables.Converter as main class"
  exit 1
fi

if [ $# -ne $EXPECTED_ARGS ];
then
  echo "Usage: `basename $0` xml-dump  json-dump "
  exit $E_BADARGS
fi

WIKI_XML_DUMP=$1
WIKI_JSON_DUMP=$2


echo "converting mediawiki xml dump to json dump ($WIKI_JSON_DUMP)"
$JAVA $WIKI_XML_DUMP $WIKI_JSON_DUMP #runnables.Converter
