package runnables

import dump.XMLDumpCreator
import it.cnr.isti.hpc.wikipedia.reader.WikipediaArticleReader

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Created by sven on 15/07/15.
 */
object XmlToJsonConverter {
  def main (args: Array[String]) {
    val xmlFile = args(0)
    val jsonFile = args(1)

    val wap: WikipediaArticleReader  = new WikipediaArticleReader(xmlFile, jsonFile, "en")

    try {
      wap.start()
    } catch {
      case e: Exception => {
        println("parsing the mediawiki {}", e.toString)
        System.exit(-1)
      }
    }
  }
}
