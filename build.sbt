name := "the-who"

version := "0.1"

scalaVersion := "2.12.8"

scalacOptions += "-Ypartial-unification"
scalacOptions += "-deprecation"
scalacOptions += "-encoding"
scalacOptions += "UTF-8"
scalacOptions += "-language:higherKinds"
scalacOptions += "-language:postfixOps"
scalacOptions += "-feature"
scalacOptions += "-Xfatal-warnings"
scalacOptions += "-unchecked"

//scalacOptions += "-Ylog-classpath"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.3.0-M28"

libraryDependencies += "org.scalaz" %% "scalaz-zio"              % "1.0-RC4"
libraryDependencies += "org.scalaz" %% "scalaz-zio-interop-cats" % "1.0-RC4"

libraryDependencies += "io.circe" %% "circe-core"    % "0.10.0"
libraryDependencies += "io.circe" %% "circe-generic" % "0.10.0"
libraryDependencies += "io.circe" %% "circe-parser"  % "0.10.0"

libraryDependencies += "com.pauldijou" %% "jwt-core" % "2.1.0"

libraryDependencies += "org.http4s" %% "http4s-dsl"          % "0.18.23"
libraryDependencies += "org.http4s" %% "http4s-blaze-server" % "0.18.23"
libraryDependencies += "org.http4s" %% "http4s-blaze-client" % "0.18.23"
libraryDependencies += "org.http4s" %% "http4s-circe"        % "0.18.23"

libraryDependencies += "ch.qos.logback"  %  "logback-classic" % "1.2.3"

Revolver.settings
