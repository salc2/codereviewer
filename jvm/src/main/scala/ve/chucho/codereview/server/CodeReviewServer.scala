package ve.chucho.codereview.server

import akka.http.scaladsl.Http
import ve.chucho.codereview._

import scala.io.StdIn

/**
  * Created by chucho on 9/22/16.
  */
object CodeReviewServer extends App{

    val bindingFuture = Http().bindAndHandle(Router.route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())

}
