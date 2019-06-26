package thewho.repository

import doobie.hikari.HikariTransactor
import scalaz.zio.{ DefaultRuntime, Task, ZIO }
import thewho.Ops
import thewho.config.DBConfig
import thewho.error.RepositoryFailure

class RepositoryTestSuite extends Ops {

  object runtime extends DefaultRuntime

  private val config = DBConfig(
    "org.postgresql.Driver",
    "jdbc:postgresql://postgres:5432/test",
    "postgres",
    "postgres",
    4
  )

  private val initializeDB = thewho.repository.createUsersTable *> thewho.repository.createCredentialsTable
  private val cleanDB      = thewho.repository.dropCredentialsTable *> thewho.repository.dropUsersTable

  private val allTestCases = UserTest.cases ++ CredentialTest.cases

  private val suite: ZIO[Repository, RepositoryFailure, List[Unit]] =
    cleanDB *> initializeDB *> ZIO.foreach(allTestCases)(compareResults[Repository])

  private val suiteWithDependencies: ZIO[Any, Throwable, List[Unit]] = Transactor.fromConfig(config).use { transactor =>
    suite.provide(new DoobieRepository {
      override protected def xa: HikariTransactor[Task] = transactor
    })
  }

  runtime unsafeRun suiteWithDependencies.mapError(unboxHasCause)

}
