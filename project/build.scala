import sbt._
import Keys._

object MyBuild extends Build {

  val name = "wikipediaListExtraction"

  val version = "1.0"

  val scalaVersion = "2.11.6"

  lazy val root = project.in(file(".")).aggregate(dump, core)

  lazy val dump = project
    .dependsOn()

  lazy val core = project
}