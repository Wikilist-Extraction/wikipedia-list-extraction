package filtering.strategies

import dataFormats.WikiFusedResult

import scala.util.Random

/**
 * Created by nico on 28/08/15.
 */
class KMeansFilter extends ScoreFilterStrategy {
  import KMeans._

  def getTypesWithKMeans(result: WikiFusedResult): List[String] = {

    implicit def g = new VectorSpace[(Double, Double)] {

      override def distance(x: (Double, Double), y: (Double, Double)): (Double, Double) = (scala.math.abs(x._1 - y._1), 0d)

      override def centroid(ps: Seq[(Double, Double)]): (Double, Double) = (ps.map(_._1).sum / ps.size.toDouble, 0d)
    }

    def assToPoint(ass: (String, Double)): (Double, Double) = (ass._2, 0d)

    val clusters = cluster[(String, Double), (Double, Double)]((ass: (String, Double)) => (ass._2, 0d))(result.types.toList, 2)(g)
    clusters.maxBy(_.toMap.values.max).toMap.keys.toList
  }

  override def filterScores(result: WikiFusedResult): List[String] = {
    if (allTypesCorrect(result))
      result.wikiListScores.getTypes
    else
      getTypesWithKMeans(result)
  }

}

trait VectorSpace[A] {
  def distance(x: A, y: A): A

  def centroid(ps: Seq[A]): A
}

object KMeans {

  def pickRandom[T](xs: Seq[T], k: Int) = Random.shuffle(xs).take(k)

  def cluster[T,U](xs: Seq[T], k: Int)
                  (implicit projection: T => U, space: VectorSpace[U]): Seq[Seq[T]] = {
//    case class Pair(original: T) {
//      val projected = projection(original)
//    }
//    @tailrec
//    def step(xs: Seq[Pair], centroids: Seq[U]): Seq[Seq[Pair]] = {
//      val labeled =
//        for (x <- xs) yield {
//          val distances = for ((centroid) <- centroids) yield (centroid, space.distance(x.projected, centroid))
//          val nearestCentroid = distances.minBy(_._2)._1
//          (nearestCentroid, x)
//        }
//      val grouped = for (centroid <- centroids) yield labeled.collect({
//        case (`centroid`, x) => x
//      })
//      val replacements = grouped.map(group => space.centroid(group.map(_.projected)))
//      val stable =
//        replacements.forall {
//          replacement =>
//            centroids.contains(replacement)
//        }
//      if (stable) {
//        grouped
//      } else {
//        step(xs, replacements)
//      }
//    }
//    val associated = xs.map(Pair)
//    val initial = pickRandom(associated.map(_.projected), k)
//    step(associated, initial).map(_.map(_.original))
    List()
  }

  def cluster[T,U](fn: T => U)(xs: Seq[T], k: Int)
                  (implicit g: VectorSpace[U]): Seq[Seq[T]] = cluster(xs, k)(fn, g)

}
