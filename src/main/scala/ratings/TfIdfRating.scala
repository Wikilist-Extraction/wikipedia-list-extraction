package ratings

import java.io.InputStream

import akka.stream.Materializer
import dataFormats.WikiListScores
import sparql.JenaSparqlWrapper

import scala.async.Async._
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
 * Created by nico on 08/07/15.
 */
object TfIdfRating extends Rating {
  // this is a manual queried value
  val totalEntityCount = 883644351
  val name = 'tfIdf

  val entityCountQueryString =
    """
      SELECT (count(?s) AS ?count) WHERE {
        ?s rdf:type ?uri
      }
    """
}

class TfIdfRating extends JenaSparqlWrapper with RatingResult {
  import ratings.TfIdfRating._

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

  def getRating(result: WikiListScores)(implicit materializer: Materializer): Future[Map[String, Double]] = {
    getTfIdfScores(result.types)
  }
}
