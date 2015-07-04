name := "wikipedia-list-extraction"

version := "1.0"

scalaVersion := "2.11.6"

scalacOptions += "-target:jvm-1.8"

logLevel := sbt.Level.Error

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.4",
  "org.scala-lang.modules" % "scala-async_2.11" % "0.9.2",
//  "org.apache.jena" % "apache-jena-libs" % "2.10.1",
//  "com.hp.hpl.jena" % "jena" % "2.6.4",
  "org.linkeddatafragments" % "ldf-client" % "0.1-SNAPSHOT" changing(),
  "it.cnr.isti.hpc" % "json-wikipedia" % "1.0.0" changing()
    exclude("org.slf4j", "slf4j-api")
    exclude("ch.qos.logback", "logback-classic")
)
