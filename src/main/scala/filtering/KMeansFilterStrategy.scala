package filtering

import dataFormats.{WikiFusedResult, WikiListScores}
import scala.util.Random

import scala.annotation.tailrec

/**
 * Created by nico on 28/08/15.
 */
class KMeansFilter extends ScoreFilterStrategy {
  def getTypesWithKMeans(result: WikiFusedResult): List[String] = {
    List()
  }

  override def filterScores(result: WikiFusedResult): List[String] = {
    if (allTypesCorrect(result))
      result.wikiListScores.getTypes
    else
      getTypesWithKMeans(result)
  }

}

trait VectorSpace[A] {
  def distance(x: A, y: A): Double

  def centroid(ps: Seq[A]): A
}

class DoubleVectorSpace extends VectorSpace[Double] {

  override def distance(x: Double, y: Double): Double = scala.math.abs(x - y)

  override def centroid(ps: Seq[Double]): Double = ps.sum / ps.size.toDouble
}

object KMeans {

  def pickRandom[T](xs: Seq[T], k: Int) = Random.shuffle(xs).take(k)

  def cluster[T,U](xs: Seq[T], k: Int)
                  (implicit projection: T => U, space: VectorSpace[U]): Seq[Seq[T]] = {
    case class Pair(original: T) {
      val projected = projection(original)
    }
    @tailrec
    def step(xs: Seq[Pair], centroids: Seq[U]): Seq[Seq[Pair]] = {
      val labeled =
        for (x <- xs) yield {
          val distances = for ((centroid) <- centroids) yield (centroid, space.distance(x.projected, centroid))
          val nearestCentroid = distances.minBy(_._2)._1
          (nearestCentroid, x)
        }
      val grouped = for (centroid <- centroids) yield labeled.collect({
        case (`centroid`, x) => x
      })
      val replacements = grouped.map(group => space.centroid(group.map(_.projected)))
      val stable =
        replacements.forall {
          replacement =>
            centroids.contains(replacement)
        }
      if (stable) {
        grouped
      } else {
        step(xs, replacements)
      }
    }
    val associated = xs.map(Pair)
    val initial = pickRandom(associated.map(_.projected), k)
    step(associated, initial).map(_.map(_.original))
  }

  def cluster[T,U](fn: T => U)(xs: Seq[T], k: Int)
                  (implicit g: VectorSpace[U]): Seq[Seq[T]] = cluster(xs, k)(fn, g)

}
