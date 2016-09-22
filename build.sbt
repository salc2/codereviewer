name := "Foo root project"

scalaVersion in ThisBuild := "2.11.8"

lazy val root = project.in(file(".")).
  aggregate(fooJS, fooJVM).
  settings(
    publish := {},
    publishLocal := {}
  )

lazy val foo = crossProject.in(file(".")).
  settings(
    name := "foo",
    version := "0.1-SNAPSHOT",
     libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.6.0"
  ).
  jvmSettings(
    libraryDependencies += "com.typesafe.akka" %% "akka-http-experimental" % "2.4.10"
  ).
  jsSettings(
    // Add JS-specific settings here
  )

lazy val fooJVM = foo.jvm
lazy val fooJS = foo.js