package dump

import java.io.FileWriter

import dispatch._
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.xml._

import scala.concurrent.ExecutionContext.Implicits.global

object XMLDumpCreator {
  val emptyDumpXml =
    <mediawiki xml:lang="en" version="0.10" xsi:schemaLocation="http://www.mediawiki.org/xml/export-0.10/ http://www.mediawiki.org/xml/export-0.10.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://www.mediawiki.org/xml/export-0.10/">
      <siteinfo>
        <sitename>Wikipedia</sitename>
        <dbname>enwiki</dbname>
        <base>https://en.wikipedia.org/wiki/Main_Page</base>
        <generator>MediaWiki 1.26wmf14</generator>
        <case>first-letter</case>
        <namespaces>
          <namespace case="first-letter" key="-2">Media</namespace>
          <namespace case="first-letter" key="-1">Special</namespace>
          <namespace case="first-letter" key="0"/>
          <namespace case="first-letter" key="1">Talk</namespace>
          <namespace case="first-letter" key="2">User</namespace>
          <namespace case="first-letter" key="3">User talk</namespace>
          <namespace case="first-letter" key="4">Wikipedia</namespace>
          <namespace case="first-letter" key="5">Wikipedia talk</namespace>
          <namespace case="first-letter" key="6">File</namespace>
          <namespace case="first-letter" key="7">File talk</namespace>
          <namespace case="first-letter" key="8">MediaWiki</namespace>
          <namespace case="first-letter" key="9">MediaWiki talk</namespace>
          <namespace case="first-letter" key="10">Template</namespace>
          <namespace case="first-letter" key="11">Template talk</namespace>
          <namespace case="first-letter" key="12">Help</namespace>
          <namespace case="first-letter" key="13">Help talk</namespace>
          <namespace case="first-letter" key="14">Category</namespace>
          <namespace case="first-letter" key="15">Category talk</namespace>
          <namespace case="first-letter" key="100">Portal</namespace>
          <namespace case="first-letter" key="101">Portal talk</namespace>
          <namespace case="first-letter" key="108">Book</namespace>
          <namespace case="first-letter" key="109">Book talk</namespace>
          <namespace case="first-letter" key="118">Draft</namespace>
          <namespace case="first-letter" key="119">Draft talk</namespace>
          <namespace case="first-letter" key="446">Education Program</namespace>
          <namespace case="first-letter" key="447">Education Program talk</namespace>
          <namespace case="first-letter" key="710">TimedText</namespace>
          <namespace case="first-letter" key="711">TimedText talk</namespace>
          <namespace case="first-letter" key="828">Module</namespace>
          <namespace case="first-letter" key="829">Module talk</namespace>
          <namespace case="first-letter" key="2600">Topic</namespace>
        </namespaces>
      </siteinfo>
    </mediawiki>
}

class XMLDumpCreator {
  import XMLDumpCreator._
//  val exportUrl = "http://en.wikipedia.org/w/index.php"
  val exportUrl = "http://en.wikipedia.org//wiki/Special:Export"

  val subDumpSize = 1000

  def postExport = url(exportUrl)
    .POST
    .setHeader("Accept-Encoding", "gzip,deflate")
    .addQueryParameter("title", "Special:Export")
    .addParameter("action", "submit")
    .addParameter("curonly", "1")

  def readFile(fileName: String): List[String] = {
    scala.io.Source.fromFile(fileName).getLines().toList
  }

  def writeToFile(fileName: String, dump: Node) = {
    val writer = new FileWriter(fileName)
    writer.write(dump.toString())
    writer.close()
  }

  def queryWikipedia(pages: List[String]): Future[Node] = {
    val req = postExport
      .addParameter("pages", pages.mkString("\n"))
    val r = Http(req OK as.xml.Elem)
//    r foreach println
    r
  }

  def addNodeTo(root: Node, newChild: Node): Node = {
    root match {
      case Elem(prefix, label, attribs, scope, children @ _*) =>
        Elem(prefix, label, attribs, scope, children ++ newChild : _*)
      case _ => error("Can only add children to elements!")
    }
  }

  def getDump(fileName: String): Future[Node] = {
    val pages = readFile(fileName)
    val subDumps = pages.grouped(subDumpSize).toList map queryWikipedia

    Future.sequence(subDumps).map { dumps =>
      dumps.fold(emptyDumpXml) { (acc, subDump) =>
//        println("subDump: " + subDump + "\n\n")
        val pageElems = subDump \\ "page"
//        println(pageElems)
        pageElems.fold(acc)(addNodeTo)
      }
    }
  }

  def readFromAndWriteTo(fileNameFrom: String, fileNameTo: String) = {
    val dump = getDump(fileNameFrom)
    dump foreach(_ => println("success"))
    dump foreach (writeToFile(fileNameTo, _))
    Await.ready(dump, 20 seconds)
  }
}
