name := "kafka"

version := "0.1"

scalaVersion := "2.13.7"

val kafkaVersion = "3.0.0"

libraryDependencies += "org.apache.kafka" % "kafka-clients" % kafkaVersion
libraryDependencies += "org.apache.kafka" %% "kafka-streams-scala" % kafkaVersion
libraryDependencies += "org.apache.kafka" % "kafka-streams-test-utils" % kafkaVersion % Test

val circeVersion = "0.14.1"

libraryDependencies += "io.circe" %% "circe-core" % circeVersion
libraryDependencies += "io.circe" %% "circe-generic" % circeVersion
libraryDependencies += "io.circe" %% "circe-parser" % circeVersion

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % Test
