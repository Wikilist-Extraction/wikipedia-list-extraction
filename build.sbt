//import com.github.retronym.SbtOneJar._

name := "wikipedia-list-extraction"

version := "1.0"

scalaVersion := "2.11.5"

//scalacOptions += "-target:jvm-1.8"

javaOptions in run ++= Seq(
  "-Xms256M", "-Xmx4G"
)

logLevel := sbt.Level.Info

testOptions in Test += Tests.Argument("-oD")

//oneJarSettings
//exportJars := true

//fork in run := true

val sprayVersion = "1.3.2"

resolvers ++= Seq(
  "unreleased-jars" at "http://nicoring.de/maven2/",
  "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/",
  "spray repo stable" at "http://repo.spray.io/"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4",
  "org.scalanlp" %% "chalk" % "1.3.3-SNAPSHOT",
  "org.scala-lang.modules" %% "scala-async" % "0.9.2",
  "com.typesafe.akka" %% "akka-stream-experimental"	% "1.0-RC4",
  "io.spray" %%  "spray-json" % "1.3.2",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.3",
  "com.typesafe.play"   %% "play-json"                     % "2.3.6",
  "io.spray"            %% "spray-client"                  % sprayVersion,
  "io.spray"            %% "spray-routing"                 % sprayVersion,
  "com.wandoulabs.akka" %% "spray-websocket"               % "0.1.3",
  "commons-io"          %  "commons-io"                    % "2.4",
  "org.linkeddatafragments" % "ldf-client" % "0.1-SNAPSHOT"
    exclude("com.hp.hpl.jena", "jena"),
  "it.cnr.isti.hpc" % "json-wikipedia" % "1.0.2" //changing()
    exclude("org.slf4j", "slf4j-api")
    exclude("ch.qos.logback", "logback-classic")
)

Revolver.settings