package streams

import akka.actor.{ActorSystem, ActorRef}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import dataFormats.{WikiListResult, WikiListPage, WikiFusedResult}
import dump.{TableArticleParser, ListArticleParser, RecordReaderWrapper}
import extractors.ListMemberTypeExtractor
import it.cnr.isti.hpc.wikipedia.article.Article
import m.graphs.DemoableFlow
import m.IntrospectableFlow
import ratings.{TextEvidenceRating, TfIdfRating}
import scorer.Scorer
import tableExtraction.TableExtractor
import util.LoggingUtils._
import scala.concurrent.ExecutionContext.Implicits.global

import implicits.ConversionImplicits._
import ExtractionFlows._
/**
 * Created by nico on 20/07/15.
 */
object ListExtraction extends DemoableFlow {
  def flow(listener: ActorRef)(implicit system: ActorSystem): Unit = {
    implicit val materializer = ActorMaterializer()
    val filename = "data/random2000.json"
    val reader = new RecordReaderWrapper(filename)
    val articleList: Iterator[Article] = reader.iterator()

    val typeSink = Sink.fold[List[WikiFusedResult], WikiFusedResult](List()) { (list, result) => result :: list }


    implicit val extractor = new TableExtractor
    val extractor2 = new ListMemberTypeExtractor
    val rating = new TfIdfRating
    val rating2 = new TextEvidenceRating

    // Super simple:
    val out = IntrospectableFlow[Article, Unit](listener, Source(() => articleList))
      .mapConcat { article =>
      time("time for converting article:") {
        try {
          println(s"starting list for ${article.getTitleInWikistyle}")
          val parsedListPage = new ListArticleParser(article).parseArticle()

          println(s"starting table for ${article.getTitleInWikistyle}")
          val parsedTablePage = new TableArticleParser(article).parseArticle()

          val finalPage = (parsedListPage, parsedTablePage) match {
            case (Some(listPage), Some(tablePage)) => {
              Some(WikiListPage(
                listPage.listMembers ++ buildTableEntities(tablePage),
                listPage.title,
                listPage.wikiAbstract,
                listPage.categories
              ))
            }
            case (Some(listPage), _) => Some(listPage)
            case (_, Some(tablePage)) => {
              Some(WikiListPage(
                buildTableEntities(tablePage),
                tablePage.title,
                tablePage.wikiAbstract,
                tablePage.categories
              ))
            }
            case _ => None
          }

          finalPage.toList
        } catch {
          case e: Exception => println("parseTables exception: " + e); List()
        }
      }
    }
      .via(storeMembershipStatementsInFile("results/membership.ttl"))
      .mapAsyncUnordered(parallelCount) { page =>
      println(s"starting: ${page.title} count: ${page.listMembers.size}")

      timeFuture("duration for getting types:") {
        extractor2.getTypesMap(page.getEntityUris) map { typesMap =>
          if (typesMap.isEmpty) { println(s"${page.title} is empty!") }
          WikiListResult(page, typesMap, Map[Symbol, Map[String, Double]]().empty)
        }
      }
    }.mapAsyncUnordered(parallelCount) { result =>
      timeFuture("duration for computing tf-idf:") {
        rating.getRating(result).map { resultMap =>
          WikiListResult(result.page, result.types, Map(TfIdfRating.name -> resultMap))
        }
      }
    }.mapAsyncUnordered(parallelCount) { result =>
      val entities = result.page.getEntityUris
      val types = result.getTypes
      timeFuture("duration for computing text evidence:") {
        rating2.getRating(result).map { resultList =>
          val newScores = result.scores + (TextEvidenceRating.name -> resultList)
          WikiListResult(result.page, result.types, newScores)
        }
      }
    }
      .map { result =>
        time("duration for computing fused results:") {
          WikiFusedResult(result.page, Scorer.fuseResult(result))
        }
      }
      .runWith(Sink.ignore)
      .onComplete { r =>
//      system.shutdown()
    }
  }
}
