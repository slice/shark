ThisBuild / scalaVersion := "2.13.6"
ThisBuild / organization := "zone.slice"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core"   % "2.1.1",
  "org.typelevel" %% "cats-effect" % "2.1.1",
  "co.fs2"        %% "fs2-core"    % "2.3.0",
  "co.fs2"        %% "fs2-io"      % "2.3.0",
)
