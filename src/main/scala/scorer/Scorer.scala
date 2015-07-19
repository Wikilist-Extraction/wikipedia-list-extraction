package scorer

import dataFormats.{WikiListPage, WikiListResult}
import ratings.{TfIdfRating, TextEvidenceRating}

/**
 * Created by sven on 18/07/15.
 */
object Scorer {

  val thresholds = Map[Symbol, Double](
    'tfIdf -> 0.1,
    'textEvidence -> 0.05
  )

  val finalThreshold = 0.1

  val weights = Map[Symbol, Double](
    'tfIdf -> 2,
    'textEvidence -> 1
  )
  val weightsSum = weights.foldLeft(0.0) { (acc, keyValue) => acc + keyValue._2 }

  val owlThingType = "http://www.w3.org/2002/07/owl#Thing"

  def normalizeScores(scoredTypes: Map[String, Double]): Map[String, Double] = {
    val highestScore = scoredTypes.values.max
    scoredTypes.mapValues { score => score / highestScore }
  }

  def thresholdScores(scoredTypes: Map[String, Double], threshold: Double) = {
    scoredTypes.filter { _._2 >= threshold }
  }

  def buildEmptyFuseMapFrom(allTypes: List[String]): Map[String, Map[Symbol, Double]] = {
    val emptyMap = Map[String, Map[Symbol, Double]]()
    allTypes.foldLeft(emptyMap) { (acc, typeName) => acc + (typeName -> Map[Symbol, Double]()) }
  }

  def fuseResult(result: WikiListResult): List[String] = {
    val page: WikiListPage = result.page
    val allTypes: Map[String, Int] = result.types
    val scores: Map[Symbol, Map[String,Double]] = result.scores

    scores
      .map { case(algorithmName, scoredTypes) =>
        val mutatedScores = algorithmName match {
          case 'tfIdf => {
            // idea: threshold all types below olw:Thing score, which should be the most unspecific thing
            val owlThingScoreOption: Option[(String, Double)] = scoredTypes.find { case (typeName, _) => typeName.equals(owlThingType) }
            val owlThingScore = owlThingScoreOption match {
              case Some((_, score)) => score
              case _ => 0.0
            }

            scoredTypes.filter { case (_, score) => score < owlThingScore }
          }
          case _ => { scoredTypes }
        }

        if (mutatedScores.isEmpty) {
          algorithmName -> mutatedScores
        } else {
          val normalizedScores = normalizeScores(mutatedScores)
          val thresholdedScores = thresholdScores(normalizedScores, thresholds(algorithmName))
          algorithmName -> thresholdedScores
        }
      }
      .foldLeft(buildEmptyFuseMapFrom(allTypes.keys.toList)) { (appendedScoreTypes, scores) =>
        val algorithmName = scores._1
        val scoredTypes = scores._2

        appendedScoreTypes.map { case (typeName, algorithmScores) =>
          (typeName -> (algorithmScores + (algorithmName -> scoredTypes.getOrElse[Double](typeName, 0))))
        }
      }
      .foldLeft(Map[String, Double]()) { (acc, keyValue) =>
        val typeName = keyValue._1
        val algorithmScores: Map[Symbol, Double] = keyValue._2

        val tmpScore = algorithmScores.foldLeft(0.0) { (acc, keyValue) =>
          val algorithmName = keyValue._1
          val score = keyValue._2

          acc + (weights(algorithmName) * score)
        }

        acc + (typeName -> tmpScore / weightsSum)
      }
      .filter { _._2 < finalThreshold }
      .keys
      .toList
  }
}
