name := "wikipedia-list-extraction"

version := "1.0"

scalaVersion := "2.11.6"

scalacOptions += "-target:jvm-1.8"

logLevel := sbt.Level.Info

testOptions in Test += Tests.Argument("-oD")

resolvers ++= Seq(
  Resolver.mavenLocal,
  "unreleased-jars" at "http://nicoring.de/maven2/"
)

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.4",
  "org.scalanlp" % "chalk" % "1.3.0",
  "org.scala-lang.modules" % "scala-async_2.11" % "0.9.2",
  "org.linkeddatafragments" % "ldf-client" % "0.1-SNAPSHOT"
    exclude("com.hp.hpl.jena", "jena"),
  "it.cnr.isti.hpc" % "json-wikipedia" % "1.0.3" //changing()
    exclude("org.slf4j", "slf4j-api")
    exclude("ch.qos.logback", "logback-classic")
)
