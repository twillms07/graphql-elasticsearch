package com.twillms.elasticsearch

import akka.actor.ActorSystem
import akka.http.scaladsl.model.DateTime
import akka.stream.Materializer
import akka.stream.alpakka.elasticsearch.scaladsl.{ElasticsearchSource, ElasticsearchSourceSettings}
import akka.stream.scaladsl.Sink
import com.howtographql.scala.sangria.model.{Link, User, Vote}
import com.howtographql.scala.sangria.model.Link._
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import com.howtographql.scala.sangria.model.JsonFormats._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ElasticsearchStream(implicit val actorSystem: ActorSystem, material: Materializer) {

    implicit val client: RestClient = RestClient.builder(new HttpHost("localhost", 9200)).build()

    val elasticsearchSourceSettings = ElasticsearchSourceSettings(bufferSize = 5)

    def getLinks(ids: Seq[Int]) : Future[Seq[Link]] = {

//        val listIds:String =  ids.mkString(",")
//        println(s"Here's the Ids - $ids with $listIds")
//
//        val linkResults:Future[Seq[Link]] = ElasticsearchSource
//            .typed[Link](indexName = "mylinks",
//            Some("link"),
//            searchParams = Map(
//                "query" -> s"""{"terms":{"_id":[$listIds]}}""".stripMargin,
//                "_source" -> """ ["id", "url", "description", "dateTime"] """
//            ),
//            elasticsearchSourceSettings)
//            .map { message =>
//                message.source
//            }
//            .runWith(Sink.seq)
//
//        linkResults.foreach(r => println(s"The link results are = $r"))
//        linkResults

        val links = Seq(Link(5,"my.url.com","test url"))
        println(s"Calling getLinks - ${DateTime.now} with - $links")
        Future(links)
    }

    def getUsers(ids: List[Int]) : Future[Seq[User]] = {
        val users = Seq(User(3,"Bill","bill@test.com","mys3cr3t",DateTime.now))
        println(s"Calling getUsers - ${DateTime.now} with $users")
        Future(users)
    }

    def getVotes(ids: List[Int]) : Future[Seq[Vote]] = {
        println(s"Calling getVotes - ${DateTime.now}")
        val votes = Seq(Vote(12,3,5,DateTime.now))
        println(s"Calling getVotes - $votes")
        Future(votes)
    }

}

object ElasticsearchStream {

    def createElasticStream(implicit actorSystem: ActorSystem, material: Materializer):ElasticsearchStream = {
        new ElasticsearchStream()
    }
}