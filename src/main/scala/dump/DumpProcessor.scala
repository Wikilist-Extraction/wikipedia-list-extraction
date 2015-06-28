package dump

import dataFormats._
import it.cnr.isti.hpc.wikipedia.article.{Table, Link, Article}
import scala.collection.JavaConverters._

import implicits.ConversionImplicits._
/**
 * Created by nico on 19/06/15.
 */

trait DumpProcessor {
  val articleList: List[Article]

  def startProcessing(): List[WikiPage] = {
    articleList
      .map(processArticle)
      .filter(_.isDefined)
      .map(_.get)
  }

  def processArticle(article: Article): Option[WikiPage]

  def getCategoriesOf(article: Article): List[WikiLink] = {
    article.getCategories.map { link =>
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

  override def processArticle(article: Article): Option[WikiListPage] = {
    val lists = article.getLists
    val links = article.getLinks

    val wikiLinksForEntry = for {
      list <- lists
      entry <- list
    } yield getLinksIn(entry, links)

    if (entriesHaveOneLinkOnly(wikiLinksForEntry)) {
      Some(WikiListPage(
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

  def getEntry(rawCell: String): Entry = {
    // TODO: decide if it is a Link or Literal and filter out templates
    Literal(rawCell, "")
  }

  def getTableCells(rawCells: List[String]): List[TableCell] = {
    rawCells map ( cell => TableCell(getEntry(cell)) )
  }

  def getTableRows(rawTable: List[List[String]]): List[TableRow] = {
    rawTable map { rawCells => TableRow(getTableCells(rawCells)) }
  }

  def getTable(table: Table): WikiTable = {
    val rawTable = table.getTable
    val header = rawTable.head
    val rows = getTableRows(rawTable.tail)
    WikiTable(header, rows, table.getName)
  }

  override def processArticle(article: Article): Option[WikiTablePage] = {
    val tables = article.getTables
    val wikiTables = tables map getTable
    Some(WikiTablePage(wikiTables, article.getTitle, article.getSummary, getCategoriesOf(article)))
  }
}
