package implicits

import org.scalatest.FlatSpec
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent._
import scala.concurrent.duration._

import implicits.futureAdditions.FutureCompanionOps

import scala.language.postfixOps

class FutureAdditionsSpec extends FlatSpec with ScalaFutures {

  "A Future.delay" should "finish after given delay" in {
    val f = Future.delay(1 second)
    Await.result(f, 2 seconds)
  }

  "A Future.timeout" should "fail after a given duration" in {
    val f = Future.timeout(1 second)(Thread.sleep(3000))
    intercept[TimeoutException] {
      Await.result(f, 2 seconds)
    }
  }

  it should "return the correct result if it finished before the timeout" in {

    def slowComputation(): Int = {
      Thread.sleep(1000)
      5
    }

    val f = Future.timeout(1.5 second)(slowComputation())
    val r = Await.result(f, 2 seconds)
    assert(r == 5)
  }
}
