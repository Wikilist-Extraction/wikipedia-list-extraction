package tfidf

import scala.async.Async.{async, await}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import dataFormats.WikiLink
import sparql.JenaFragmentWrapper

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

class ListMemberTypeExtractor extends JenaFragmentWrapper {
  import ListMemberTypeExtractor._

  val fragmentServerUrl: String = "http://data.linkeddatafragments.org/dbpedia"

  //  val list = Observable.from(linksList)

  private val overallEntityCount: scala.collection.mutable.Map[String, Long] = scala.collection.mutable.Map.empty

  def getTypesOf(uri: String): Future[List[String]] = {
    queryWithUri(typeQueryString, uri)
      .map ( _.map ( _.getResource("type").toString))
  }

  def getCountOfEntityInDBpedia(entityType: String): Future[Long] = {
    if (overallEntityCount.contains(entityType)) {
      Future { overallEntityCount.get(entityType).get }
    } else {
      val count = queryWithUri(entityCountQueryString, entityType)
        .map ( _.map (_.getLiteral("count").getLong))
        .map ( _.head )
      count
    }
  }

  def computeTfIdf(count: Int, overallCount: Long, countOfTypesInList: Int): Double = {
    val tf = count / countOfTypesInList.toDouble
    val idf = math.log (totalEntityCount / overallCount)
    println(tf, idf, tf * idf)
    tf * idf
  }

  def getTypesMap(linksList: List[String]): Future[Map[String, Int]] = {
    val typeLists: Future[List[List[String]]] = Future.sequence(
      linksList
        //        .map (_.destination.resourceIri)
        .map (uri => getTypesOf(uri))
    )

    typeLists
      .map (_.flatten.groupBy(identity))
      .map ( _.mapValues(_.size))
  }

  def compute(list: List[String]): Future[Map[String, Double]] = {

    // get Types and count occurrence
    val typesMapFuture: Future[Map[String, Int]] = getTypesMap(list)//getTypesMap(linksList)


    // some magic
    val tupleFuture: Future[Map[String, Double]] = typesMapFuture
      .flatMap { typesMap =>
      val listOfFutures = typesMap.map {
        case (typeName: String, count: Int) =>
          async {
            val overallCount: Long = await(getCountOfEntityInDBpedia(typeName))
            val countOfTypesInList: Int = await(typesMapFuture).size

            typeName -> computeTfIdf(count, overallCount, countOfTypesInList)
          }
      }
      val futureOfList = Future.sequence(listOfFutures)
      futureOfList.map( _.toMap)
    }
    tupleFuture
  }
}