package streams

import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import dataFormats.{WikiListResult, WikiListPage}
import dump.ListArticleParser
import it.cnr.isti.hpc.wikipedia.article.Article
import textEvidence.TextEvidenceExtractor
import typesExtraction.{TfIdfWorker, ListMemberTypeExtractor}
import util.LoggingUtils._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by nico on 14/07/15.
 */
object ExtractionFlows {
  val parallelCount = 8

  def completeFlow()(implicit materializer: Materializer) = Flow[Article]
    .via(convertArticle())
    .via(getTypesMap())
    .via(computeTfIdf())
    .via(computeTextEvidence())

  def tfIdfFlow()(implicit materializer: Materializer) = Flow[Article]
    .via(convertArticle())
    .via(getTypesMap())
    .via(computeTfIdf())

  def convertArticle() = Flow[Article].mapConcat { article =>
    new ListArticleParser(article).parseArticle().toList
  }

  def getTypesMap()(implicit materializer: Materializer): Flow[WikiListPage, WikiListResult, Unit] = {
    val extractor = new ListMemberTypeExtractor
    Flow[WikiListPage].mapAsyncUnordered(parallelCount) { page =>
      println(s"starting: ${page.title} count: ${page.listMembers.size}")

      timeFuture("duration for getting types:") {
        extractor.getTypesMap(page.getEntityUris) map { typesMap =>
          WikiListResult(page, typesMap, Map[Symbol, Map[String, Double]]().empty)
        }
      }
    }
  }

  def computeTfIdf(): Flow[WikiListResult, WikiListResult, Unit] = {
    val tfIdfWorker = new TfIdfWorker
    Flow[WikiListResult].mapAsyncUnordered(parallelCount) { result =>
      timeFuture("duration for computing tf-idf:") {
        tfIdfWorker.getTfIdfScores(result.types).map { resultMap =>
          WikiListResult(result.page, result.types, Map(TfIdfWorker.testSymbol -> resultMap))
        }
      }
    }
  }

  def computeTextEvidence()(implicit materializer: Materializer): Flow[WikiListResult, WikiListResult, Unit] = {
    val extractor = new TextEvidenceExtractor()
    Flow[WikiListResult].mapAsyncUnordered(parallelCount) { result =>
      val entities = result.page.getEntityUris
      val types = result.getTypes
      timeFuture("duration for computing text evidence:") {
        extractor.compute(entities, types).map { resultList =>
          val newScores = result.scores + (TextEvidenceExtractor.testSymbol -> resultList)
          WikiListResult(result.page, result.types, newScores)
        }
      }
    }
  }
}
