package tableExtraction

import dataFormats.{WikiTable, Literal, WikiLink, WikiTablePage}
import implicits.ConversionImplicits._

/**
 * Created by nico on 30/06/15.
 */
class RDFTableWrapper(page: WikiTablePage) {

  val rdfTables: List[RDFTable] = convertTables()
  val tableExtractor = new TableExtractor()

  def createJavaRDFTable(table: WikiTable): RDFTable = {
    val rows = table.rows map { row =>
      val cells = row.cells map { cell =>
        cell.entry match {
          case WikiLink(label, uri) => new TableEntry(uri, label)
          case Literal(raw, _) => new TableEntry(raw)
        }
      }
      new TableRow(cells)
    }
    new RDFTable(rows)
  }

  def convertTables(): List[RDFTable] = {
    page.tables map createJavaRDFTable
  }

  def getResuls() = {
    val links = tableExtractor.extractTableEntities(rdfTables)
  }
}
