package runnables

import tfidf.ListMemberTypeExtractor
import scala.concurrent.duration._
import scala.concurrent.Await

/**
 * Created by nico on 01/07/15.
 */
object JenaSpike {

  def main (args: Array[String]) {
    //    val jena = new JenaWrapper("http://dbpedia.org/sparql")

    //    val endpoint = "http://dbpedia.org/sparql"

    //    val qs =
    //      """
    //        SELECT ?type WHERE {
    //          ?uri rdf:type ?type
    //        }
    //      """
    //    val pss = createParameterizedQuery(qs)
    //    pss.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    //    pss.setIri("uri", "http://dbpedia.org/resource/Jason_Caffey")
    //
    //    val qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", pss.asQuery())
    //
    //    val resultsFuture: Future[List[QuerySolution]] = execQuery(qexec)
    //
    //    val s: Future[List[String]] = resultsFuture map[List[String]] { results: List[QuerySolution] =>
    //      results map { result =>
    //        result.getResource("type").toString
    //      }
    //    }


    val lm = new ListMemberTypeExtractor()
    val list = List(
      "http://dbpedia.org/resource/?arko_?abarkapa",
      "http://dbpedia.org/resource/Barney_Cable",
      "http://dbpedia.org/resource/Soup_Cable",
      "http://dbpedia.org/resource/Bruno_Caboclo",
      "http://dbpedia.org/resource/Jason_Caffey",
      "http://dbpedia.org/resource/Michael_Cage",
      "http://dbpedia.org/resource/Gerald_A._Calabrese",
      "http://dbpedia.org/resource/Nick_Calathes",
      "http://dbpedia.org/resource/Jos?_Calder?n_(basketball)",
      "http://dbpedia.org/resource/Adrian_Caldwell",
      "http://dbpedia.org/resource/Jim_Caldwell_(basketball)",
      "http://dbpedia.org/resource/Joe_Caldwell",
      "http://dbpedia.org/resource/Kentavious_Caldwell-Pope",
      "http://dbpedia.org/resource/Bill_Calhoun_(basketball)",
      "http://dbpedia.org/resource/Corky_Calhoun",
      "http://dbpedia.org/resource/Bob_Calihan",
      "http://dbpedia.org/resource/Demetrius_Calip",
      "http://dbpedia.org/resource/Tom_Callahan",
      "http://dbpedia.org/resource/Rick_Calloway",
      "http://dbpedia.org/resource/Ernie_Calverley",
      "http://dbpedia.org/resource/Mack_Calvin",
      "http://dbpedia.org/resource/Dexter_Cambridge",
      "http://dbpedia.org/resource/Dexter_Cambridge",
      "http://dbpedia.org/resource/Marcus_Camby",
      "http://dbpedia.org/resource/Joe_Camic",
      "http://dbpedia.org/resource/Elden_Campbell",
      "http://dbpedia.org/resource/Fred_Campbell_(basketball)",
      "http://dbpedia.org/resource/Ken_Campbell_(basketball)",
      "http://dbpedia.org/resource/Tony_Campbell",
      "http://dbpedia.org/resource/Ed_Campion",
      "http://dbpedia.org/resource/Isaiah_Canaan",
      "http://dbpedia.org/resource/Larry_Cannon_(basketball)",
      "http://dbpedia.org/resource/Clint_Capela",
      "http://dbpedia.org/resource/Derrick_Caracter",
      "http://dbpedia.org/resource/Frank_Card",
      "http://dbpedia.org/resource/Brian_Cardinal",
      "http://dbpedia.org/resource/Keith_Carey",
      "http://dbpedia.org/resource/Howie_Carl",
      "http://dbpedia.org/resource/Chet_Carlisle",
      "http://dbpedia.org/resource/Geno_Carlisle",
      "http://dbpedia.org/resource/Rick_Carlisle",
      "http://dbpedia.org/resource/Don_Carlos_(basketball)",
      "http://dbpedia.org/resource/Al_Carlson",
      "http://dbpedia.org/resource/Don_Carlson",
      "http://dbpedia.org/resource/Dan_Carnevale",
      "http://dbpedia.org/resource/Bob_Carney",
      "http://dbpedia.org/resource/Rodney_Carney",
      "http://dbpedia.org/resource/Tony_Carp",
      "http://dbpedia.org/resource/Bob_Carpenter_(basketball)",
      "http://dbpedia.org/resource/Antoine_Carr",
      "http://dbpedia.org/resource/Austin_Carr"
    )
    val s = lm.compute(list)

    val result = Await.result(s, 60 seconds)
    println(result)
  }
}
