import sbt.compilerPlugin
import sbt.librarymanagement.syntax.{ stringToOrganization, Test }

object dependencies {

  import Definitions._

  private val common = Set(
    zio,
    compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
  )

  private val commonTest = Set(
    zioTest,
    zioTestSbt,
  )

  val app = common ++ Set(
    logbackClassic,
  )

  val core = common ++ commonTest ++ Set(
    circeCore,
    circeGeneric,
    circeParser,
    jwt,
    scalactic,
    scalatest,
    zioInteropCats,
  )

  val coreCommon = common

  object database {

    val inMemory = common ++ commonTest

    val doobie = common ++ commonTest ++ Set(
      doobieCore,
      doobieHikari,
      doobiePostgres,
      zioInteropCats,
    )

  }

  object server {
    val http4s = common ++ commonTest ++ Set(
      circeCore,
      circeGeneric,
      http4sCirce,
      http4sDsl,
      http4sServer,
      zioInteropCats,
    )
  }

  val utilsZioTest = Set(
    zio,
    "dev.zio" %% "zio-test" % Versions.zio,
  )

}

object Definitions {

  val circeCore    = "io.circe" %% "circe-core"    % Versions.circe
  val circeGeneric = "io.circe" %% "circe-generic" % Versions.circe
  val circeParser  = "io.circe" %% "circe-parser"  % Versions.circe

  val doobieCore     = "org.tpolecat" %% "doobie-core"     % Versions.doobie
  val doobieHikari   = "org.tpolecat" %% "doobie-hikari"   % Versions.doobie
  val doobiePostgres = "org.tpolecat" %% "doobie-postgres" % Versions.doobie

  val http4sCirce  = "org.http4s" %% "http4s-circe"        % Versions.http4s
  val http4sDsl    = "org.http4s" %% "http4s-dsl"          % Versions.http4s
  val http4sServer = "org.http4s" %% "http4s-blaze-server" % Versions.http4s

  val jwt = "com.pauldijou" %% "jwt-core" % Versions.jwt

  val logbackClassic = "ch.qos.logback" % "logback-classic" % Versions.logback

  val scalactic = "org.scalactic" %% "scalactic" % Versions.scalatest
  val scalatest = "org.scalatest" %% "scalatest" % Versions.scalatest % Test

  val zio        = "dev.zio" %% "zio"          % Versions.zio
  val zioTest    = "dev.zio" %% "zio-test"     % Versions.zio % Test
  val zioTestSbt = "dev.zio" %% "zio-test-sbt" % Versions.zio % Test

  val zioInteropCats = "dev.zio" %% "zio-interop-cats" % Versions.zioInteropCats

}

object Versions {
  val circe          = "0.13.0"
  val doobie         = "0.8.6"
  val http4s         = "0.21.1"
  val jwt            = "4.2.0"
  val logback        = "1.2.3"
  val scalatest      = "3.1.2"
  val zio            = "1.0.0-RC20"
  val zioInteropCats = "2.1.3.0-RC15"
}
