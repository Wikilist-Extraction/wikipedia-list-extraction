package dump

import dataFormats.{WikiPage, WikiTable, WikiList, WikiLink}
import it.cnr.isti.hpc.wikipedia.article.{Link, Article}
import scala.collection.JavaConverters._
/**
 * Created by nico on 19/06/15.
 */



trait DumpProcessor {
  val articleList: List[Article]

  def startProcessing(): List[Option[WikiPage]] = {
    articleList map processArticle
  }

  def processArticle(article: Article): Option[WikiPage]

  def getCategoriesOf(article: Article): List[WikiLink] = {
    article.getCategories.asScala.toList.map { link =>
      WikiLink(link.getDescription, link.getId)
    }
  }

}

class ListProcessor(val articleList: List[Article]) extends DumpProcessor {

  def getLinksIn(entry: String, links: List[Link]): List[WikiLink] = {
     for {
       link <- links
       if entry contains link.getDescription
     } yield WikiLink(link.getDescription, link.getId)
  }

  def entriesHaveOneLinkOnly(entries: List[List[WikiLink]]): Boolean = {
    entries.forall { _.length == 1 }
  }

  override def processArticle(article: Article): Option[WikiList] = {
    val lists = article.getLists.asScala.toList
    val links = article.getLinks.asScala.toList

    val wikiLinksForEntry = for {
      list <- lists
      entry <- list.asScala.toList
    } yield getLinksIn(entry, links)

    if (entriesHaveOneLinkOnly(wikiLinksForEntry)) {
      Some(WikiList(
        wikiLinksForEntry.flatten,
        article.getTitle,
        article.getSummary,
        getCategoriesOf(article)))
    } else {
      None
    }
  }
}

class TableProcessor(val articleList: List[Article]) extends DumpProcessor {

  override def processArticle(article: Article): Option[WikiTable] = {
    val tables = article.getTables.asScala.toList
    tables foreach { table =>
//      table.
    }
    Some(WikiTable(List(), article.getTitle, article.getSummary, getCategoriesOf(article)))
  }
}
