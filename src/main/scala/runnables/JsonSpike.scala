package runnables

import streams.JsonWriter

/**
 * Created by nico on 13/07/15.
 */
object JsonSpike {
  def main(args: Array[String]) {
    val resmap = Map(
      "List_of_Donald_Ross-designed_courses" -> List(
    "http://dbpedia.org/ontology/PopulatedPlace",
    "http://dbpedia.org/ontology/Place",
    "http://dbpedia.org/ontology/Settlement",
    "http://dbpedia.org/ontology/City",
    "http://dbpedia.org/ontology/Town"
      ),
    "List_of_DuckTales_characters" -> List(
    "http://dbpedia.org/ontology/Person",
    "http://wikidata.dbpedia.org/resource/Q5",
    "http://wikidata.dbpedia.org/resource/Q215627",
    "http://dbpedia.org/ontology/Agent",
    "http://dbpedia.org/ontology/FictionalCharacter"
    ),
    "List_of_English_monarchs" -> List(
    "http://dbpedia.org/ontology/Royalty",
    "http://dbpedia.org/ontology/BritishRoyalty",
    "http://dbpedia.org/ontology/Person",
    "http://wikidata.dbpedia.org/resource/Q5",
    "http://wikidata.dbpedia.org/resource/Q215627"
    )
    )

    val j = JsonWriter.createResultJson(resmap)

    println(j.toString())
  }

}
