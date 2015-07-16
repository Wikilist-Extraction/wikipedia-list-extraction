package runnables

import akka.actor.ActorSystem
import akka.stream.{Materializer, ActorMaterializer}
import akka.stream.scaladsl.{Flow, Sink, Source}
import dataFormats.{WikiListResult, WikiListPage}
import dump.{ListArticleParser, RecordReaderWrapper}
import it.cnr.isti.hpc.wikipedia.article.Article
import java.io.FileWriter

import implicits.ConversionImplicits._
import streams.{ExtractionFlows, JsonWriter}
import textEvidence.TextEvidenceExtractor
import typesExtraction.{TfIdfWorker, ListMemberTypeExtractor}
import util.LoggingUtils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
import scala.util.Success

/**
 * Created by nico on 05/07/15.
 */
object FlowSpike {
  def main(args: Array[String]) {


    val filename = "data/random1000.json"
//    val filename = "/Users/nico/Studium/KnowMin/datasets/data/json/karateka-list.json"

    implicit val actorSys = ActorSystem("wikilist-extraction")
    implicit val materializer = ActorMaterializer()

    val reader = new RecordReaderWrapper(filename)
    val articleList: List[Article] = reader.getArticlesList

    val printSink = Sink.foreach[WikiListResult](result => println(s"finished: ${result.page.title} count:${result.scores}"))

    val mapSink = Sink.fold[Map[String, List[String]], WikiListResult](Map[String, List[String]]()) { (resultTypes, result) =>
      resultTypes + (result.page.title -> result.types.keys.toList)
    }

    val tfIdfSink = Sink.fold[List[WikiListResult], WikiListResult](List()) { (list, result) =>
      result :: list
    }


    val g = Source(articleList)
      .via(ExtractionFlows.completeFlow())
      .runWith(mapSink)

    timeFuture("completeDuration")(g)

    g onComplete  { case Success(res) =>
      val json = JsonWriter.createResultJson(res)
      JsonWriter.write(json, "results/result.json")
      materializer.shutdown()
      actorSys.shutdown()
    }
  }
}
