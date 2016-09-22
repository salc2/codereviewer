package ve.chucho.codereview

/**
  * Created by chucho on 9/22/16.
  */
import scala.scalajs.js.{JSApp, JSON}
import org.scalajs.dom._
import org.scalajs.dom.raw.MessageEvent

object CodeReviewApp extends JSApp {
  def main(): Unit = {
    println("Hello world!")

   val ws = new WebSocket("ws://localhost:8080/ws")
   ws.onmessage = (msg:MessageEvent) => println(msg.data)
    ws.onopen = (e:Event) => {
      ws.send("holaaa si")
    }

  }
}