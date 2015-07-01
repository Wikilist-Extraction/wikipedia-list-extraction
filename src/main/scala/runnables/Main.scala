package runnables

import dataFormats.WikiTablePage
import dump.{ListProcessor, RecordReaderWrapper, TableProcessor}
import it.cnr.isti.hpc.wikipedia.article.Article
import tableExtraction.{RDFTable, TableExtractor}

import scala.collection.JavaConverters._

object Main {
  def main(args: Array[String]) = {
    val filename = args(0)

    val reader = new RecordReaderWrapper(filename)
    val articleList: List[Article] = reader.getArticlesList.asScala.toList

    val listExtractor = new ListProcessor(articleList)
    val tableExtractor = new TableProcessor(articleList)

    val listPages = listExtractor.startProcessing()
    val tablePages = tableExtractor.startProcessing()


    val extractor = new  TableExtractor()

    println(listPages)
    println(tablePages)
  }
}
