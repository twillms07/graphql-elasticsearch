name := "graphql-elasticsearch"

version := "1.0"

description := "GraphQL server with akka-http and alpakka elastic search."

scalaVersion := "2.12.3"

scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies ++= Seq(
  "com.lightbend.akka" %% "akka-stream-alpakka-elasticsearch" % "0.19",
  "org.sangria-graphql" %% "sangria" % "1.3.0",
  "org.sangria-graphql" %% "sangria-spray-json" % "1.0.0",
  "com.typesafe.akka" %% "akka-http" % "10.0.10",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.10",
  "org.scalatest" %% "scalatest" % "3.0.4" % Test
)

Revolver.settings
