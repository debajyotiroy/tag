name := "tag"

version := "1.0"

scalaVersion := "2.11.8"

Defaults.itSettings

lazy val `it-config-sbt-project` = project.in(file(".")).configs(IntegrationTest)

libraryDependencies ++= Seq(
  "com.github.finagle" %% "finch-core" % "0.9.2" changing(),
  "com.github.finagle" %% "finch-argonaut" % "0.9.2" changing(),
  "io.argonaut" %% "argonaut" % "6.1",
  "com.github.finagle" %% "finch-test" % "0.9.2" % "test,it" changing(),
  "org.scalacheck" %% "scalacheck" % "1.12.5" % "test,it",
  "org.scalatest" %% "scalatest" % "2.2.5" % "test,it"
)