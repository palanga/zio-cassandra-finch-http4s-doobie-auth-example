name := "the-who"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies += "org.scalaz" %% "scalaz-zio" % "1.0-RC3"

libraryDependencies += "io.circe" %% "circe-core"    % "0.10.0"
libraryDependencies += "io.circe" %% "circe-generic" % "0.10.0"
libraryDependencies += "io.circe" %% "circe-parser"  % "0.10.0"

libraryDependencies += "com.pauldijou" %% "jwt-core" % "2.1.0"
