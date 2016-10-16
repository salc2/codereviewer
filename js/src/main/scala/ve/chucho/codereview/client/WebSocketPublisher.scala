package ve.chucho.codereview.client

import akka.actor.ActorLogging
import akka.stream.actor.ActorPublisher
import akka.stream.actor.ActorPublisherMessage.{Cancel, Request}
import org.scalajs.dom.CloseEvent
import org.scalajs.dom.raw.{Event, MessageEvent, WebSocket}
import prickle._

import scala.annotation.tailrec
import ve.chucho.codereview.shared.Messages._

import scala.util.{Failure, Success}

/**
  * Created by chucho on 9/27/16.
  */

class WebSocketPublisher extends ActorPublisher[AppMessage] with ActorLogging{

  val ws = new WebSocket("ws://localhost:8080/ws")

  ws.onopen = (_:Event) => {
    self ! InitApp
  }

  ws.onmessage = (msg:MessageEvent) => {
    Unpickle[AppMessage]
      .fromString(msg.data.toString) match {
      case Success(m) =>{
        val hgms = m.asInstanceOf[AppMessage]

        if (buf.isEmpty && totalDemand > 0)
          onNext( hgms )
        else {
          buf :+= hgms
          deliverBuf()
        }
      }
      case Failure(e) => println(e)
    }
  }

  val MaxBufferSize = 100
  var buf = Vector.empty[AppMessage]

  override def receive: Receive = {
    case Request(_) => deliverBuf()
    case Cancel => context.stop(self)
    case m:AppMessage => ws.send(Pickle.intoString(m))
  }


  @tailrec final def deliverBuf(): Unit =
    if (totalDemand > 0) {
      if (totalDemand <= Int.MaxValue) {
        val (use, keep) = buf.splitAt(totalDemand.toInt)
        buf = keep
        use foreach onNext
      } else {
        val (use, keep) = buf.splitAt(Int.MaxValue)
        buf = keep
        use foreach onNext
        deliverBuf()
      }
    }


}
