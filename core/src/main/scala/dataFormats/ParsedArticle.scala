package dataFormats

import parser.Parser
import it.cnr.isti.hpc.wikipedia.article.{Article, Table}
import scala.collection.JavaConverters._

/**
 * Created by sven on 28/06/15.
 */

object ParsedArticle {


  def parseFromArticle(article: Article): ParsedArticle = {

    val parser = new Parser(article.getLinks.asScala.toList, article.getExternalLinks.asScala.toList)

    val lists = for(l <- article.getLists.asScala.toList) yield l.asScala.toList

    new ParsedArticle(
      article.getTitle,
      article.getSections.asScala.toList,
      article.getSummary,
      parser.parseLists(lists),
      parser.parseTables(article.getTables.asScala.toList)
    )
  }
}

class ParsedArticle(
                     _title: String,
                     _sections: List[String],
                     _summary: String,
                     _lists: List[List[String]],
                     _tables: List[Table]
                     ) {

  val title: String = _title
  val sections:  List[String] = _sections
  val summary: String = _summary
  val lists: List[List[String]] = _lists
  val tables: List[Table] = _tables

}
