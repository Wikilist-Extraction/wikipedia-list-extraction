package listCrossing

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.scalatest.FlatSpec
import textEvidence.TextEvidenceExtractor
import typesExtraction.{TfIdfWorker, ListMemberTypeExtractor}
import scala.concurrent.Await
import scala.concurrent.duration._

class TestListCrossing extends FlatSpec {

  val typeExtractor = new ListMemberTypeExtractor()
  val tfIdfExtractor = new TfIdfWorker()
  val textEvidenceExtractor = new TextEvidenceExtractor()
  val listCrosser = new ListCrosser()

  val resourceList = List(
    "http://dbpedia.org/resource/?arko_?abarkapa",
    "http://dbpedia.org/resource/Barney_Cable",
    "http://dbpedia.org/resource/Soup_Cable",
    "http://dbpedia.org/resource/Bruno_Caboclo",
    "http://dbpedia.org/resource/Jason_Caffey",
    "http://dbpedia.org/resource/Michael_Cage",
    "http://dbpedia.org/resource/Gerald_A._Calabrese",
    "http://dbpedia.org/resource/Nick_Calathes",
    "http://dbpedia.org/resource/Jos?_Calder?n_(basketball)",
    "http://dbpedia.org/resource/Adrian_Caldwell",
    "http://dbpedia.org/resource/Jim_Caldwell_(basketball)",
    "http://dbpedia.org/resource/Joe_Caldwell",
    "http://dbpedia.org/resource/Kentavious_Caldwell-Pope",
    "http://dbpedia.org/resource/Bill_Calhoun_(basketball)",
    "http://dbpedia.org/resource/Corky_Calhoun",
    "http://dbpedia.org/resource/Bob_Calihan",
    "http://dbpedia.org/resource/Demetrius_Calip",
    "http://dbpedia.org/resource/Tom_Callahan",
    "http://dbpedia.org/resource/Rick_Calloway",
    "http://dbpedia.org/resource/Ernie_Calverley",
    "http://dbpedia.org/resource/Mack_Calvin",
    "http://dbpedia.org/resource/Dexter_Cambridge",
    "http://dbpedia.org/resource/Dexter_Cambridge",
    "http://dbpedia.org/resource/Marcus_Camby",
    "http://dbpedia.org/resource/Joe_Camic",
    "http://dbpedia.org/resource/Elden_Campbell",
    "http://dbpedia.org/resource/Fred_Campbell_(basketball)",
    "http://dbpedia.org/resource/Ken_Campbell_(basketball)"
  )

  implicit val actorSys = ActorSystem("wikilist-extraction")
  implicit val materializer = ActorMaterializer()

  it should "get the result of a crossed tf-idf and text-evidence list" in {
    val types = typeExtractor.getTypesMap(resourceList)
    val typesCount = Await.result(types, 20 seconds)
    val tfIdfFuture = tfIdfExtractor.getTfIdfScores(typesCount)
    val tfIdfList = Await.result(tfIdfFuture, 20 seconds)

    val typesList: List[String] = typesCount.keys.toList
    val textEvidenceFuture = textEvidenceExtractor.compute(resourceList, typesList)
    val textEvidenceList = Await.result(textEvidenceFuture, 20 seconds)

    val crossedList = listCrosser.crossLists(tfIdfList, textEvidenceList)
    crossedList
  }

}
