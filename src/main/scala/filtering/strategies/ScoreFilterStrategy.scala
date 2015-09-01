package filtering.strategies

import dataFormats.WikiFusedResult
import util.Config._

/**
 * Created by nico on 28/08/15.
 */
trait ScoreFilterStrategy {
  val allTypesCorrectThreshold = config.getDouble("filtering.thresholds.allTypesCorrect")

  def mean(xs: List[Int]): Double = xs match {
    case ys => ys.sum / ys.size.toDouble
  }

  def stddev(xs: List[Int], avg: Double): Double = xs match {
    case ys => math.sqrt((0.0 /: ys) {
      (a,e) => a + math.pow(e - avg, 2.0)
    } / xs.size)
  }

  def allTypesCorrect(result: WikiFusedResult): Boolean = {
    val typeCounts = result.wikiListScores.types.values.toList
    val standardDerivation = stddev(typeCounts, mean(typeCounts))
    standardDerivation < allTypesCorrectThreshold
  }

  def filterScores(result: WikiFusedResult): List[String]
}
