ThisBuild / scalaVersion := "2.13.6"
ThisBuild / organization := "zone.slice"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core"   % "2.6.1",
  "org.typelevel" %% "cats-effect" % "3.2.5",
  "co.fs2"        %% "fs2-core"    % "3.1.1",
  "co.fs2"        %% "fs2-io"      % "3.1.1",
  "com.comcast"   %% "ip4s-core"   % "3.0.3",
)

scalacOptions ++= Seq(
  "-deprecation"
)
