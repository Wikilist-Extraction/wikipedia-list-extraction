package ratings

import akka.stream.Materializer
import dataFormats.WikiListScores

import scala.concurrent.Future

/**
 * Created by sven on 18/07/15.
 */

trait Rating {
  val name: Symbol
}

trait RatingResult {
  // rating from
  // type name -> score
  def getRating(wikiListResult: WikiListScores)(implicit materializer: Materializer): Future[Map[String, Double]]
}

