package dump

import it.cnr.isti.hpc.wikipedia.article.{Link, Article}
import scala.collection.JavaConverters._

/**
 * Created by nico on 19/06/15.
 */
trait DumpProcessor {
  val articleList: List[Article]

  def startProcessing() = {
    articleList foreach processArticle
  }

  def processArticle(article: Article)

}

class ListProcessor(val articleList: List[Article]) extends DumpProcessor {

  def getLinksIn(entry: String, links: List[Link]): List[Link] = {
     for {
       link <- links
       if entry contains link.getDescription
     } yield link
  }


  override def processArticle(article: Article): Unit = {
    val lists = article.getLists().asScala.toList
    val links = article.getLinks().asScala.toList

    val linksInList = for {
      list <- lists
      entry <- list.asScala.toList
      link <- getLinksIn(entry, links)
    } yield link
    println(linksInList)
  }
}

class TableProcessor(val articleList: List[Article]) extends DumpProcessor {

  override def processArticle(article: Article): Unit = ???
}
