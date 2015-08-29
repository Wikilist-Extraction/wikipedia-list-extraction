package filtering

import dataFormats.{WikiFusedResult, WikiListScores}

/**
 * Created by nico on 28/08/15.
 */
class ThresholdFilterStrategy(threshold: Double) extends ScoreFilterStrategy {
  override def filterScores(fusedResult: WikiFusedResult): List[String] = {
    fusedResult.types
      .filter { case (typeName, score) => score > threshold }
      .keys
      .toList
  }
}
