package util

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
 * Created by nico on 05/07/15.
 */
object LoggingUtils {
  def time[T](str: String)(thunk: => T): T = {
    print(str + "... ")
    val t1 = System.currentTimeMillis
    val x = thunk
    val t2 = System.currentTimeMillis
    println((t2 - t1) + " msecs")
    x
  }

  def logFuture[T](text: String)(block: => Future[T])(implicit executionContext: ExecutionContext): Future[T] = {
    val f = block
    f.foreach(x => println(text))
    f
  }

  def logFutureResult[T](f: PartialFunction[Try[T], String])(block: => Future[T])(implicit executionContext: ExecutionContext): Future[T] = {
    block onComplete f.andThen(println(_))
    block
  }

  def timeFuture[T](text: String)(block: => Future[T])(implicit executionContext: ExecutionContext): Future[T] = {
    val t1 = System.currentTimeMillis
    val f = block
    f.foreach(x => {
      val t2 = System.currentTimeMillis
      val elapsedTime = t2 - t1
//      println(text.trim() + " " + elapsedTime + "ms")
    })
    f
  }
}
