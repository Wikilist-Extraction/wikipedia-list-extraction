package filtering

import dataFormats.WikiFusedResult

/**
 * Created by nico on 28/08/15.
 */
trait ScoreFilterStrategy {
  def filterScores(result: WikiFusedResult): List[String]
}
