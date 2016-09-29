package ve.chucho.codereview.server

import java.io.File

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.model.{StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.{Flow}
import org.webjars.WebJarAssetLocator
import prickle._
import ve.chucho.codereview.MercurialCommands._
import ve.chucho.codereview.shared.Messages._
import scala.util.{Failure, Success, Try}
import scala.collection.immutable.Iterable

/**
  * Created by chucho on 9/21/16.
  */
object Router {
  private val locator = new WebJarAssetLocator

  def greeter: Flow[Message, Message, Any] =
    Flow[Message].mapConcat {
      case tm: TextMessage => processMessage(tm.getStrictText)
      case _ => Nil
    }

  val route =
    pathSingleSlash {
      get {
        getFromResource("index.html")
      }
    } ~
      path("assets" / Remaining) { webJar =>
        Try(locator.getFullPath(webJar)) match {
          case Success(path) => getFromResource(path)
          case _ => complete(ToResponseMarshallable(StatusCodes.NotFound))
        }
      } ~
      path("app" / "codereview.js") {
        getFromFile(new File("js/target/scala-2.11/foo-fastopt.js").getAbsolutePath)
      } ~
      path("ws") {
        handleWebSocketMessages(greeter)
      }


  def processMessage(msg: String): Iterable[TextMessage] =
    Unpickle[AppMessage]
      .fromString(msg) match {
      case Success(msg) =>
        msg.asInstanceOf[AppMessage] match {
          case InitApp => cmdCommitLogs.map(cm => TextMessage(Pickle.intoString(cm))).toList
          case _ => Nil
        }
      case Failure(e) => Nil
    }


}

