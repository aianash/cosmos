import sbt._
import sbt.Classpaths.publishTask
import Keys._

import com.typesafe.sbt.SbtMultiJvm
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.{ MultiJvm, extraOptions, jvmOptions, scalatestOptions, multiNodeExecuteTests, multiNodeJavaName, multiNodeHostsFileName, multiNodeTargetDirName, multiTestOptions }

import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys

import com.typesafe.sbt.SbtStartScript

import sbtassembly.AssemblyPlugin.autoImport._

import com.aianonymous.sbt.standard.libraries.StandardLibraries


object CosmosBuild extends Build with StandardLibraries {

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


  def cosmos = Project(
    id = "cosmos",
    base = file("."),
    settings = Project.defaultSettings ++
      sharedSettings
  ).settings(
    libraryDependencies ++= Seq(
    ) ++ Libs.akka
  ) aggregate (core, preprocessing, processing)


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
    )
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
  ).dependsOn(core, preprocessing)

}