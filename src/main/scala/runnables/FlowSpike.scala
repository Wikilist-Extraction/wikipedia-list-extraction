package runnables

import akka.actor.ActorSystem
import akka.stream.{Materializer, ActorMaterializer}
import akka.stream.scaladsl.{Flow, Sink, Source}
import dataFormats.{WikiFusedResult, WikiListResult, WikiListPage}
import dump.{ListArticleParser, RecordReaderWrapper}
import it.cnr.isti.hpc.wikipedia.article.Article
//import java.io.FileWriter

import implicits.ConversionImplicits._
//import ratings.TextEvidenceRating
import streams.{ExtractionFlows, JsonWriter}
//import typesExtraction.TfIdfWorker
//import util.LoggingUtils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

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
    println("Article count: %d", articleList.size)

//    val printSink = Sink.foreach[WikiListResult](result => println(s"finished: ${result.page.title} count:${result.scores}"))

//    val mapSink = Sink.fold[Map[String, List[String]], WikiListResult](Map[String, List[String]]()) { (resultTypes, result) =>
//      resultTypes + (result.page.title -> result.types.keys.toList)
//    }

//    val tfIdfSink = Sink.fold[List[WikiListResult], WikiListResult](List()) { (list, result) =>
//      result :: list
//    }

    val typeSink = Sink.fold[List[WikiFusedResult], WikiFusedResult](List()) { (list, result) => result :: list }
    val printSink = Sink.foreach[WikiFusedResult](result => println(s"finished: ${result.page.title} count:${result.types}"))

    val g = Source(articleList)
      .via(ExtractionFlows.completeFlow)
      .runWith(typeSink)


    g foreach { res =>
      val json = JsonWriter.createJson(res)
      JsonWriter.write(json, "results/result.json")
      materializer.shutdown()
      actorSys.shutdown()
    }

  }
}
