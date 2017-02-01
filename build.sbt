lazy val emsgame = project.in(file(".")).
  aggregate(sjsJS, sjsJVM).
  settings(
    publish := {},
    publishLocal := {}
  )

lazy val sjs = crossProject.in(file(".")).
  settings(
    name := "ems-game",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.11.8",
    crossPaths := false
  ).
  jvmSettings(
  ).
  jsSettings(
    persistLauncher := true,
    libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.6.1",
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.0",
    libraryDependencies += "com.github.japgolly.scalacss" %%% "core" % "0.5.1",
    libraryDependencies += "com.github.japgolly.scalacss" %%% "ext-scalatags" % "0.5.1",
    libraryDependencies += "com.lihaoyi" %%% "scalarx" % "0.3.2"
  )

lazy val sjsJVM = sjs.jvm
lazy val sjsJS = sjs.js

lazy val testing = project
  .dependsOn(sjsJS)
  .settings(
    scalaVersion := "2.11.8",
    crossPaths := false,
    resolvers += "javanet" at "http://download.java.net/maven/2",
    resolvers += "boundless" at "http://repo.boundlessgeo.com/main",
    resolvers += "osgeo" at "http://download.osgeo.org/webdav/geotools/",
    libraryDependencies += "com.typesafe.akka" % "akka-http-experimental_2.11" % "2.4.11.1",
    libraryDependencies += "org.geotools" % "gt-shapefile" % "16.1",
    libraryDependencies += "org.geotools" % "gt-geometry" % "16.1",
    libraryDependencies += "org.geotools" % "gt-jts-wrapper" % "16.1"
  )