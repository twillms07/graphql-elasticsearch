package com.twillms.sangria.model

import akka.http.scaladsl.model.DateTime
import sangria.execution.deferred.HasId

case class Vote(id:Int, userId: Int, linkId : Int, createdAt:DateTime = DateTime.now) extends Identifiable

object Vote {
//    implicit val hasId:HasId[Vote,Int] = HasId[Vote, Int](_.id)
}
