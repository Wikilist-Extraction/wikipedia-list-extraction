package dataFormats

import parser.Parser
import it.cnr.isti.hpc.wikipedia.article.Article
import scala.collection.JavaConverters._

/**
 * Created by sven on 28/06/15.
 */

object ParsedArticle {

  def parseFromArticle(article: Article): ParsedArticle = {

    val parser = new Parser(article.getLinks, article.getExternalLinks)

    new ParsedArticle(
      article.getTitle,
      article.getSections,
      article.getSummary,
      parser.parseLists(article.getLists.asScala.toList),
      parser.parseTables(article.getTables)
    )
  }
}

class ParsedArticle(
                     _title: String,
                     _sections: List[String],
                     _summary: String,
                   _lists: List[List[String]],
                   _tables: List[List[String]]
                     ) {

  val title: String = _title
  val sections:  List[String] = _sections
  val summary: String = _summary
  val lists: List[List[String]] = _lists
  val tables: List[List[String]] = _tables


}
