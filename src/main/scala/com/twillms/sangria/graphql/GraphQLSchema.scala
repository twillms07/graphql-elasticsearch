package com.twillms.sangria.graphql

import akka.http.scaladsl.model.DateTime
import com.twillms.sangria.elasticsearch.ElasticsearchStreamContext
import com.twillms.sangria.model._
import sangria.ast.StringValue
import sangria.execution.deferred.{DeferredResolver, Fetcher}
import sangria.schema._

object GraphQLSchema {


    val linksFetcher = Fetcher((ctx: ElasticsearchStreamContext, ids: Seq[Int]) =>
        ctx.elasticsearchStream.getLinks(ids.toList))

    val userFetcher = Fetcher(
        (ctx: ElasticsearchStreamContext, ids: Seq[Int]) => ctx.elasticsearchStream.getUsers(ids.toList)
    )

    val voteFetcher = Fetcher(
        (ctx: ElasticsearchStreamContext, ids: Seq[Int]) => ctx.elasticsearchStream.getVotes(ids.toList)
    )

    val Resolver: DeferredResolver[ElasticsearchStreamContext] = DeferredResolver.fetchers(linksFetcher, userFetcher, voteFetcher)

    val IdentifiableType: InterfaceType[Unit, Identifiable] = InterfaceType[Unit, Identifiable](
        "Identifiable",
        fields[Unit, Identifiable](
            Field("id", IntType, resolve = _.value.id)
        )
    )

    implicit val GraphQLDateTime: ScalarType[DateTime] = ScalarType[DateTime](
        "DateTime",
        coerceOutput = (dateTime, _) => dateTime.toString,
        coerceInput = {
            case StringValue(dateTime, _, _) => DateTime.fromIsoDateTimeString(dateTime).toRight(DateTimeCoerceViolation)
            case _ => Left(DateTimeCoerceViolation)
        },
        coerceUserInput = {
            case s: String => DateTime.fromIsoDateTimeString(s).toRight(DateTimeCoerceViolation)
            case _ => Left(DateTimeCoerceViolation)
        }
    )

    val LinkType: ObjectType[Unit, Link] = ObjectType[Unit, Link](
        "Link",
        interfaces[Unit, Link](IdentifiableType),
        fields[Unit, Link](
            Field("id", IntType, resolve = _.value.id),
            Field("url", StringType, resolve = _.value.url),
            Field("description", StringType, resolve = _.value.description),
            Field("dateTime", GraphQLDateTime, resolve = _.value.dateTime)
        )
    )

    val UserType: ObjectType[Unit, User] = ObjectType[Unit, User](
        "User",
        interfaces[Unit, User](IdentifiableType),
        fields[Unit, User](
            Field("id", IntType, resolve = _.value.id),
            Field("name", StringType, resolve = _.value.name),
            Field("email", StringType, resolve = _.value.email),
            Field("password", StringType, resolve = _.value.password),
            Field("createdAt", GraphQLDateTime, resolve = _.value.createdAt)
        )
    )

    val VoteType: ObjectType[Unit, Vote] = ObjectType[Unit, Vote](
        "Vote",
        interfaces[Unit, Vote](IdentifiableType),
        fields[Unit, Vote](
            Field("id", IntType, resolve = _.value.id),
            Field("url", IntType, resolve = _.value.userId),
            Field("description", IntType, resolve = _.value.linkId),
            Field("createdAt", GraphQLDateTime, resolve = _.value.createdAt)
        )
    )


    val Id = Argument("id", IntType)

    val Ids = Argument("ids", ListInputType(IntType))

    val QueryType = ObjectType(
        "Query",
        fields[ElasticsearchStreamContext, Unit](
            Field("link",
                OptionType(LinkType),
                arguments = Id :: Nil,
                resolve = c => linksFetcher.deferOpt(c.arg(Id))
            ),
            Field("links",
                ListType(LinkType),
                arguments = Ids :: Nil,
                resolve = c => linksFetcher.deferSeq(c.arg(Ids))
            ),
            Field("user",
                OptionType(UserType),
                arguments = Id :: Nil,
                resolve = c => userFetcher.deferOpt(c.arg(Id))
            ),
            Field("users",
                ListType(UserType),
                arguments = List(Argument("ids", ListInputType(IntType))),
                resolve = c => userFetcher.deferSeq(c.arg(Ids))
            ),
            Field("vote",
                OptionType(VoteType),
                arguments = Id :: Nil,
                resolve = c => voteFetcher.deferOpt(c.arg(Id))
            ),
            Field("votes",
                ListType(VoteType),
                arguments = Ids :: Nil,
                resolve = c => voteFetcher.deferSeq(c.arg(Ids))
            )
        )

    )


    val SchemaDefinition = Schema(QueryType)


}

//    val QueryType = ObjectType(
//        "Query",
//        fields[ElasticsearchStreamContext, Unit](
//            Field("link",
//                OptionType(LinkType),
//                arguments = Id :: Nil,
//                resolve = c => linksFetcher.deferOpt(c.arg[Int]("id"))
//            ),
//            Field("links",
//                ListType(LinkType),
//                arguments = List(Argument("ids", ListInputType(IntType))),
//                resolve = c => linksFetcher.deferSeq(c.arg[Seq[Int]]("ids"))
//            ),
//            Field("user",
//                OptionType(UserType),
//                arguments = Id :: Nil,
//                resolve = c => userFetcher.deferOpt(c.arg[Int]("id"))
//            ),
//            Field("users",
//                ListType(UserType),
//                arguments = List(Argument("ids", ListInputType(IntType))),
//                resolve = c => userFetcher.deferSeq(c.arg[Seq[Int]]("ids"))
//            ),
//            Field("vote",
//                OptionType(VoteType),
//                arguments = Id :: Nil,
//                resolve = c => voteFetcher.deferOpt(c.arg[Int]("id"))
//            ),
//            Field("votes",
//                ListType(VoteType),
//                arguments = List(Argument("ids", ListInputType(IntType))),
//                resolve = c => voteFetcher.deferSeq(c.arg[Seq[Int]]("ids"))
//            )
//
//        )
//
//    )

//    val linksFetcher = Fetcher(
//        (ctx: ElasticsearchStreamContext, ids: Seq[Int]) => ctx.elasticsearchStream.getLinks(ids.toList)
//    )
//
//    val userFetcher = Fetcher(
//        (ctx: ElasticsearchStreamContext, ids: Seq[Int]) => ctx.elasticsearchStream.getUsers(ids.toList)
//    )
//
//    val voteFetcher = Fetcher(
//        (ctx: ElasticsearchStreamContext, ids: Seq[Int]) => ctx.elasticsearchStream.getVotes(ids.toList)
//    )
//
//    val Resolver:DeferredResolver[ElasticsearchStreamContext] = DeferredResolver.fetchers(linksFetcher)
//
//    implicit val GraphQLDateTime : ScalarType[DateTime] = ScalarType[DateTime](
//        "DateTime",
//        coerceOutput = (dateTime,_) => dateTime.toString,
//        coerceInput = {
//            case StringValue(dateTime,_,_) => DateTime.fromIsoDateTimeString(dateTime).toRight(DateTimeCoerceViolation)
//            case _ => Left(DateTimeCoerceViolation)
//        },
//        coerceUserInput = {
//            case s:String => DateTime.fromIsoDateTimeString(s).toRight(DateTimeCoerceViolation)
//            case _ => Left(DateTimeCoerceViolation)
//        }
//    )
//
//    val IdentifiableType : InterfaceType [Unit, Identifiable] = InterfaceType[Unit,Identifiable](
//        "Identifiable",
//        fields[Unit,Identifiable](
//            Field("id", IntType, resolve = _.value.id)
//        )
//    )
//

//    val UserType :ObjectType[Unit, User] = ObjectType[Unit, User](
//        "User",
////        interfaces[Unit,User](IdentifiableType),
//        fields[Unit, User](
//            Field("id", IntType, resolve = _.value.id),
//            Field("name", StringType, resolve = _.value.name),
//            Field("email", StringType, resolve = _.value.email),
//            Field("password", StringType, resolve = _.value.password),
//            Field("createdAt", GraphQLDateTime, resolve = _.value.createdAt)
//        )
//    )
//
//    val VoteType :ObjectType[Unit, Vote] = ObjectType[Unit, Vote](
//        "Vote",
////        interfaces[Unit,Vote](IdentifiableType),
//        fields[Unit, Vote](
//            Field("id", IntType, resolve = _.value.id),
//            Field("url", IntType, resolve = _.value.userId),
//            Field("description", IntType, resolve = _.value.linkId),
//            Field("createdAt", GraphQLDateTime, resolve = _.value.createdAt)
//        )
//    )
