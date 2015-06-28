package dataFormats

/**
 * Created by nico on 19/06/15.
 */

trait WikiPage {
  val title: String
  val wikiAbstract: String
  val categories: List[WikiLink]
}

case class WikiList(
                     listMembers: List[WikiLink],
                     title: String,
                     wikiAbstract: String,
                     categories: List[WikiLink]) extends WikiPage


case class WikiTable(
                      tableRows: List[TableRow],
                      title: String,
                      wikiAbstract: String,
                      categories: List[WikiLink]) extends WikiPage


case class WikiListEntry(
                        text: String,
                        links: List[WikiLink])

case class WikiLink(label: String, uri: String)

case class TableRow()
case class TableCell(entry: String)
