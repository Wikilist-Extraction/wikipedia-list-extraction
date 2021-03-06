package runnables

import dump.XMLDumpCreator
import scala.io.Source

/**
 * Created by nico on 16/07/15.
 */
object XmlDumpCreator {
  def main(args: Array[String]) {
    val from = args(0)
    val to = args(1)

    val dumpCreator = new XMLDumpCreator

    dumpCreator.readFromAndWriteTo(from, to)
    System.exit(0)
  }
}
