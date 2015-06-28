package dump

import it.cnr.isti.hpc.wikipedia.article.Article

import scala.collection.JavaConverters._

object Main {
  def main(args: Array[String]) = {
    val filename = args(0)

    val reader = new RecordReaderWrapper(filename)
    val articleList: List[Article] = reader.getArticlesList.asScala.toList

    // preprocessing
    // beautify json

    // extraction


//    val listExtractor = new ListProcessor(articleList)
//    val tableExtractor = new TableProcessor(articleList)
//
//    val lists = listExtractor.startProcessing()
//    val tables = tableExtractor.startProcessing()
//
//    println(lists)
  }
}
