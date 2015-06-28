package dump

import dataFormats.{WikiPage, WikiTable, WikiList, WikiLink}
import it.cnr.isti.hpc.wikipedia.article.{Link, Article}
import scala.collection.JavaConverters._

/**
 * Created by nico on 19/06/15.
 */
trait DumpProcessor {
  val articleList: List[Article]

  def startProcessing(): List[WikiPage] = {
    articleList map processArticle
  }

  def processArticle(article: Article): WikiPage

  def getCategoriesoOf(article: Article): List[WikiLink]= {
    article.getCategories.asScala.toList.map { link =>
      WikiLink(link.getDescription, link.getId)
    }
  }

}

class ListProcessor(val articleList: List[Article]) extends DumpProcessor {

  def getLinksIn(entry: String, links: List[Link]): List[Link] = {
     for {
       link <- links
       if entry contains link.getDescription
     } yield link
  }

  override def processArticle(article: Article): WikiList = {
    val lists = article.getLists.asScala.toList
    val links = article.getLinks.asScala.toList


    val wikiLinks = for {
      list <- lists
      entry <- list.asScala.toList
      link <- getLinksIn(entry, links)
    } yield WikiLink(link.getDescription, link.getId)

    WikiList(wikiLinks, article.getTitle, article.getSummary, getCategoriesoOf(article))
  }
}

class TableProcessor(val articleList: List[Article]) extends DumpProcessor {

  override def processArticle(article: Article): WikiTable = {
    val tables = article.getTables.asScala.toList
    tables foreach { table =>
    }
    WikiTable(List(), article.getTitle, article.getSummary, getCategoriesoOf(article))
  }
}
