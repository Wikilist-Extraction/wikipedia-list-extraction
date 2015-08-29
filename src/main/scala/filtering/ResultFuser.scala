package filtering

import dataFormats.{WikiFusedResult, WikiListScores}
import util.Config._
/**
 * Created by nico on 28/08/15.
 */
object ResultFuser {

  val weights = Map[Symbol, Double](
    'tfIdf -> config.getInt("fusing.weights.tfIdf"),
    'textEvidence -> config.getInt("fusing.weights.textEvidence")
  )

  val weightsSum = weights.foldLeft(0.0) { (acc, keyValue) => acc + keyValue._2 }

  def normalizeScores(scoredTypes: Map[String, Double]): Map[String, Double] = {
    val highestScore = scoredTypes.values.max
    scoredTypes.mapValues { score => score / highestScore }
  }

  def weightedScore(algorithmName: Symbol, score: Double): Double = {
    val weight: Double = weights getOrElse(algorithmName, 0)
    (score * weight) / weightsSum
  }

  def fuseResults(result: WikiListScores): WikiFusedResult = {

    val normalizedScores = result.scores map { case (algorithmName, scoreMap) =>
      algorithmName -> normalizeScores(scoreMap)
    }

    def fuseTypeResults(typeName: String): (String, Double) = {
      var fusedScore = 0d

      for {
        (algorithmName, resultScores) <- normalizedScores
      } {
         fusedScore += weightedScore(algorithmName, resultScores(typeName))
      }

      (typeName, fusedScore)
    }

    val fusedTypes = result.getTypes map fuseTypeResults

    WikiFusedResult(result, fusedTypes.toMap)
  }

}
