package typesExtraction


import akka.stream.Materializer
import akka.stream.scaladsl.Source
import com.hp.hpl.jena.shared.Lock

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import sparql.{JenaDumpWrapper, JenaFragmentsWrapper}
import util.LoggingUtils._

import scala.reflect.io.Directory
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

  override val tdbDirectory: String = "db/types"
  assert(!Directory(tdbDirectory).isEmpty)

  def getTypesOf(uri: String): Future[List[String]] = {
    queryDumpWithUri(typeQueryString, uri)
      .map(_.map(_.getResource("type").toString))
  }

  def getTypesMap(linksList: List[String])(implicit materializer: Materializer): Future[Map[String, Int]] = {


//    val typeLists: Future[List[List[String]]] = Future.sequence(
//      linksList
//        .map (uri => {
//          getTypesOf(uri)
//        })
//    )

    val typeLists = Source(linksList)
      .mapAsyncUnordered(10)(getTypesOf)
      .runFold[List[List[String]]](List())((acc, elem) => elem :: acc)

    typeLists
      .map (_.flatten.groupBy(identity))
      .map (_.mapValues(_.size))
  }
}