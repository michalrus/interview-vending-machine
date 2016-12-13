enablePlugins(GitVersioning)

git.useGitDescribe := true

organization in ThisBuild := "com.michalrus"

scalaVersion in ThisBuild := "2.11.8"

scalafmtConfig in ThisBuild := Some(file(".scalafmt.conf"))

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

lazy val root = (project in file("."))
  .settings(reformatOnCompileSettings)
  .settings(
    name := "interview",
    scalacOptions in Compile ++= Seq("-deprecation",
                                     "-feature",
                                     "-unchecked",
                                     "-Xlint",
                                     "-Xfatal-warnings",
                                     "-Yno-adapted-args",
                                     "-Yrangepos",
                                     "-Ywarn-dead-code",
                                     "-Ywarn-inaccessible",
                                     "-Ywarn-infer-any",
                                     "-Ywarn-nullary-override",
                                     "-Ywarn-numeric-widen",
                                     "-Ywarn-unused",
                                     "-Ywarn-unused-import",
                                     "-Ywarn-value-discard"),
    wartremoverErrors ++= Warts.all,
    scalastyleFailOnError := true,
    compileScalastyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle
      .in(Compile)
      .toTask("")
      .value,
    (compile in Compile) <<= (compile in Compile) dependsOn compileScalastyle,
    libraryDependencies ++= Seq(
      "joda-time"      % "joda-time"    % "2.9.6",
      "org.joda"       % "joda-convert" % "1.8",
      "org.scalatest"  %% "scalatest"   % "2.2.4" % "test",
      "org.scalacheck" %% "scalacheck"  % "1.12.5" % "test"
    )
  )
