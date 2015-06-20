package dataFormats

/**
 * Created by nico on 19/06/15.
 */

case class WikiList(listMembers: List[WikiLink], title: String, wikiAbstract: String)
case class WikiTable(tableRows: List[TableRow])


case class WikiLink(label: String, uri: String)

case class TableRow()
case class TableCell(entry: String)
