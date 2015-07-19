package textEvidence

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.scalatest.FlatSpec
import ratings.TextEvidenceRating
import scala.concurrent.Await
import scala.concurrent.duration._

class TextEvidenceSpec extends FlatSpec {

  val extractor = new TextEvidenceRating()

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

  val typesList = List(
    "http://dbpedia.org/ontology/Agent",
    "http://dbpedia.org/ontology/Athlete",
    "http://dbpedia.org/ontology/BasketballPlayer",
    "http://dbpedia.org/ontology/Person",
    "http://dbpedia.org/ontology/BaseballPlayer",
    "http://dbpedia.org/ontology/Coach",
    "http://dbpedia.org/ontology/CollegeCoach",
    "http://dbpedia.org/ontology/GridironFootballPlayer",
    "http://dbpedia.org/class/yago/Athlete109820263",
    "http://dbpedia.org/class/yago/LivingPeople",
    "http://dbpedia.org/class/yago/BasketballPlayer109842047",
    "http://dbpedia.org/class/yago/BasketballPlayersFromPennsylvania"
  )


  implicit val actorSys = ActorSystem("wikilist-extraction")
  implicit val materializer = ActorMaterializer()


//  it should "get a list of types with their score" in {
//    val resFuture = extractor.compute(resourceList, typesList)
//    val results = Await.result(resFuture, 20 seconds)
//    results
//  }

  it should "get the title of a given result" in {

    val uri = "http://dbpedia.org/resource/Bill_Haarlow"
    val f = extractor.getTitle(uri)
    val results = Await.result(f, 20 seconds)
    results
  }

  it should "get the abstract of a given result" in {

    val uri = "http://dbpedia.org/resource/Bill_Haarlow"

    val f = extractor.getAbstract(uri)
    val results = Await.result(f, 20 seconds)
    results
  }

}
