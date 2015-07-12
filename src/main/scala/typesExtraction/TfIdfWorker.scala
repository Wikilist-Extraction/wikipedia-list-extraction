package typesExtraction

import java.io.InputStream

import sparql.JenaSparqlWrapper

import scala.async.Async._
import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * Created by nico on 08/07/15.
 */
object TfIdfWorker {
  val testSymbol = 'tfIdf

  val totalEntityCount = 883644351

  val entityCountQueryString =
    """
      SELECT (count(?s) AS ?count) WHERE {
        ?s rdf:type ?uri
      }
    """
}

class TfIdfWorker extends JenaSparqlWrapper {
  import TfIdfWorker._

  val endpointUrl = "http://dbpedia.org/sparql"

  val typeCountFileName = "/typeCount.csv"

  val cachedEntityCounts: mutable.AnyRefMap[String, Long] = loadTypeCount()

  private def loadTypeCount() = {
    val stream : InputStream = getClass.getResourceAsStream(typeCountFileName)
    val lines = scala.io.Source.fromInputStream(stream).getLines()

    val associations = lines map { line: String =>
      val members = line.split(", ")
      val count = members.head.toLong
      val typeName = members.last
      typeName -> count
    }

    val typesMap = mutable.AnyRefMap[String, Long]()
    associations.foreach(typesMap += _)
    typesMap
  }

  private def getCountOfEntityInDBpedia(entityType: String): Future[Long] = {
    if (cachedEntityCounts.contains(entityType)) {
      Future { cachedEntityCounts.get(entityType).get }
    } else {
      val count = querySparqlWithUri(entityCountQueryString, entityType)
        .map ( _.map (_.getLiteral("count").getLong))
        .map ( _.head )
      count
    }
  }

  private def calculateTfIdf(count: Int, overallCount: Long, countOfTypesInList: Int): Double = {
    val tf = count / countOfTypesInList.toDouble
    val idf = math.log (totalEntityCount / overallCount)
    tf * idf
  }

  def getTfIdfScores(typeCounts: Map[String, Int]): Future[Map[String, Double]] = {

    val listOfFutures = typeCounts.map {
      case (typeName: String, count: Int) =>
        async {
          val overallCount: Long = await(getCountOfEntityInDBpedia(typeName))
          val countOfTypesInList: Int = typeCounts.size
          typeName -> calculateTfIdf(count, overallCount, countOfTypesInList)
        }
    }
    val futureOfList = Future.sequence(listOfFutures)
    futureOfList.map( _.toMap)
  }
}
