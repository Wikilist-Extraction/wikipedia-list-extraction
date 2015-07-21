package runnables

import akka.actor.PoisonPill
import akka.actor.{ ActorSystem, Actor, Props, ActorLogging, ActorRef, ActorRefFactory }
import akka.io.IO
import m.GraphInstance
import spray.can.server.UHttp
import spray.routing.{ExceptionHandler, HttpService}
import play.api.libs.json._
import m.websocket.WebSocketServer
import m.websocket.WebsocketIO
import spray.can.Http
import streams.ListExtraction

object Visualization extends App {
  implicit val system = ActorSystem("reactive-viz")
  import system.dispatcher

  // To run a different demoable flow, substitute this line:
  val demoableFlow = ListExtraction
  // val demoableFlow = m.graphs.Shipping

  def allocateGraphInstance(io: WebsocketIO): Unit = {
    println("very allocate")
    val graph = system.actorOf(Props(new GraphInstance(
      io,
      demoableFlow
    )))
    io.terminateOnClose(graph)
  }

  val server = system.actorOf(
    Props(new WebSocketServer(allocateGraphInstance)),
    "websocket")

  IO(UHttp) ! Http.Bind(server, "localhost", 8080)
}
