package ve.chucho.codereview.client

/**
  * Created by chucho on 9/22/16.
  */

import akka.actor.{ActorSystem, Props}
import akka.stream._
import akka.stream.scaladsl._
import org.scalajs.dom._
import ve.chucho.codereview.shared.Messages.{AppMessage, _}

import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSName
import scala.util.{Failure, Success, Try}
import scalatags.Text.all._


case class AppState(model:Model)

trait View{
  def render:String
}
case class DashboardView(model:Dashboard) extends View {
  override def render: String = {
    ul(`class`:="mdl-list",
      for(c <- model.commits) yield li(`class`:="mdl-list__item mdl-list__item--three-line",
        span(`class`:="mdl-list__item-primary-content",
          span(
            s"${c.author} at ${c.date}",
            a(`class`:="mdl-list__item-text-body",href :=s"#${c.node}",c.desc)
          )
        ),
        span(`class`:="mdl-list__item-secondary-content",
        a(`class`:="mdl-list__item-secondary-action",href :=s"#${c.node}",
          c.node.substring(0,8)
        )
        )
    )
    ).toString
  }
}

case class DiffView(model:Diff) extends View{
  override def render: String =
    /*div(h3(model.diff.chageset)
    ).toString*/
    Diff2Html.getPrettyHtml(model.diff.diffText,
      new js.Object{
        val inputFormat = "diff"
        val outputFormat = "line-by-line"
        val showFiles = true
        val matching = "lines"
      })
}

sealed trait Model
case class Dashboard(commits:List[HGCommit]) extends Model
case class Diff(diff:HGDiff) extends Model


object CodeReviewApp extends JSApp {
  implicit val system = ActorSystem("scala-js-system")
  implicit val dispatcher = system.dispatcher
  implicit val materializer = ActorMaterializer()

  def main(): Unit = {
    val wsSource = Source.
      actorPublisher[AppMessage](Props(classOf[WebSocketPublisher]))
      .scan[Model](Dashboard(Nil))( (model,msg) => msg match {
        case cmmt:HGCommit => model match {
          case Dashboard(commits) => Dashboard(commits :+ cmmt  )
          case _ => model
        }
        case diff:HGDiff => Diff(diff)
        case _ => Dashboard(Nil)
      })

    val flow = Flow[Model].to(Sink.foreach {
      case dbd: Dashboard => render(DashboardView(dbd))
      case df: Diff => render(DiffView(df))
      case _ => ()
    }).runWith(wsSource)

    window.onhashchange = (hashEvent:HashChangeEvent) =>
      Try(hashEvent.newURL.split("#")(1)) match {
        case Success(path) => flow ! RequestDiff(path)
        case Failure(ex) => ()
      }

  }

  def render(view:View):Unit = {
    document.getElementById("app").innerHTML = view.render
  }
}

@js.native
object Diff2Html extends js.Object{
  def getPrettyHtml(input: String, configuration: js.Object): String = js.native
}