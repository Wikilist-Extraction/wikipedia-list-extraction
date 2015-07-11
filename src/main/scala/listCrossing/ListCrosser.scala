package listCrossing

object ListCrosser {
  val tfIdfWeight : Double = 0.5
  val textEvidenceWeight : Double = 0.5
}

class ListCrosser {
  import ListCrosser._

  def calculateScore(tfIdfScore: Double, textEvidenceScore: Double) : Double = {
    tfIdfScore * tfIdfWeight + textEvidenceScore * textEvidenceWeight
  }

  def normalizeList(list: Map[String, Double]) : Map[String, Double] = {
    val maxValue : Double = list.maxBy(_._2)._2

    for ((key, value) <- list) yield { key -> (value / maxValue) }
  }

  def crossLists(tfIdfList: Map[String, Double], textEvidenceList: Map[String, Double]) : Map[String, Double] = {
    val normalizedTfIdf = normalizeList(tfIdfList)
    val normalizedTextEvidence = normalizeList(textEvidenceList)

    val crossedList = for ((key, tfIdfValue) <- normalizedTfIdf) yield {
      val score = calculateScore(tfIdfValue, normalizedTextEvidence.get(key).getOrElse(0))
      key -> score
    }

    crossedList
  }

}
