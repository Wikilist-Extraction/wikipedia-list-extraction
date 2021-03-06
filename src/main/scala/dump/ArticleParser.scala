package dump

import dataFormats._
import it.cnr.isti.hpc.wikipedia.article.{Table, Link, Article}

import implicits.ConversionImplicits._

import scala.collection.mutable.ListBuffer

/**
 * Parser for parsing one article
 */
trait ArticleParser {
  val article: Article
  //def links: List[Link] = article.getLinks
  def parseArticle(): Option[WikiPage]

  def getCategoriesOf(article: Article): List[WikiLink] = {
    article.getCategories.map { link =>
      WikiLink(link.getDescription, link.getId)
    }
  }

  def getLinksIn(entry: String): List[WikiLink] = {
    val regexGroups = """LINK\[(.+?)\|(.+?)\]""".r

    val listBuffer = new ListBuffer[WikiLink]()
    val it = regexGroups.findAllIn(entry).matchData
    while (it.hasNext) {
      val subgroups = it.next.subgroups
      listBuffer += WikiLink(subgroups.head, subgroups(1))
    }

    listBuffer.toList
  }
}

trait EntryCleaner {
  def extractTemplateValue(raw: String): String = {
    // execute regex via match and return matched groups
    val regexGroups = """^TEMPLATE\[\w+, ([^,]+),?.*\]$""".r

    raw match {
      case regexGroups(wrappedValue) => wrappedValue
      case _ => raw
    }
  }

  def extractTemplateFrom(literal: Literal): Literal = {
    Literal(extractTemplateValue(literal.raw), literal.dataType)
  }
}

class ListArticleParser(val article: Article) extends ArticleParser {

  def entriesHaveOneLinkOnly(entries: List[List[WikiLink]]): Boolean = {
    entries.forall { _.length <= 1 }
  }

  def entriesHaveAtLeastOneLink(entries: List[List[WikiLink]]): Boolean = {
    entries.exists(_.nonEmpty)
  }

  def removeListOfLists(entries: List[List[WikiLink]]): List[List[WikiLink]] = {
    entries.map { list =>
      list.filter { link => !link.id.contains("List_of_")}
    }.filter { list => list.nonEmpty }
  }

  override def parseArticle(): Option[WikiListPage] = {
    val lists = article.getLists

    val wikiLinksForEntry = for {
      list <- lists
      entry <- list
    } yield getLinksIn(entry)

    if (entriesHaveAtLeastOneLink(wikiLinksForEntry)) {
      val filteredWikiLinks = removeListOfLists(wikiLinksForEntry)

      if (filteredWikiLinks.isEmpty) {
        None
      } else {
        val wikiList = WikiListPage(
          wikiLinksForEntry flatMap (_.headOption), //wikiLinksForEntry.flatten,
          article.getTitle,
          article.getSummary,
          getCategoriesOf(article))

        Some(wikiList)
      }
    } else {
      None
    }
  }
}

class TableArticleParser(val article: Article) extends ArticleParser with EntryCleaner {

  def getEntry(rawCell: String): Entry = {
    val links = getLinksIn(rawCell)
    if (links.nonEmpty) {
      // TODO: decide to choose the right link
      links.head
    } else {
     //extractTemplateFrom(Literal(rawCell, "String"))
      Literal(rawCell, "String")
    }
  }

  def getTableCells(rawCells: List[String]): List[TableCell] = {
    rawCells map { cell => TableCell(getEntry(cell)) }
  }

  def getTableRows(rawTable: List[List[String]]): List[TableRow] = {
    rawTable map { rawCells => TableRow(getTableCells(rawCells)) }
  }

  def getTable(table: Table): WikiTable = {
    val rawTable = table.getTable
    // TODO: process header
    val header = rawTable.head
    val rows = getTableRows(rawTable.tail)
    WikiTable(header, rows, table.getName)
  }

  override def parseArticle(): Option[WikiTablePage] = {
    val tables = article.getTables
    val wikiTables = tables map getTable
    Some(WikiTablePage(wikiTables, article.getTitle, article.getSummary, getCategoriesOf(article)))
  }
}
