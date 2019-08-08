version := "1.0-SNAPSHOT"

val json4sVersion = "3.6.5"
val enumeratumVersion = "1.5.13"

lazy val root = Project("kamon-on-play-27", file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(JavaAgent)
  .settings(
    scalaVersion := "2.12.8",
    organization := "cspinetta",
    name := "kamon-on-play-27",
    fork in run := true,
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-feature",
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-unchecked",
      "-Xlint",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Xfuture",
      "-Ypartial-unification",
    ),
    PlayKeys.devSettings += "config.file" -> "conf/application.conf",
    libraryDependencies ++= Seq(
      guice,
      ws,
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "ch.qos.logback.contrib" % "logback-json-classic" % "0.1.5",
      "ch.qos.logback.contrib" % "logback-jackson" % "0.1.5",
      "io.kamon" %% "kamon-bundle" % "2.0.0",
    ),
  )


resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += Resolver.bintrayRepo("kamon-io", "snapshots")
resolvers += Resolver.bintrayRepo("kamon-io", "releases")

// workaround for failed downloading of sbt-native-packager's docs and sources
updateConfiguration in updateSbtClassifiers := (updateConfiguration in updateSbtClassifiers).value.withMissingOk(true)
