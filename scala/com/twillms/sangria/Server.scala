package com.twillms.sangria

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.concurrent.Await
import scala.language.postfixOps


object Server extends App {

  implicit val actorSystem = ActorSystem("graphql-server")
  implicit val materializer = ActorMaterializer()
  val graphQLServer = new GraphQLServer

  import actorSystem.dispatcher
  import scala.concurrent.duration._

  scala.sys.addShutdownHook(() -> shutdown())

  val route: Route = {
    (post & path("graphql")) {
      entity(as[JsValue]) { requestJson =>
        graphQLServer.endpoint(requestJson)
      }
    } ~ {
      getFromResource("graphiql.html")
    }
  }

//  val route: Route = {
//    (post & path("graphql")) {
//      println(s"I'm getting to the graphql...")
//      entity(as[JsValue]) { requestJson =>
//        graphQLServer.endpoint(requestJson)
//      }
//    } ~ {
//      (get & path("getgraphql") & parameter('query)){ (query) =>
//        OK("This worked")
//      }
//    } ~ {
//      getFromResource("graphiql.html")
//    }
//  }


  Http().bindAndHandle(route, "0.0.0.0", 8080)
  println(s"open a browser with URL: http://localhost:8080")


  def shutdown(): Unit = {
    actorSystem.terminate()
    Await.result(actorSystem.whenTerminated, 30 seconds)
  }
}
