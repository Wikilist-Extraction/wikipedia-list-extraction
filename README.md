wikipedia-list-extraction
===================

Extract lists and tables from wikipedia and add their information to DBpedia.

### Installation

Make sure you have [Java 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html), [scala](http://www.scala-lang.org/download/install.html) and [sbt](http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html) installed.

1. Clone repo
2. Install jena CLI
  * on OS X you can run `brew install jena`
  * on other platforms you need to install them as described [here](https://jena.apache.org/documentation/tdb/commands.html#installation)
3. Then run `scripts/loadDumps.sh`, optionally you can update the preloaded typeCounts with `scripts/typeCount.sh`.
4. Download or create a wiki-markup xml dump. Downloads from [special:export](https://en.wikipedia.org/wiki/Special:Export) work just fine.
5. Convert it to a json dump with `scripts/convert.sh`.
6. Copy `src/main/resources/application.conf-default` to `src/main/resources/application.conf`, there you need to change the input filename accordingly to your generated dump file and you can change the parameters of the algorithm.
7. To start the application run `sbt run` and choose `GenerateTypes` as main class.
