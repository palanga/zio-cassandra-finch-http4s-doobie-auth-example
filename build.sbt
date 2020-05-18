lazy val root =
  (project in file("."))
    .settings(name := "thewho")
    .settings(version := "0.1")
    .settings(skip in publish := true)
    .aggregate(
      app,
      core,
      coreCommon,
      dbInMemory,
      dbDoobie,
      serverHttp4s,
      utilsZioTest,
    )

val commonSettings =
  Def.settings(
    scalacOptions := ScalaOptions.dev,
    scalaVersion := "2.13.1",
    fork in Test := true,
  )

lazy val app =
  (project in file("app"))
    .settings(name := "app")
    .settings(commonSettings)
    .settings(libraryDependencies ++= dependencies.app.toSeq)
    .dependsOn(
      serverHttp4s,
      dbInMemory,
    )

lazy val core =
  (project in file("core"))
    .settings(name := "core")
    .settings(commonSettings)
    .settings(testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")))
    .settings(libraryDependencies ++= dependencies.core.toSeq)
    .dependsOn(
      coreCommon,
      dbInMemory   % Test,
      utilsZioTest % Test,
    )

lazy val coreCommon =
  (project in file("core/common"))
    .settings(name := "core-common")
    .settings(commonSettings)
    .settings(testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")))
    .settings(libraryDependencies ++= dependencies.coreCommon.toSeq)

lazy val dbInMemory =
  (project in file("database/in-memory"))
    .settings(name := "database-inmemory")
    .settings(commonSettings)
    .settings(libraryDependencies ++= dependencies.database.inMemory.toSeq)
    .dependsOn(coreCommon)

lazy val dbDoobie =
  (project in file("database/doobie"))
    .settings(name := "database-doobie")
    .settings(commonSettings)
    .settings(libraryDependencies ++= dependencies.database.doobie.toSeq)
    .dependsOn(coreCommon)

lazy val serverHttp4s =
  (project in file("server/http4s"))
    .settings(name := "server-http4s")
    .settings(commonSettings)
    .settings(libraryDependencies ++= dependencies.server.http4s.toSeq)
    .dependsOn(
      core,
      coreCommon,
    )

lazy val utilsZioTest =
  (project in file("utils-zio-test"))
    .settings(name := "utils.zio.test")
    .settings(commonSettings)
    .settings(libraryDependencies ++= dependencies.utilsZioTest.toSeq)

//Revolver.settings
