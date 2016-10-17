package ve.chucho.codereview.shared

import java.util.Date

import prickle.{CompositePickler, PicklerPair}

/**
  * Created by chucho on 9/28/16.
  */
object Messages {
  implicit val hgMessagesPickler: PicklerPair[AppMessage] = CompositePickler[AppMessage].
    concreteType[HGCommit].concreteType[HGLog].concreteType[HGPull.type].concreteType[HGDiff]
    .concreteType[InitApp.type].concreteType[RequestDiff].concreteType[EmptyApp.type]
  sealed trait AppMessage
  case class HGCommit(
                       node: String,
                       desc: String,
                       author: String,
                       date: String
                     ) extends AppMessage
  case class HGLog(limit: Int) extends AppMessage
  case class HGDiff(chageset:String,diffText:String) extends AppMessage
  case class RequestDiff(chageset:String) extends AppMessage
  case object HGPull extends AppMessage
  case object InitApp extends AppMessage
  case object EmptyApp extends AppMessage

}
