package com.twillms.sangria

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.howtographql.scala.sangria.elasticsearch.{ElasticsearchStream, ElasticsearchStreamContext}
import com.howtographql.scala.sangria.graphql.GraphQLSchema
import com.howtographql.scala.sangria.model.Link
import sangria.ast.Document
import sangria.execution._
import sangria.execution.deferred.HasId
import sangria.marshalling.sprayJson._
import sangria.parser.QueryParser
import spray.json.{JsObject, JsString, JsValue}

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class GraphQLServer(implicit val actorSystem: ActorSystem, actorMaterializer: ActorMaterializer) {

//    private val dao: DAO = DBSchema.createDatabase


    implicit val linkHasId:HasId[Link,Int] = HasId[Link, Int](_.id)

    private val elasticsearchStream:ElasticsearchStream = ElasticsearchStream.createElasticStream


    def endpoint(requestJSON: JsValue)(implicit ec: ExecutionContext): Route = {

        val JsObject(fields) = requestJSON
        val JsString(query)= fields("query")

        println(s"The query is $query")

        QueryParser.parse(query) match {
            case Success(queryAst) =>
                val operation : Option[String] = fields.get("operationName") collect {
                    case JsString(op) => op
                }

                val variables :JsObject = fields.get("variables") match {
                    case Some(obj: JsObject) => obj
                    case _ => JsObject.empty
                }

                complete(executeGraphQLQuery(queryAst, operation, variables))

            case Failure(error) =>
                complete(BadRequest, JsObject("error" -> JsString(error.getMessage)))
        }

    }

    private def executeGraphQLQuery(query:Document, operation: Option[String], vars: JsObject) = {
        Executor.execute(
            GraphQLSchema.SchemaDefinition,
            query,
            ElasticsearchStreamContext(elasticsearchStream),
            variables = vars,
            operationName = operation
        ).map(OK -> _)
            .recover {
                case error: QueryAnalysisError => BadRequest -> error.resolveError
                case error: ErrorWithResolver => InternalServerError -> error.resolveError
            }
    }


}


