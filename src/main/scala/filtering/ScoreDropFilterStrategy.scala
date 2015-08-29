package filtering

import dataFormats.WikiFusedResult

/**
 * Created by nico on 28/08/15.
 */
class ScoreDropFilterStrategy extends ScoreFilterStrategy {

  def findDrop(result: WikiFusedResult): List[String] = {
    // idea: find the biggest gap between 2 scorings and use the lower scoredType as threshold
    //val scoredTypeCount = scoredTypes.size
    //var scoreDistanceSum = 0.0 //scoredTypes.foldLeft(0.0) { (acc, scoredType) => acc + scoredType._2 }

    val sortedScoredTypes = result.types
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

    //    if (minScore < maxScoreDistance) {
    result.types.filter { case (_, score) => score > maxScoredType._2 }.keys.toList
    //    } else {
    //      result.types.keys.toList
    //    }

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

  override def filterScores(result: WikiFusedResult): List[String] = {
    if (allTypesCorrect(result))
      result.wikiListScores.getTypes
    else
      findDrop(result)
  }
}
