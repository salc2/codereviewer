package ve.chucho.codereview

import java.io.File

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import org.webjars.WebJarAssetLocator

import scala.util.{Success, Try}

/**
  * Created by chucho on 9/21/16.
  */
object Router {
  private val locator = new WebJarAssetLocator

  def greeter: Flow[Message, Message, Any] =
    Flow[Message].mapConcat {
      case tm: TextMessage =>
        TextMessage(Source.single("Hello ") ++ tm.textStream ++ Source.single("!")) :: Nil
      case bm: BinaryMessage =>
        bm.dataStream.runWith(Sink.ignore)
        Nil
    }


  val route =
    path("hello") {
      get {
        complete{
          HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            index
          )
        }
      }
    } ~
      path("assets" / Remaining) { webJar =>
        Try(locator.getFullPath(webJar)) match {
          case Success(path) => getFromResource(path)
          case _ => complete(StatusCodes.NotFound)
        }
      } ~
      path("app" / "codereview.js"){
        getFromFile(new File("js/target/scala-2.11/foo-fastopt.js").getAbsolutePath)
      } ~
      path("ws") {
        handleWebSocketMessages(greeter)
      }
}
