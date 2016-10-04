import sbt._
import sbt.Classpaths.publishTask
import Keys._

import com.typesafe.sbt.packager.archetypes.JavaAppPackaging

import sbtassembly.AssemblyPlugin.autoImport._

import com.typesafe.sbt.SbtNativePackager._, autoImport._
import com.typesafe.sbt.packager.Keys._
import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd, CmdLike}

import com.aianonymous.sbt.standard.libraries.StandardLibraries

import com.typesafe.sbt.packager.docker.DockerPlugin


object CosmosBuild extends Build with StandardLibraries {

  lazy val makeScript = TaskKey[Unit]("make-script", "make bash script in local directory to run main classes")

  def sharedSettings = Seq(
    organization := "com.aianonymous",
    version := "0.1.0",
    scalaVersion := Version.scala,
    crossScalaVersions := Seq(Version.scala, "2.10.4"),
    scalacOptions := Seq("-unchecked", "-optimize", "-deprecation", "-feature", "-language:higherKinds", "-language:implicitConversions", "-language:postfixOps", "-language:reflectiveCalls", "-Yinline-warnings", "-encoding", "utf8"),
    retrieveManaged := true,

    fork := true,
    javaOptions += "-Xmx2500M",

    resolvers ++= StandardResolvers,

    publishMavenStyle := true
  ) ++ net.virtualvoid.sbt.graph.Plugin.graphSettings


  lazy val cosmos = Project(
    id = "cosmos",
    base = file("."),
    settings = Project.defaultSettings ++
      sharedSettings
  ).aggregate(core, preprocessing, processing, server)


  lazy val core = Project(
    id="cosmos-core",
    base = file("core"),
    settings = Project.defaultSettings ++
      sharedSettings
  ).settings(
    name := "cosmos-core"
  )


  lazy val preprocessing = Project(
    id = "cosmos-preprocessing",
    base = file("preprocessing"),
    settings = Project.defaultSettings ++
      sharedSettings
  ).settings(
    name := "cosmos-preprocessing",
    libraryDependencies ++= Seq(
    ) ++ Libs.akka
      ++ Libs.akkaCluster
      ++ Libs.commonsCore
      ++ Libs.commonsEvents
      ++ Libs.cassieCore
  )


  lazy val processing = Project(
    id = "cosmos-processing",
    base = file("processing"),
    settings = Project.defaultSettings ++
      sharedSettings
  ).settings(
    name := "cosmos-processing",
    libraryDependencies ++= Seq(
    ) ++ Libs.akka
      ++ Libs.cassieCore
  ).dependsOn(core, preprocessing)


  lazy val server = Project(
    id = "cosmos-server",
    base = file("server"),
    settings = Project.defaultSettings ++
      sharedSettings
  )
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    name := "cosmos-server",
    mainClass in Compile := Some("cosmos.server.CosmosServer"),
    dockerExposedPorts := Seq(4849),
    dockerEntrypoint := Seq("sh", "-c",
                            """export COSMOS_HOST=`ifdata -pa eth0` && echo $COSMOS_HOST && \
                            |  export COSMOS_PORT=4849 && \
                            |  bin/cosmos-server -Dakka.cluster.roles.0=cosmos-server $*""".stripMargin
                            ),
    dockerRepository := Some("aianonymous"),
    dockerBaseImage := "aianonymous/baseimage",
    dockerCommands ++= Seq(
      Cmd("USER", "root")
    ),
    libraryDependencies ++= Seq(
    ) ++ Libs.akka
      ++ Libs.microservice,
  makeScript <<= (stage in Universal, stagingDirectory in Universal, baseDirectory in ThisBuild, streams) map { (_, dir, cwd, streams) =>
      var path = dir / "bin" / "cosmos-server"
      sbt.Process(Seq("ln", "-sf", path.toString, "cosmos-server"), cwd) ! streams.log
    }
  ).dependsOn(processing)

}