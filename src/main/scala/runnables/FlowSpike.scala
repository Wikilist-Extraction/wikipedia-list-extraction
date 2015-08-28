package runnables

import akka.actor.ActorSystem
import akka.stream.{Supervision, ActorMaterializerSettings, ActorMaterializer}
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.scalalogging.slf4j.LazyLogging
import dataFormats.{WikiListResult, WikiFusedResult}
import dump.RecordReaderWrapper
import it.cnr.isti.hpc.wikipedia.article.Article

import implicits.ConversionImplicits._
import streams.{RdfWriter, ExtractionFlows, JsonWriter}
import util.LoggingUtils._

import scala.collection.immutable.TreeMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps


/**
 * Created by nico on 05/07/15.
 */
object FlowSpike extends LazyLogging {
  def main(args: Array[String]) {

    val filename = args(0)
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
     val printSink = Sink.foreach[WikiFusedResult](result => println(s"finished: ${result.page.title} count:${result.types}"))

    val countSink = Sink.fold[Int, WikiFusedResult](0)((acc, _) => { println("finished list: " + (acc + 1)); acc + 1 })
    val typeSink = Sink.fold[List[WikiFusedResult], WikiFusedResult](List()) { (list, result) => result :: list }

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
//      res foreach { fusedResults => rdfWriter.addTypeStatementsFor(fusedResults, "results/random10000.ttl") }
      val json = JsonWriter.createResultJson(res)
      JsonWriter.write(json, "results/random10000.json")
      materializer.shutdown()
      actorSys.shutdown()
    }

  }
}
