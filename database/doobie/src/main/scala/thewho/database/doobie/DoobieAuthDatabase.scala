package thewho.database.doobie

import cats.effect.Blocker
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor
import thewho.database.module.AuthDatabase
import thewho.error.DatabaseException.DatabaseError.{ CredentialAlreadyExist, CredentialNotFound, UserNotFound }
import thewho.model._
import zio.blocking.Blocking
import zio.{ IO, Task, ZIO, ZLayer }

object DoobieAuthDatabase {

  type DriverName = String
  type URL        = String
  type Username   = String
  type Password   = String

  case class DBConfig(
    driverName: DriverName,
    url: URL,
    username: Username,
    password: Password,
  )

  // TODO make config a dependency
  def make(config: DBConfig): ZLayer[Blocking, Throwable, AuthDatabase] = {

    import zio.interop.catz._

    ZIO
      .runtime[Blocking]
      .toManaged_
      .flatMap { implicit runtime =>
        HikariTransactor
          .newHikariTransactor[Task](
            config.driverName,
            config.url,
            config.username,
            config.password,
            runtime.platform.executor.asEC,
            Blocker liftExecutionContext runtime.environment.get.blockingExecutor.asEC,
          )
          .toManaged
      }
      .map(new DoobieAuthDatabase(_))
      .toLayer

  }

}

class DoobieAuthDatabase(xa: Transactor[Task]) extends AuthDatabase.Service {

  object dsl extends syntax {
    override protected def _xa: Transactor[Task] = xa
  }

  import dsl._

  override final def createUser(credential: UnvalidatedCredential) =
    for {
      credentialExists <- credentialExists(credential.id)
      _                <- IO fail CredentialAlreadyExist when credentialExists
      userId           <- sql.user.create(credential)._create[UserId]
    } yield User(userId, Credential(credential.id, credential.secret))

  private def credentialExists(credentialId: CredentialId) =
    findCredential(credentialId) as true catchSome { case CredentialNotFound => IO succeed false }

  override final def findUser(credentialId: CredentialId)  =
    for {
      credential <- findCredential(credentialId)
      userId     <- sql.user.find(credentialId)._queryAndIfEmpty[UserId](UserNotFound)
    } yield User(userId, credential)

  override final def deleteUser(userId: UserId) =
    sql.user.delete(userId)._update.as(userId)

  private final def findCredential(credentialId: CredentialId) =
    sql.credential.find(credentialId)._queryAndIfEmpty[Credential](CredentialNotFound)

  // TODO optimize
  override final def findCredentialId(userId: UserId) =
    // first check if the user exists, then fetch its credential
    sql.user.find(userId)._queryAndIfEmpty(UserNotFound) *>
      sql.credential.find(userId)._queryAndIfEmpty[Credential](CredentialNotFound).map(_.id)

  override final def updateCredential(credential: Credential) =
    sql.credential.update(credential)._updateAndIfNoHits(CredentialNotFound).as(credential)

}
