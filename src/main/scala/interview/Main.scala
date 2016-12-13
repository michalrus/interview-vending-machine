package interview

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.io.StdIn

import scala.concurrent.ExecutionContext

object Main {

  val Port = 8080
  val Host = "localhost"

  def main(args: Array[String]) {
    implicit val system: ActorSystem                = ActorSystem()
    implicit val materializer: ActorMaterializer    = ActorMaterializer()
    implicit val executionContext: ExecutionContext = system.dispatcher

    val bindingFuture =
      Http().bindAndHandle(Routing.route(new Persistence), Host, Port)

    system.log.info(s"Running at http://$Host:$Port/\nPress <RET> to stop…")
    val _ = StdIn.readLine()
    bindingFuture.flatMap(_.unbind()).onComplete(_ ⇒ system.terminate())
  }
}
