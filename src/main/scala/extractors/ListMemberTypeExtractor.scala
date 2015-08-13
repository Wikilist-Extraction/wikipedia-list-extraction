package extractors

import akka.stream.Materializer
import akka.stream.scaladsl.Source
import sparql.JenaDumpWrapper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.reflect.io.Directory

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
      .recover {
        case e: IllegalArgumentException => List[String]()
      }
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
