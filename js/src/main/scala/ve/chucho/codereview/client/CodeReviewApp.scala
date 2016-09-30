package ve.chucho.codereview.client

/**
  * Created by chucho on 9/22/16.
  */

import akka.actor.{ActorSystem, Props}
import akka.stream._
import akka.stream.scaladsl._
import org.scalajs.dom._
import ve.chucho.codereview.client.model.{Dashboard, Diff, Model}
import ve.chucho.codereview.shared.Messages.{AppMessage, _}

import scala.scalajs.js.JSApp

object CodeReviewApp extends JSApp {

  implicit val system = ActorSystem("scala-js-system")
  implicit val dispatcher = system.dispatcher
  implicit val materializer = ActorMaterializer()

  def main(): Unit = {
    val wsSource = Source.
      actorPublisher[AppMessage](Props(classOf[WebSocketPublisher]))
      .scan[Model](Dashboard(Nil))( (model,msg) => msg match {
        case cmmt:HGCommit => model match {
          case Dashboard(commits) => Dashboard(cmmt +: commits)
          case _ => model
        }
        case diff:HGDiff => Diff(diff)
        case _ => Dashboard(Nil)
      })

    val flow = Flow[Model].to(Sink.foreach(m => {
      m match {
        case dbd:Dashboard => render(DashboardView(dbd))
        case df:Diff => render(DiffView(df))
        case _ => ()
      }
    })).runWith(wsSource)
  }

  def render(view:View):Unit = {
    document.getElementById("app").innerHTML = view.render
  }


}