package runnables

import akka.actor.ActorSystem
import akka.stream.{Materializer, ActorMaterializer}
import akka.stream.scaladsl.{Flow, Sink, Source}
import dataFormats.{WikiListResult, WikiListPage}
import dump.{ListArticleParser, RecordReaderWrapper}
import it.cnr.isti.hpc.wikipedia.article.Article

import implicits.ConversionImplicits._
import textEvidence.TextEvidenceExtractor
import typesExtraction.{TfIdfWorker, ListMemberTypeExtractor}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

/**
 * Created by nico on 05/07/15.
 */
object FlowSpike {
  def main(args: Array[String]) {


    val filename = "/Users/nico/Studium/KnowMin/datasets/data/json/karateka-list.json"

    implicit val actorSys = ActorSystem("wikilist-extraction")
    implicit val materializer = ActorMaterializer()

    val reader = new RecordReaderWrapper(filename)
    val articleList: List[Article] = reader.getArticlesList
    println(articleList.size)
    val articleSource = Source(articleList)
    val printSink = Sink.foreach[WikiListResult](result => println(s"finished: ${result.page.title} count:${result.page.listMembers.size}"))
    var counter = 0
    val countSink = Sink.foreach[WikiListResult](x => {counter += 1; println(counter)})

    val convertArticle = Flow[Article].mapConcat { article =>
      new ListArticleParser(article).parseArticle().toList
    }

    articleSource
      .via(convertArticle)
      .via(getTypesMap())
//      .via(computeTfIdf())
//      .via(computeTextEvidence())
      .to(printSink)
      .run()

  }

  val parallelCount = 8

  def getTypesMap()(implicit materializer: Materializer): Flow[WikiListPage, WikiListResult, Unit] = {
    val extractor = new ListMemberTypeExtractor
    Flow[WikiListPage].mapAsyncUnordered(1) { page =>
      println(s"starting: ${page.title} count: ${page.listMembers.size}")

      extractor.getTypesMap(page.getEntityUris) map { typesMap =>
        WikiListResult(page, typesMap, Map[Symbol, Map[String, Double]]().empty)
      }
    }
  }

  def computeTfIdf(): Flow[WikiListResult, WikiListResult, Unit] = {
    val tfIdfWorker = new TfIdfWorker
    Flow[WikiListResult].mapAsyncUnordered(parallelCount) { result =>
      tfIdfWorker.getTfIdfScores(result.types).map { resultMap =>
        WikiListResult(result.page, result.types, Map(TfIdfWorker.testSymbol -> resultMap))
      }
    }
  }

  def computeTextEvidence(): Flow[WikiListResult, WikiListResult, Unit] = {
    val extractor = new TextEvidenceExtractor()
    Flow[WikiListResult].mapAsyncUnordered(parallelCount) { result =>
      val entities = result.page.getEntityUris
      val types = result.getTypes
      extractor.compute(entities, types).map { resultList =>
        val newScores = result.scores + (TextEvidenceExtractor.testSymbol -> resultList.toMap)
        WikiListResult(result.page, result.types, newScores)
      }
    }
  }
}
