package util

import com.typesafe.scalalogging.slf4j.{StrictLogging, Logger, LazyLogging}
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
 * Created by nico on 05/07/15.
 */
object LoggingUtils extends StrictLogging {


  def time[T](str: String)(thunk: => T): T = {
    val t1 = System.currentTimeMillis
    val x = thunk
    val t2 = System.currentTimeMillis
    val elapsedTime = t2 - t1
    logger.info(str.trim() + " " + elapsedTime + " ms")
    x
  }

  def logFuture[T](text: String)(block: => Future[T])(implicit executionContext: ExecutionContext): Future[T] = {
    val f = block
    f.foreach(x => logger.info(text))
    f
  }

  def logFutureResult[T](f: PartialFunction[Try[T], String])(block: => Future[T])(implicit executionContext: ExecutionContext): Future[T] = {
    block onComplete f.andThen(x => logger.info(x))
    block
  }

  def timeFuture[T](text: String)(block: => Future[T])(implicit executionContext: ExecutionContext): Future[T] = {
    val t1 = System.currentTimeMillis
    val f = block
    f.foreach(x => {
      val t2 = System.currentTimeMillis
      val elapsedTime = t2 - t1
      logger.info(text.trim() + " " + elapsedTime + "ms")
    })
    f
  }
}
