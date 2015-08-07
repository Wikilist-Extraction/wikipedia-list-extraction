package scorer

import dataFormats.{WikiListPage, WikiListResult}
import ratings.{TfIdfRating, TextEvidenceRating}

/**
 * Created by sven on 18/07/15.
 */
object Scorer {

  val thresholds = Map[Symbol, Double](
    'tfIdf -> 0.1,
    'textEvidence -> 0.0
  )

  val finalThreshold = 0.4

  val weights = Map[Symbol, Double](
    'tfIdf -> 1,
    'textEvidence -> 1
  )
  val weightsSum = weights.foldLeft(0.0) { (acc, keyValue) => acc + keyValue._2 }

//  val owlThingType = "http://www.w3.org/2002/07/owl#Thing"

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
    val scores: Map[Symbol, Map[String, Double]] = result.scores

    if (allTypes.isEmpty || scores.isEmpty) {
      return List[String]()
    }

    scores
      .map { case (algorithmName, scoredTypes) =>
        val mutatedScores = algorithmName match {
          case 'tfIdf => {
            // idea: find the biggest gap between 2 scorings and use the lower scoredType as threshold
            //val scoredTypeCount = scoredTypes.size
            //var scoreDistanceSum = 0.0 //scoredTypes.foldLeft(0.0) { (acc, scoredType) => acc + scoredType._2 }

            val sortedScoredTypes = scoredTypes
              .toList
              .sortBy({ case(_, score) => -score })

            var oldScoredType: (String, Double) = sortedScoredTypes.head
            var maxScoredType: (String, Double) = sortedScoredTypes.head
            var maxScoreDistance: Double = 0.0
            val minScore = sortedScoredTypes.last._2

            sortedScoredTypes.foreach({ case (typeName, score) =>
              val oldScore = oldScoredType._2
              //scoreDistanceSum += oldScore - score
              if (oldScore - score >= maxScoreDistance) {
                maxScoreDistance = oldScore - score
                maxScoredType = (typeName, score)
              }

              oldScoredType = (typeName, score)
            })

            // val scoreDistanceAvg = scoreDistanceSum / scoredTypeCount

            if (minScore < maxScoreDistance) {
              scoredTypes.filter { case (_, score) => score > maxScoredType._2 }
            } else {
              scoredTypes
            }

            /*
            // idea: threshold all types below olw:Thing score, which should be the most unspecific thing
            val owlThingScoreOption: Option[(String, Double)] = scoredTypes.find { case (typeName, _) => typeName.equals(owlThingType) }
            val owlThingScore = owlThingScoreOption match {
              case Some((_, score)) => score
              case _ => 0.0
            }

            scoredTypes.filter { case (_, score) => score >= owlThingScore }
            */
          }
          case _ => scoredTypes
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
          typeName -> (algorithmScores + (algorithmName -> scoredTypes.getOrElse[Double](typeName, 0)))
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
      .filter( _._2 > finalThreshold )
      .keys
      .toList
  }
}
