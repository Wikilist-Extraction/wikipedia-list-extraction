package dataFormats

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


trait Entry
case class WikiLink(label: String, uri: String) extends Entry
case class Literal(raw: String, dataType: String) extends Entry

case class TableRow(cells : List[TableCell])
case class TableCell(entry : Entry)
