package dump

import dataFormats.WikiTablePage
import it.cnr.isti.hpc.wikipedia.article.Article
import tableExtraction.TableExtractor

import scala.collection.JavaConverters._
import implicits.ConversionImplicits._

object Main {
  def main(args: Array[String]) = {
    val filename = args(0)

    val reader = new RecordReaderWrapper(filename)
    val articleList: List[Article] = reader.getArticlesList.asScala.toList

    val listExtractor = new ListProcessor(articleList)
    val tableExtractor = new TableProcessor(articleList)

    val lists = listExtractor.startProcessing()
    val tables = tableExtractor.startProcessing()


    val extractor = new  TableExtractor()

    val tableEntities = tables.map((page) => extractor.extractTableEntities(page
      .asInstanceOf[WikiTablePage].tables))

    println(lists)
    println(tables)
  }
}
