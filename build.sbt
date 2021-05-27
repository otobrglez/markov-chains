import Dependencies._

ThisBuild / scalaVersion := "2.13.5"
ThisBuild / version := "0.0.1"
ThisBuild / organization := "com.pinkstack"
ThisBuild / organizationName := "pinkstack"

lazy val root = (project in file("."))
  .settings(
    name := "Markov Chains",
    libraryDependencies ++=
      cats ++ circe ++ sttp ++
        doobie ++ logging ++ apacheCommons ++
        fs2 ++ scalaTest
  )

addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")

scalacOptions ++= Seq(
  "-Ymacro-annotations",
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-encoding", "UTF-8", // Specify character encoding used by source files.
  "-explaintypes", // Explain type errors in more detail.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-language:higherKinds", // Allow higher-kinded types
  "-language:implicitConversions", // Allow definition of implicit functions called views
  "-language:existentials",
  "-language:postfixOps", // New lines for each options
  "-unchecked", // additional warnings where generated code depends on assumptions
  // TODO: This should be used.
  // "-Xlint", // recommended additional warnings
  // "-Ywarn-value-discard", // Warn when non-Unit expression results are unused
  "-Ywarn-dead-code"
)

lazy val jvmOptions = Seq(
  "-Xms1G",
  "-Xmx4G",
  "-Xss1M",
  "-XX:+CMSClassUnloadingEnabled",
  "-XX:MaxPermSize=256M"
)
