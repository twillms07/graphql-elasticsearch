package com.twillms.sangria.model

import akka.http.scaladsl.model.DateTime
import sangria.execution.deferred.HasId

case class User(id: Int, name: String, email:String, password:String, createdAt:DateTime = DateTime.now) extends Identifiable

object User {
//    implicit val hasId:HasId[User,Int] = HasId[User, Int](_.id)
}
