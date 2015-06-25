import sbt._
import Keys._

object MyBuild extends Build {

  val name = "wikipedia-list-extraction"

  val version = "1.0"

  val scalaVersion = "2.11.6"

  lazy val submoduleSettings = Seq(
    unmanagedBase := (root.base \ "lib").get(0)
  )

  lazy val root: Project = project.in(file(".")).aggregate(dump, core)

  lazy val dump = project
    .settings(submoduleSettings: _*)
    .dependsOn(core)

  lazy val core = project
    .settings(submoduleSettings: _*)
}