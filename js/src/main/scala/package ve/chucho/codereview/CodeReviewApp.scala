package ve.chucho.codereview

/**
  * Created by chucho on 9/22/16.
  */
import scala.scalajs.js.{JSApp, JSON}
import org.scalajs.dom._
import org.scalajs.dom.raw.MessageEvent

import akka.actor.{ActorSystem, ActorRef}
import akka.stream._
import akka.stream.scaladsl._
import akka.util.Timeout

import scala.util.Try
import scala.concurrent.Future
import scala.concurrent.duration._





object CodeReviewApp extends JSApp {

  implicit val system = ActorSystem("scala-js-system")
  implicit val dispatcher = system.dispatcher
  implicit val materializer = ActorMaterializer()



  def main(): Unit = {
    println("Hello world!")

   val ws = new WebSocket("ws://localhost:8080/ws")
   ws.onmessage = (msg:MessageEvent) => println(msg.data)
    ws.onopen = (e:Event) => {
      Source.tick(1 millis, 10 nano, 1).scan(0)( (n,m) => n+m ).map(n => s"hi$n").runWith(Sink.foreach(m =>
        ws.send(m)
      ))
    }


  }
}