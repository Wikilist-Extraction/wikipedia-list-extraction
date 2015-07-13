package implicits

import scala.concurrent._
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Created by nico on 12/07/15.
 */

object futureAdditions {
  implicit class FutureCompanionOps(val f: Future.type) extends AnyVal {

    /** Given a list of futures `fs`, returns the future holding the value of the future from `fs` that completed first.
      *  If the first completing future in `fs` fails, then the result is failed as well.
      *
      *  E.g.:
      *
      *      Future.any(List(Future { 1 }, Future { 2 }, Future { throw new Exception }))
      *
      *  may return a `Future` succeeded with `1`, `2` or failed with an `Exception`.
      */
    def any[T](fs: List[Future[T]]): Future[T] = {
      val p = Promise[T]()
      fs foreach (_ onComplete { p.tryComplete })
      p.future
    }

    /** Returns a future with a unit value that is completed after time `t`.
      */
    def delay(t: Duration): Future[Unit] = Future {
      Thread.sleep(t.toMillis)
    }

    def timeout[T](t: Duration)(block: => T): Future[T] = {
      val p = Promise[T]()
      val timeout = Future.delay(t)
      val f = Future(block)

      f onComplete p.tryComplete
      timeout onComplete( _ => p.tryFailure(new TimeoutException))

      p.future
    }
  }
}


