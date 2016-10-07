package ve.chucho.codereview

import ve.chucho.codereview.shared.Messages._
import scala.sys.process._
import java.time._
import java.time.format._

/**
  * Created by chucho on 9/29/16.
  */
object MercurialCommands {
  val workingPath = "/home/chucho/workspace/ficha"
  val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  def formattedDtm(unixTime:Long):String = Instant.ofEpochSecond(unixTime)
        .atZone(ZoneId.of("GMT-3"))
        .format(formatter);

  def cmdCommitLogs:Stream[HGCommit] = Process(Seq("hg", "--cwd", s"$workingPath","log", "-b", "default", "--template", "{node} ;; {desc} ;; {author} ;; {date}\n"))
    .lineStream.filter(!_.isEmpty).map( l => l.split(";;").toList).map{
    //{node} || {desc} || {tags} || {author} || {date}
    case node :: desc :: author :: date :: Nil => println(s"$node $desc $author $date");Some(HGCommit(node,desc,author,date))
    case x => println(x);None
  }.filter(_.isDefined).flatten

  def cmdDiffByRevision(changeSet: String) = Process(s"hg -R $workingPath diff -p -r $changeSet").lineStream

}
