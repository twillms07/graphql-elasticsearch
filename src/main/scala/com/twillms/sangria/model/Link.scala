package com.twillms.sangria.model

import akka.http.scaladsl.model.DateTime
import sangria.execution.deferred.HasId

case class Link(id: Int, url: String, description: String, dateTime: DateTime) extends Identifiable

object Link {
//    implicit val hasId:HasId[Link,Int] = HasId[Link, Int](_.id)
}