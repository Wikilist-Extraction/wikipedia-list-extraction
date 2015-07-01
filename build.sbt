name := "wikipedia-list-extraction"

version := "1.0"

scalaVersion := "2.11.6"

scalacOptions += "-target:jvm-1.8"

logLevel := Level.Warn

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.4",
  "org.scala-lang.modules" %% "scala-async" % "0.9.2",
  "org.apache.jena" % "apache-jena-libs" % "2.13.0"
)
