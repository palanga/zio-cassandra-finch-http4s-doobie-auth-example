package thewho.repository

import doobie.util.transactor.{ Transactor => DoobieTransactor }
import scalaz.zio.{ IO, Task }
import thewho.auth.{ Credential, CredentialId, User, UserId }
import thewho.error._
import thewho.repository.Repository.Service

trait DoobieRepository extends Repository {

  protected def xa: DoobieTransactor[Task]

  object dsl extends syntax {
    override protected def _xa: DoobieTransactor[Task] = xa
  }

  import dsl._

  override final val repository = new Service[Any] {

    override final def heartBeat: IO[RepositoryFailure, Unit] = sql.heartBeat._query[Unit].map(_ => ())

    override final def createUser(credential: Credential): IO[RepositoryException, User] =
      for {
        credentialExists <- credentialExists(credential.id)
        _                <- IO.when(credentialExists)(IO fail CredentialAlreadyExist)
        userId           <- sql.user.create(credential)._create[UserId]
      } yield User(userId, credential)

    private def credentialExists(credentialId: CredentialId): IO[RepositoryException, Boolean] =
      findCredential(credentialId).map(_ => true).catchSome { case CredentialNotFound => IO succeed false }

    // TODO optimize
    override final def findUser(userId: UserId): IO[RepositoryException, User] =
      // first check if the user exists, then fetch its credential
      sql.user.find(userId)._queryAndIfEmpty(UserNotFound) *>
        findCredential(userId).map(credential => User(userId, credential))

    override final def findUser(credentialId: CredentialId): IO[RepositoryException, User] =
      for {
        credential <- findCredential(credentialId)
        userId     <- sql.user.find(credentialId)._queryAndIfEmpty[UserId](UserNotFound)
      } yield User(userId, credential)

    override final def deleteUser(userId: UserId): IO[RepositoryException, UserId] =
      for {
        credential <- findCredential(userId)
        _          <- deleteCredential(credential.id)
        _          <- sql.user.delete(userId)._update
      } yield userId

    // TODO this should not exist by now
    override final def createCredential(credential: Credential, userId: UserId): IO[RepositoryException, Credential] =
      sql.credential.create(credential)(userId)._updateAndIfNoHits(NoCredentialsCreated).map(_ => credential)

    // TODO this should be private
    override final def findCredential(credentialId: CredentialId): IO[RepositoryException, Credential] =
      sql.credential.find(credentialId)._queryAndIfEmpty[Credential](CredentialNotFound)

    override final def findCredential(userId: UserId): IO[RepositoryException, Credential] =
      sql.credential.find(userId)._queryAndIfEmpty[Credential](CredentialNotFound)

    override final def updateCredential(credential: Credential): IO[RepositoryException, Credential] =
      sql.credential.update(credential)._updateAndIfNoHits(CredentialNotFound).map(_ => credential)

    // TODO this should not exist by now
    override final def deleteCredential(credentialId: CredentialId): IO[RepositoryException, CredentialId] =
      sql.credential.delete(credentialId)._updateAndIfNoHits(CredentialNotFound).map(_ => credentialId)

  }

}
