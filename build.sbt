lazy val root = project.in(file(".")).
  aggregate(fooJS, fooJVM).
  settings(
    publish := {},
    publishLocal := {}
  )

lazy val foo = crossProject.in(file(".")).
  settings(
    name := "codereview",
    version := "0.1-SNAPSHOT",
    scalaVersion in ThisBuild := "2.11.8",
     libraryDependencies ++= Seq(
       "com.lihaoyi" %%% "scalatags" % "0.6.0",
       "com.github.benhutchison" %%% "prickle" % "1.1.11"),
        mainClass in assembly := Some("ve.chucho.codereview.server.CodeReviewServer")
  ).
  jvmSettings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http-experimental" % "2.4.10",
      "org.webjars" % "webjars-locator" % "0.32",
      "org.webjars.bower" % "material-design-lite" % "1.2.1",
      "org.webjars.bower" % "diff2html" % "2.0.8"
    ),
    assemblyJarName in assembly := "jvm.jar"
  ).
  jsSettings(
    resolvers += Resolver.sonatypeRepo("releases"),
    libraryDependencies ++= Seq(
      "eu.unicredit" %%% "akkajsactor" % "0.2.4.10",
      "eu.unicredit" %%% "akkajsactorstream" % "0.2.4.10"
    )
  )

lazy val fooJVM = foo.jvm
lazy val fooJS = foo.js