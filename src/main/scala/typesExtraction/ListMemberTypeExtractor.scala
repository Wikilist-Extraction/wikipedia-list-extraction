package typesExtraction


import akka.stream.Materializer
import akka.stream.scaladsl.Source
import com.hp.hpl.jena.shared.Lock

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import sparql.{JenaDumpWrapper, JenaFragmentsWrapper}
import util.LoggingUtils._

import scala.util.{Failure, Success}

/**
 * Gets type for each list member, groups and counts them
 */
object ListMemberTypeExtractor {
  val typeQueryString =
    """
      SELECT ?type WHERE {
        ?uri rdf:type ?type
      }
    """
}

class ListMemberTypeExtractor extends JenaDumpWrapper {
  import ListMemberTypeExtractor._

  var count = 0

////  val fragmentServerUrl = "http://data.linkeddatafragments.org/dbpedia2014"
//  val baseDir = "/Users/nico/Studium/KnowMin/datasets/dbpedia/"
//  override val dbpediaTypesDumpFileName: String = baseDir + "instance_types_en.ttl"
////  override val yagoTypesDump: String = baseDir + "yago_types.nt.bz2"
//  println("size of model:" + dbpediaTypesModel.size())

  override val tdbDirectory: String = "/Users/nico/Studium/KnowMin/datasets/dbpedia/db"
  model.enterCriticalSection(Lock.READ)
  println(model.size())

  def getTypesOf(uri: String): Future[List[String]] = {
//    println(s"start get types of: $uri")
    count += 1
    println(count)
//    timeFuture(s"finished get types of: $uri") {
    logFutureResult[List[String]] {
      case Success(_) => "success"
      case Failure(_) => "failure"
    }{
      queryDumpWithUri(typeQueryString, uri)
        .map ( _.map ( _.getResource("type").toString))
    }
  }

  def getTypesMap(linksList: List[String])(implicit materializer: Materializer): Future[Map[String, Int]] = {
    val typeLists: Future[List[List[String]]] = Future.sequence(
      linksList
        .map (uri => getTypesOf(uri))
    )
//    val typeLists = Source(linksList)
//      .mapAsyncUnordered(5)(getTypesOf)
//      .runFold[List[List[String]]](List())((acc, elem) => elem :: acc)


    logFuture("finished listFuture"){
      typeLists
        .map (_.flatten.groupBy(identity))
        .map (_.mapValues(_.size))
    }
  }
}