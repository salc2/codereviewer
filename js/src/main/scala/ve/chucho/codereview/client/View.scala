package ve.chucho.codereview.client

import ve.chucho.codereview.client.model.{Dashboard, Diff}

import scalatags.Text.all._
/**
  * Created by chucho on 9/29/16.
  */
trait View{
  def render:String
}
case class DashboardView(model:Dashboard) extends View {
  override def render: String = {
    ul(
      for(c <- model.commits) yield li(span(c.node),span(c.author))
    ).toString()
  }
}

case class DiffView(model:Diff) extends View{
  override def render: String = {
    div(h3(model.diff.chageset),p(model.diff.diffText)).toString()
  }
}