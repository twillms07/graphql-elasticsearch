package com.twillms.sangria.model

import akka.http.scaladsl.model.DateTime
import spray.json._

object JsonFormats extends DefaultJsonProtocol {

//    implicit val dateTimeFormat : JsonFormat[DateTime] = jsonFormat1(DateTime.apply)

    implicit val linkJsonFormat :JsonFormat[Link] = jsonFormat3(Link.apply)


}
