package ve.chucho.codereview

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import org.webjars.WebJarAssetLocator

import scala.util.{Success, Try}
import scalatags.Text._
import scalatags.Text.all._

/**
  * Created by chucho on 9/21/16.
  */
object Router {

  private val locator = new WebJarAssetLocator

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
      }
}
