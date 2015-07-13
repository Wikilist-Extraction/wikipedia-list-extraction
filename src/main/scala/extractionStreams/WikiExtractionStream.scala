package extractionStreams

import akka.stream.Materializer
import akka.stream.scaladsl.RunnableGraph

/**
 * Created by nico on 13/07/15.
 */
abstract class WikiExtractionStream[T] {
  val stream: RunnableGraph[T]
  def run()(implicit materializer: Materializer): Unit = {
    stream.run()
  }
}
