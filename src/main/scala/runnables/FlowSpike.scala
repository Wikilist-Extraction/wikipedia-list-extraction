package runnables

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import dataFormats.{WikiLink, WikiListResult, WikiListPage}
import dump.{ListArticleParser, RecordReaderWrapper}
import it.cnr.isti.hpc.wikipedia.article.Article

import implicits.ConversionImplicits._
import tfidf.ListMemberTypeExtractor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

/**
 * Created by nico on 05/07/15.
 */
object FlowSpike {
  def main(args: Array[String]) {
    val filename = "/Users/nico/Studium/KnowMin/datasets/data/json/sample-list.json"

    implicit val actorSys = ActorSystem("wikilist-extraction")
    implicit val materializer = ActorMaterializer()

    val reader = new RecordReaderWrapper(filename)
    val articleList: List[Article] = reader.getArticlesList

    val articleSource = Source(articleList)
    val printSink = Sink.foreach[WikiListResult](result => println(result.types))

    val convertArticle = Flow[Article].mapConcat { article =>
      new ListArticleParser(article).parseArticle().toList
    }


    articleSource
      .via(convertArticle)
      .via(computeTfIdf())
      .to(printSink)
      .run()
  }

  def computeTfIdf(): Flow[WikiListPage, WikiListResult, Unit] = {
    val tfIdf = new ListMemberTypeExtractor
    Flow[WikiListPage].mapAsyncUnordered(4) { page =>
      val list = page.listMembers.map(_.uri)
      println(list)
      tfIdf.compute(list).map { resultMap =>
        println(resultMap)
        val types = resultMap.keys.map(WikiLink("", _)).toList.take(2)
        WikiListResult(page.listMembers, page.title, types)
      }
    }
  }
}
