package runnables

import com.hp.hpl.jena.query.QuerySolution
import sparql.JenaFragmentsWrapper

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
import scala.concurrent.duration._

/**
 * Created by nico on 01/07/15.
 */
object FragmentsSpike extends JenaFragmentsWrapper {
  def main(args: Array[String]) {
        val qs =
          """
            SELECT ?type WHERE {
              ?uri rdf:type ?type
            }
          """
        val pss = createParameterizedQuery(qs)
        pss.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        pss.setIri("uri", "http://dbpedia.org/resource/Jason_Caffey")

        val qexec = createFragmentQueryExecution(pss)

        val resultsFuture: Future[List[QuerySolution]] = execQuery(qexec)

        val s: Future[List[String]] = resultsFuture map { results: List[QuerySolution] =>
          results map { result =>
            result.getResource("type").toString
          }
        }

        val res = Await.result(s, 20 seconds)
        println(res)
  }

  val fragmentServerUrl: String = "http://data.linkeddatafragments.org/dbpedia2014"
  val endpointUrl = ""
}
