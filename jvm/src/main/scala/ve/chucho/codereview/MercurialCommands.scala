package ve.chucho.codereview

import ve.chucho.codereview.shared.Messages._

import sys.process._
/**
  * Created by chucho on 9/29/16.
  */
object MercurialCommands {
  val workingPath = "/home/chucho/workspace/ficha"
  def cmdCommitLogs = toHgCommit(Process(s"hg -R $workingPath log -b default -v").lineStream)
  def cmdDiffByRevision(changeSet: String) = Process(s"hg -R $workingPath diff -p -r $changeSet").lineStream

  private def toHgCommit(source:Stream[String]):Stream[AppMessage] = source.
    foldLeft(Stream.empty[List[String]])( (acc,line) => line match {
      case x if x.startsWith("changeset") => ( x :: Nil ) #:: acc
      case x => (x :: acc.head ) #:: acc.tail
    }).map(_ match {
    case changeset::parent1::parent2::user::date::tail if parent2.startsWith("parent") =>
      HGCommit(changeset,parent1::parent2::Nil,user,date,tail.mkString("\n"))
    case changeset::parent::user::date::tail =>
      HGCommit(changeset,parent::Nil,user,date,tail.mkString("\n"))
    case x => EmptyApp
  })
}
