wikipedia-list-extraction
===================

Extract lists and tables from wikipedia and add their information to DBpedia.

### Installation

1. Clone repo
2. Install jena CLI
  * on OS X you can run `brew install jena`
  * on other platforms you need to install them as described here https://jena.apache.org/documentation/tdb/commands.html#installation
3. Then run `scripts/loadDumps.sh`.
4. To start the application run `sbt run` and choose a runnable class.
