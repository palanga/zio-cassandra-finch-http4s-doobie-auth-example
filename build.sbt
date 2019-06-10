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

libraryDependencies += "com.github.pureconfig" %% "pureconfig"              % "0.11.0"
libraryDependencies += "com.github.pureconfig" %% "pureconfig-yaml"         % "0.11.0"
libraryDependencies += "com.pauldijou"         %% "jwt-core"                % "2.1.0"
libraryDependencies += "io.circe"              %% "circe-core"              % "0.10.0"
libraryDependencies += "io.circe"              %% "circe-generic"           % "0.10.0"
libraryDependencies += "io.circe"              %% "circe-parser"            % "0.10.0"
libraryDependencies += "org.http4s"            %% "http4s-blaze-server"     % "0.20.1"
libraryDependencies += "org.http4s"            %% "http4s-circe"            % "0.20.1"
libraryDependencies += "org.http4s"            %% "http4s-dsl"              % "0.20.1"
libraryDependencies += "org.scalaz"            %% "scalaz-core"             % "7.2.27"
libraryDependencies += "org.scalaz"            %% "scalaz-zio"              % "1.0-RC5"
libraryDependencies += "org.scalaz"            %% "scalaz-zio-interop-cats" % "1.0-RC5"
libraryDependencies += "org.tpolecat"          %% "doobie-core"             % "0.6.0"
libraryDependencies += "org.tpolecat"          %% "doobie-hikari"           % "0.6.0"
libraryDependencies += "org.tpolecat"          %% "doobie-postgres"         % "0.6.0"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3" // TODO #5 improve logging

Revolver.settings
