package ve.chucho

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

/**
  * Created by chucho on 9/22/16.
  */
package object codereview {
  val index = View.index
  implicit val system = ActorSystem("codereview-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
}
