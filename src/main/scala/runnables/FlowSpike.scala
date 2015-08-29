package runnables

import akka.actor.ActorSystem
import akka.stream.{Supervision, ActorMaterializerSettings, ActorMaterializer}
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.scalalogging.slf4j.LazyLogging
import dataFormats.{WikiListScores, WikiListResult}
import dump.RecordReaderWrapper
import fragmentsWrapper.QueryWrapper
import it.cnr.isti.hpc.wikipedia.article.Article

import implicits.ConversionImplicits._
import streams.{RdfWriter, ExtractionFlows, JsonWriter}
import tableExtraction.{RDFTable, TableExtractor}
import util.LoggingUtils._

import scala.collection.immutable.TreeMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
import util.Config._

/**
 * Created by nico on 05/07/15.
 */
object FlowSpike extends LazyLogging {
  def main(args: Array[String]) {


    val filename = config.getString("io.inputFile")
    val decider: Supervision.Decider = {
      case _ => Supervision.Resume
    }

    implicit val actorSys = ActorSystem("wikilist-extraction")
    implicit val materializer = ActorMaterializer(
      ActorMaterializerSettings(actorSys)
        .withDebugLogging(true)
        .withSupervisionStrategy(decider)
    )

    val rdfWriter = new RdfWriter()
    val reader = new RecordReaderWrapper(filename)
    val articles: Iterator[Article] = reader.iterator

//    val printSink = Sink.foreach[WikiListResult](result => println(s"finished: ${result.page.title} count:${result.scores}"))

//    val mapSink = Sink.fold[Map[String, List[String]], WikiListResult](Map[String, List[String]]()) { (resultTypes, result) =>
//      resultTypes + (result.page.title -> result.types.keys.toList)
//    }

    // val typeSink = Sink.fold[List[WikiFusedResult], WikiFusedResult](List()) { (list, result) => result :: list }
     val printSink = Sink.foreach[WikiListResult](result => println(s"finished: ${result.page.title} count:${result.types}"))

    val countSink = Sink.fold[Int, WikiListResult](0)((acc, _) => { println("finished list: " + (acc + 1)); acc + 1 })
    val typeSink = Sink.fold[List[WikiListResult], WikiListResult](List()) { (list, result) => result :: list }
    //val rdfSink = Sink.foreach[WikiFusedResult](each => { rdfWriter.addTypeStatementsFor(each, "data/results/types_scientists.ttl") })

    val resFuture = Source(() => articles)
      .via(ExtractionFlows.completeFlow())
      .runWith(typeSink)

    resFuture onFailure {
      case e => {
        logger.error(e.toString)
        logger.error(e.getCause.toString)
//        e.printStackTrace()
      }
    }

    resFuture foreach { res =>
      res foreach (fusedResults => rdfWriter.addTypeStatementsFor(fusedResults, config.getString("io.rdfTypesOutput")))
      val json = JsonWriter.createResultJson(res)
      JsonWriter.write(json, config.getString("io.jsonOutput"))
      materializer.shutdown()
      actorSys.shutdown()
    }
  }
}
