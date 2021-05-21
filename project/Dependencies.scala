import sbt._

object Dependencies {

  type Version = String

  object Versions {
    val Sttp: Version = "3.3.2"
    val ScalaTest: Version = "3.2.8"

    val Cats: Version = "2.3.0"
    val Circe: Version = "0.12.3"
    val Doobie: Version = "0.12.1"
    val LogbackClassic: Version = "1.2.3"
    val ScalaLogging: Version = "3.9.3"
  }

  val sttp = Seq(
    "com.softwaremill.sttp.client3" %% "core" % Versions.Sttp,
    "com.softwaremill.sttp.client3" %% "circe" % Versions.Sttp
  )

  val cats = Seq(
    "org.typelevel" %% "cats-core" % Versions.Cats
  )

  val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % Versions.Circe)

  val doobie = Seq(
    "org.tpolecat" %% "doobie-core" % Versions.Doobie,
    "org.xerial" % "sqlite-jdbc" % "3.34.0"

    // "org.tpolecat" %% "doobie-specs2"    % "0.12.1" % "test", // Specs2 support for typechecking statements.
    // "org.tpolecat" %% "doobie-scalatest" % "0.12.1" % "test"  // ScalaTest support for typechecking statements.
  )

  val logging = Seq(
    "ch.qos.logback" % "logback-classic" % Versions.LogbackClassic,
    "com.typesafe.scala-logging" %% "scala-logging" % Versions.ScalaLogging
  )

  val apacheCommons = Seq(
    "org.apache.commons" % "commons-math3" % "3.6.1"
  )

  val scalaTest = Seq(
    "org.scalatest" %% "scalatest" % Versions.ScalaTest
  )
}
