package ve.chucho.codereview.client.model

import ve.chucho.codereview.shared.Messages.{HGCommit, HGDiff}

/**
  * Created by chucho on 9/29/16.
  */
sealed trait Model
case class Dashboard(commits:List[HGCommit]) extends Model
case class Diff(diff:HGDiff) extends Model