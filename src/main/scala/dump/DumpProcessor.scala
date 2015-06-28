package dump

import dataFormats.{WikiPage, WikiTable, WikiList, WikiLink, Literal}
import it.cnr.isti.hpc.wikipedia.article.{Link, Article, Template}
import scala.collection.JavaConverters._


trait EntryCleaner {
  def filterConvertTemplate(literal: Literal): Literal = {
    def extractTemplateValue(raw: String): String = {
      // execute regex via match and return matched groups
      val regexGroups = """^TEMPLATE[\w+, value:(.+),?.*]""".r

      raw match {
        case regexGroups(group) => group
        case _ => raw
      }

    }

    Literal(extractTemplateValue(literal.raw), literal.dataType)
  }
}

trait DumpProcessor {
  val articleList: List[Article]

  def startProcessing(): List[WikiPage] = {
    articleList map processArticle
  }

  def processArticle(article: Article): WikiPage

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
      val wikiList = WikiList(
        wikiLinksForEntry.flatten,
        article.getTitle,
        article.getSummary,
        getCategoriesOf(article))

      Some(wikiList)
    } else {
      None
    }
  }
}

class TableProcessor(val articleList: List[Article]) extends DumpProcessor with EntryCleaner {

  override def processArticle(article: Article): Option[WikiTable] = {
    val tables = article.getTables.asScala.toList
    tables foreach { table =>
//      table.
    }
    Some(WikiTable(List(), article.getTitle, article.getSummary, getCategoriesOf(article)))
  }
}
