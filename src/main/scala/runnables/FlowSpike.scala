package runnables

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import dataFormats.WikiFusedResult
import dump.RecordReaderWrapper
import it.cnr.isti.hpc.wikipedia.article.Article

import implicits.ConversionImplicits._
import streams.{ExtractionFlows, JsonWriter}
import util.LoggingUtils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps


/**
 * Created by nico on 05/07/15.
 */
object FlowSpike {
  def main(args: Array[String]) {


    val filename = "data/random2000.json"
//    val filename = "/Users/nico/Studium/KnowMin/datasets/data/json/karateka-list.json"

    implicit val actorSys = ActorSystem("wikilist-extraction")
    implicit val materializer = ActorMaterializer()

    val reader = new RecordReaderWrapper(filename)
    val articleList: List[Article] = reader.getArticlesList
    println("Article count: ", articleList.size)

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
      .via(ExtractionFlows.completeFlow())
      .runWith(typeSink)


    timeFuture("completeDuration")(g)

    g foreach { res =>
      val json = JsonWriter.createResultJson(res)
      JsonWriter.write(json, "results/result2000_2.json")
      materializer.shutdown()
      actorSys.shutdown()
    }

  }
}
