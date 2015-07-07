package tfidf

import java.io.InputStream

import scala.async.Async.{async, await}
import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import sparql.{JenaSparqlWrapper, JenaFragmentsWrapper}
import util.LoggingUtils.time
/**
 * Created by nico on 01/07/15.
 */

object ListMemberTypeExtractor {
  lazy val totalEntityCount = 883644351


  val typeQueryString =
    """
      SELECT ?type WHERE {
        ?uri rdf:type ?type
      }
    """

  val entityCountQueryString =
    """
      SELECT (count(?s) AS ?count) WHERE {
        ?s rdf:type ?uri
      }
    """
}

class ListMemberTypeExtractor extends JenaFragmentsWrapper with JenaSparqlWrapper {
  import ListMemberTypeExtractor._

  val fragmentServerUrl = "http://data.linkeddatafragments.org/dbpedia2014"
  val endpointUrl = "http://dbpedia.org/sparql"

  val typeCountFileName = "/typeCount.csv"

  val cachedEntityCounts: mutable.AnyRefMap[String, Long] = loadTypeCount()

  def loadTypeCount() = {
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

  def getTypesOf(uri: String): Future[List[String]] = {
    queryFragmentWithUri(typeQueryString, uri)
      .map ( _.map ( _.getResource("type").toString))
  }

  def getCountOfEntityInDBpedia(entityType: String): Future[Long] = {
    if (cachedEntityCounts.contains(entityType)) {
      Future { cachedEntityCounts.get(entityType).get }
    } else {
      val count = querySparqlWithUri(entityCountQueryString, entityType)
        .map ( _.map (_.getLiteral("count").getLong))
        .map ( _.head )
      count
    }
  }

  def computeTfIdf(count: Int, overallCount: Long, countOfTypesInList: Int): Double = {
    val tf = count / countOfTypesInList.toDouble
    val idf = math.log (totalEntityCount / overallCount)
    tf * idf
  }

  def getTypesMap(linksList: List[String]): Future[Map[String, Int]] = {
    val typeLists: Future[List[List[String]]] = Future.sequence(
      linksList
        .map (uri => getTypesOf(uri))
    )

    typeLists
      .map (_.flatten.groupBy(identity))
      .map ( _.mapValues(_.size))
  }

  def compute(list: List[String]): Future[Map[String, Double]] = {

    // get Types and count occurrence
    val typeCountMap: Future[Map[String, Int]] = getTypesMap(list)//getTypesMap(linksList)


    // some magic
    val tupleFuture: Future[Map[String, Double]] = typeCountMap
      .flatMap { typesMap =>
      println(typesMap)
      val listOfFutures = typesMap.map {
        case (typeName: String, count: Int) =>
          async {
            val overallCount: Long = await(getCountOfEntityInDBpedia(typeName))
            val countOfTypesInList: Int = typesMap.size
            typeName -> computeTfIdf(count, overallCount, countOfTypesInList)
          }
      }
      val futureOfList = Future.sequence(listOfFutures)
      futureOfList.map( _.toMap)
    }
    tupleFuture
  }
}