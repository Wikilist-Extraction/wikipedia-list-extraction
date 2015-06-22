package dump

import it.cnr.isti.hpc.wikipedia.article.Article

import scala.collection.JavaConverters._

/**
 * Created by nico on 19/06/15.
 */
object Main {
  def main(args: Array[String]) = {
    val filename = args(0)

    val reader = new RecordReaderWrapper(filename)

    val articleList: List[Article] = reader.getArticlesList.asScala.toList

    new ListProcessor(articleList).startProcessing()
    new TableProcessor(articleList).startProcessing()
  }
}
