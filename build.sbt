name := "wikipedia-list-extraction"

version := "1.0"

scalaVersion := "2.11.6"

scalacOptions += "-target:jvm-1.8"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.4",
  "org.scalanlp" % "chalk" % "1.3.0"
)
