package streams


import com.typesafe.scalalogging.slf4j.LazyLogging
import dataFormats._
import dump.{TableArticleParser, ListArticleParser}
import akka.stream.{FlowShape, Materializer}
import akka.stream.scaladsl.{Sink, FlowGraph, Flow, Broadcast}
import dataFormats.{WikiFusedResult, WikiListResult, WikiListPage}
import dump.ListArticleParser
import extractors.ListMemberTypeExtractor
import it.cnr.isti.hpc.wikipedia.article.Article
import ratings.{RDFTableWrapper, TfIdfRating, TextEvidenceRating}
import scorer.Scorer
import tableExtraction.TableExtractor
import util.LoggingUtils._
import implicits.ConversionImplicits._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

/**
 * Created by nico on 14/07/15.
 */
object ExtractionFlows extends LazyLogging {
  val rdfWriter = new RdfWriter()
  val parallelCount = 8

  def completeFlow()(implicit materializer: Materializer) = Flow[Article]
    .via(convertArticle())
    .via(storeMembershipStatementsInFile("results/membership.ttl"))
    .via(getTypesMap())
    .via(filterEmptyTypes())
    .via(computeTfIdf())
    .via(computeTextEvidence())
    .via(fuseResults())

  def tfIdfFlow()(implicit materializer: Materializer) = Flow[Article]
    .via(convertArticle())
    .via(getTypesMap())
    .via(filterEmptyTypes())
    .via(computeTfIdf())


  def convertArticle()(implicit materializer: Materializer): Flow[Article, WikiListPage, Unit] = {
    val extractor = new TableExtractor

    def buildTableEntities(tablePage: WikiTablePage): List[WikiLink] = {
      val tableMatcher = new RDFTableWrapper(tablePage)
      val rdfTables = tableMatcher.convertTables()
      extractor.extractTableEntities(rdfTables)
      //    List()
    }

    Flow[Article].mapConcat { article =>
      time("time for converting article:") {
        try {
          logger.info(s"starting list for ${article.getTitleInWikistyle}")
          val parsedListPage = new ListArticleParser(article).parseArticle()

          logger.info(s"starting table for ${article.getTitleInWikistyle}")
          val parsedTablePage = new TableArticleParser(article).parseArticle()

          val finalPage = (parsedListPage, parsedTablePage) match {
            case (Some(listPage), Some(tablePage)) =>
              Some(WikiListPage(
                listPage.listMembers ++ buildTableEntities(tablePage),
                listPage.title,
                listPage.wikiAbstract,
                listPage.categories
              ))
            case (Some(listPage), _) => Some(listPage)
            case (_, Some(tablePage)) =>
              Some(WikiListPage(
                buildTableEntities(tablePage),
                tablePage.title,
                tablePage.wikiAbstract,
                tablePage.categories
              ))
            case _ => None
          }
          finalPage.toList
        } catch {
          case e: Exception =>
            logger.error("parseTables exception: " + e)
            List()
        }
      }
    }
  }

  def storeMembershipStatementsInFile(fileName: String) = {
    val statementsSink = Sink.foreach[WikiListPage](page => rdfWriter.addMembershipStatementsFor(page, fileName))

    FlowGraph.partial[FlowShape[WikiListPage, WikiListPage]]() { implicit b =>
      import FlowGraph.Implicits._

      val broadcast = b.add(Broadcast[WikiListPage](2))
      broadcast.out(0) ~> statementsSink

      FlowShape(broadcast.in, broadcast.out(1))
    }
  }

  def getTypesMap()(implicit materializer: Materializer): Flow[WikiListPage, WikiListResult, Unit] = {
    val extractor = new ListMemberTypeExtractor
    Flow[WikiListPage].mapAsyncUnordered(parallelCount) { page =>
      logger.info(s"starting: ${page.title} count: ${page.listMembers.size}")

      timeFuture("duration for getting types:") {
        extractor.getTypesMap(page.getEntityUris) map { typesMap =>
          if (typesMap.isEmpty) { logger.info(s"${page.title} is empty!") }
          WikiListResult(page, typesMap, Map[Symbol, Map[String, Double]]().empty)
        }
      }
    }
  }

  def filterEmptyTypes(): Flow[WikiListResult, WikiListResult, Unit] = {
    Flow[WikiListResult].filter(_.types.nonEmpty)
  }

  def computeTfIdf()(implicit materializer: Materializer): Flow[WikiListResult, WikiListResult, Unit] = {
    val rating = new TfIdfRating
    Flow[WikiListResult].mapAsyncUnordered(parallelCount) { result =>
      timeFuture("duration for computing tf-idf:") {
        rating.getRating(result).map { resultMap =>
          WikiListResult(result.page, result.types, Map(TfIdfRating.name -> resultMap))
        }
      }
    }
  }

  def computeTextEvidence()(implicit materializer: Materializer): Flow[WikiListResult, WikiListResult, Unit] = {
    val rating = new TextEvidenceRating
    Flow[WikiListResult].mapAsyncUnordered(parallelCount) { result =>
      val entities = result.page.getEntityUris
      val types = result.getTypes
      timeFuture("duration for computing text evidence:") {
        rating.getRating(result).map { resultList =>
          val newScores = result.scores + (TextEvidenceRating.name -> resultList)
          WikiListResult(result.page, result.types, newScores)
        }
      }
    }
  }

  def fuseResults(): Flow[WikiListResult, WikiFusedResult, Unit] = {
    Flow[WikiListResult].map { result =>
      time("duration for computing fused results:") {
        WikiFusedResult(result.page, Scorer.fuseResult(result))
      }
    }
  }
}
