package ve.chucho.codereview.server

import java.io.File
import java.time.{Instant, ZoneId}
import java.time.format.DateTimeFormatter
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import org.webjars.WebJarAssetLocator
import prickle.{Pickle, Unpickle}
import ve.chucho.codereview.shared.Messages.{AppMessage, HGCommit, InitApp}
import scala.collection.immutable.Iterable
import scala.io.StdIn
import scala.util.{Failure, Success, Try}
import ve.chucho.codereview.shared.Messages._
import scala.sys.process._
import java.util.Date

/**
  * Created by chucho on 9/22/16.
  */


package object server {
    implicit val system = ActorSystem("codereview-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher
}

object CodeReviewServer extends App{
    import server._
    val bindingFuture = Http().bindAndHandle(Router.route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
}



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
              getFromFile(new File("js/target/scala-2.11/foo-opt.js").getAbsolutePath)
          } ~
          path("ws") {
              handleWebSocketMessages(greeter)
          }


    def processMessage(msg: String): Iterable[TextMessage] =
        Unpickle[AppMessage]
          .fromString(msg) match {
            case Success(msg) =>
                msg.asInstanceOf[AppMessage] match {
                    case InitApp => MercurialCommands.cmdCommitLogs.map( (cm:AppMessage) => TextMessage(Pickle.intoString(cm))).toList
                    case RequestDiff(chageset) => List(TextMessage(Pickle.intoString(MercurialCommands
                      .cmdDiffByRevision(chageset))))
                    case _ => Nil
                }
            case Failure(e) => Nil
        }
}

object MercurialCommands {
    val workingPath = "/home/chucho/workspace/ficha"
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    def formattedDtm(unixTime:Long):String = Instant.ofEpochSecond(unixTime)
      .atZone(ZoneId.of("GMT-3"))
      .format(formatter)

    def cmdCommitLogs:Stream[HGCommit] = Process(Seq("hg", "--cwd", s"$workingPath","log", "-b", "default", "--template", "{node} ;; {desc|urlescape} ;; {author|person} ;; {date|rfc822date}\n\n"))
      .lineStream.filter(!_.isEmpty).map( l => l.split(";;").toList).map{
        case node :: desc :: author :: date :: Nil => Some(HGCommit(node,java.net.URLDecoder.decode(desc, "UTF-8"),author,date))
        case x =>println(x); None
    }.filter(_.isDefined).flatten

    def cmdDiffByRevision(changeSet: String):AppMessage = HGDiff(changeSet,Process(Seq("hg", "--cwd", s"$workingPath","diff","-c",changeSet)).lineStream.mkString("\n"))

    def parseDate(stringDate:String):Date = {
        val timestamp = stringDate.toDouble
        new Date(timestamp.toLong*1000)
    }

}

