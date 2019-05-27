package thewho.repository

import doobie._
import doobie.implicits._
import scalaz.zio.{ Task, ZIO }
import scalaz.zio.interop.catz._
import thewho.auth.{ Credential, CredentialId, User, UserId }
import thewho.repository.Repository.Service

// TODO extract the queries.
trait DoobieRepository extends Repository {

  protected def xa: Transactor[Task]

  override final val repository = new Service[Any] {

    override final def heartBeat: Task[Unit] =
      sql"""SELECT true""".query[Boolean].option.transact(xa).flatMap {
        case Some(_) => ZIO.succeed(())
        case None    => ZIO.fail(new Exception("DB heartbeat failed"))
      }

    private def createUser: Task[UserId] =
      sql"""INSERT INTO users DEFAULT VALUES""".update
        .withUniqueGeneratedKeys[Int]("id")
        .transact(xa)

    // TODO the recovery with _catchAll_ and _mapError_ and _initCause_ is shit. Maybe use _orElse_
    // TODO and take a look to what happens with the errors. Do they accumulate ? Or just the last one is kept ?
    override final def createUser(credential: Credential): Task[User] =
      for {
        userId <- createUser
        _      <- createCredential(userId, credential).catchAll(t1 => deleteUser(userId).mapError(_ initCause t1))
      } yield User(userId, credential)

    override final def findUser(userId: UserId): Task[User] =
      for {
        credential <- findCredential(userId)
      } yield User(userId, credential)

    override final def findUser(credentialId: CredentialId): Task[User] =
      for {
        credential <- findCredential(credentialId)
        userId <- sql"""SELECT (user_id) FROM credentials WHERE id = $credentialId"""
                   .query[UserId]
                   .option
                   .transact(xa)
                   .flatMap {
                     case Some(value) => Task succeed value
                     case None        => Task fail new Exception(s"Couldn't find credential $credentialId")
                   }
      } yield User(userId, credential)

    override final def deleteUser(userId: UserId): Task[UserId] =
      for {
        credential <- findCredential(userId)
        _          <- deleteCredential(credential.id)
        _ <- sql"""DELETE FROM users WHERE id = $userId""".update.run.transact(xa).flatMap {
              case 1 => Task succeed userId
              case 0 => Task fail new Exception(s"Couldn't delete user $userId")
            }
      } yield userId

    // TODO curryfy and flip args so we can createUser flatMap createCredential(credential).
    override final def createCredential(userId: UserId, credential: Credential): Task[Credential] =
      sql"""
           |INSERT INTO credentials (id, secret, user_id)
           |VALUES (${credential.id}, ${credential.secret}, $userId)""".stripMargin.update.run
        .transact(xa)
        .map(_ => credential)

    override final def findCredential(credentialId: CredentialId): Task[Credential] =
      sql"""SELECT * FROM credentials WHERE credentials.id = $credentialId"""
        .query[Credential]
        .option
        .transact(xa)
        .flatMap {
          case Some(value) => Task succeed value
          case None        => Task fail new Exception(s"Couldn't find credential $credentialId")
        }

    override final def findCredential(userId: UserId): Task[Credential] =
      sql"""SELECT * FROM credentials WHERE credentials.user_id = $userId"""
        .query[Credential]
        .option
        .transact(xa)
        .flatMap {
          case Some(value) => Task succeed value
          case None        => Task fail new Exception(s"Couldn't find credential for user $userId")
        }

    override final def updateCredential(credential: Credential): Task[Credential] =
      sql"""UPDATE credentials SET secret = ${credential.secret} WHERE id = ${credential.id}""".update.run
        .transact(xa)
        .flatMap {
          case 1 => Task succeed credential
          case 0 => Task fail new Exception(s"Couldn't update credential ${credential.id}")
        }

    override final def deleteCredential(credentialId: CredentialId): Task[CredentialId] =
      sql"""DELETE FROM credentials WHERE id = $credentialId""".update.run.transact(xa).flatMap {
        case 1 => Task succeed credentialId
        case 0 => Task fail new Exception(s"Couldn't delete credential $credentialId")
      }

  }

}
