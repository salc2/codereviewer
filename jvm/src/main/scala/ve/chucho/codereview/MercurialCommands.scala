package ve.chucho.codereview

import ve.chucho.codereview.shared.Messages._

import scala.sys.process._

/**
  * Created by chucho on 9/29/16.
  */
object MercurialCommands {
  val workingPath = "/home/chucho/workspace/ficha"

  def cmdCommitLogs = toHgCommit(Process(s"hg -R $workingPath log -b default -v").lineStream.filter(!_.isEmpty))

  private def toHgCommit(source: Stream[String]): Stream[AppMessage] = source.
    foldLeft(Stream.empty[List[String]])((acc, line) => line match {
      case x if x.startsWith("changeset") =>
        (x :: Nil) #:: acc
      case x =>
        (acc.head.:+(x)) #:: acc.tail
    }).map { line => line.map(removePrefix) match {
    case changeset :: parent1 :: parent2 :: user :: date :: tail if parent2.startsWith("parent") =>
      HGCommit(changeset, parent1 :: parent2 :: Nil, user, date, tail.mkString("\n"))
    case changeset :: parent :: user :: date :: tail =>
      HGCommit(changeset, parent :: Nil, user, date, tail.mkString("\n"))
    case x => EmptyApp
  }

  }

  def cmdDiffByRevision(changeSet: String) = Process(s"hg -R $workingPath diff -p -r $changeSet").lineStream

  def removePrefix(prefix: String): String = prefix match {
    case x if x.startsWith("changeset:") => prefix.replace("changeset:", "")
    case x if x.startsWith("parent:") => prefix.replace("parent:", "")
    case x if x.startsWith("user:") => prefix.replace("user:", "")
    case x if x.startsWith("date:") => prefix.replace("date:", "")
    case x if x.startsWith("files:") => prefix.replace("files:", "")
    case x if x.startsWith("description:") => prefix.replace("description:", "")
    case _ => prefix
  }


}
