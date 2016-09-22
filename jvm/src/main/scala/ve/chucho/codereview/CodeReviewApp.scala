package ve.chucho.codereview

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.io.StdIn

/**
  * Created by chucho on 9/22/16.
  */
object CodeReviewApp extends App{
  implicit val system = ActorSystem("codereview-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

    val bindingFuture = Http().bindAndHandle(Router.route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())

}
