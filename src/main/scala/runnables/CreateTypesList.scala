package runnables

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Source, Sink}
import akka.stream.{ActorMaterializerSettings, ActorMaterializer, Supervision}
import com.typesafe.scalalogging.slf4j.LazyLogging
import dataFormats.{WikiListScores, WikiListResult}
import dump.RecordReaderWrapper
import it.cnr.isti.hpc.wikipedia.article.Article

import implicits.ConversionImplicits._
import streams.{RdfWriter, ExtractionFlows, JsonWriter}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

/**
 * Created by nico on 27/08/15.
 */
object CreateTypesList extends LazyLogging {
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

    val reader = new RecordReaderWrapper(filename)
    val articles: Iterator[Article] = reader.iterator

    val tfIdfSink = Sink.fold[List[WikiListScores], WikiListScores](List()) { (list, result) =>
      result :: list
    }

    val resultFuture = Source(() => articles)
      .via(ExtractionFlows.tfIdfFlow())
      .runWith(tfIdfSink)

    resultFuture foreach { res =>
      val json = JsonWriter.createTfIdfJson(res)
      JsonWriter.write(json, "results/random-1000-tfidf.json")
      materializer.shutdown()
      actorSys.shutdown()
    }

    resultFuture onFailure {
      case e => {
        logger.error(e.toString)
        logger.error(e.getCause.toString)
        //        e.printStackTrace()
      }
    }
  }
}
