package dataFormats

import java.net.URLEncoder
import util.UriUtils._

trait WikiPage {
  val title: String
  val wikiAbstract: String
  val categories: List[WikiLink]

  def titleUri = "http://dbpedia.org/resource/" + encodeWikistyle(title)
}

case class WikiListPage(
                         listMembers: List[WikiLink],
                         title: String,
                         wikiAbstract: String,
                         categories: List[WikiLink]) extends WikiPage {
  def getEntityUris: List[String] = listMembers.map(_.toUri)
}


case class WikiTablePage(
                          tables: List[WikiTable],
                          title: String,
                          wikiAbstract: String,
                          categories: List[WikiLink]) extends WikiPage


trait Entry
case class WikiLink(label: String, id: String) extends Entry {
  def toUri: String = "http://dbpedia.org/resource/" + URLEncoder.encode( id.replaceAll(" ", "_"), "UTF-8")
}
case class Literal(raw: String, dataType: String) extends Entry

case class WikiTable(header: List[String], rows: List[TableRow], name: String)
case class TableRow(cells : List[TableCell])
case class TableCell(entry : Entry)

// page - wikipedia parsed result
// types - type name --> type count
// scores - approach name (tfidf, text evidence, ...) --> (type name --> type score)
case class WikiListScores(page: WikiListPage, types: Map[String, Int], scores: Map[Symbol, Map[String, Double]]) {
  def getTypes: List[String] = types.keys.toList
}

case class WikiFusedResult(wikiListScores: WikiListScores, types: Map[String, Double])

case class WikiListResult(page: WikiListPage, types: List[String])
