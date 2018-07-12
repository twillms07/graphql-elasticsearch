package com.twillms.sangria.model

import akka.http.scaladsl.model.DateTime
import spray.json._

object JsonFormats extends DefaultJsonProtocol {

    implicit val dateTimeFormat : JsonFormat[DateTime] = jsonFormat6(DateTime.apply)

    implicit val linkJsonFormat :JsonFormat[Link] = jsonFormat4(Link.apply)


}
