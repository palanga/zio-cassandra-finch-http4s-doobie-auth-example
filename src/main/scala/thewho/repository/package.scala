package thewho

import scalaz.zio.ZIO
import thewho.auth.{ Credential, CredentialId, User, UserId }
import thewho.error.{ RepositoryException, RepositoryFailure }

package object repository extends Repository.Service[Repository] {

  type RepoTask[A] = ZIO[Repository, RepositoryException, A]

  override def heartBeat: ZIO[Repository, RepositoryFailure, Unit] = ZIO accessM (_.repository heartBeat)

  /**
   * For testing purposes only
   */
  override def createUsersTable: ZIO[Repository, RepositoryFailure, Unit] =
    ZIO accessM (_.repository createUsersTable)

  /**
   * For testing purposes only
   */
  override def dropUsersTable: ZIO[Repository, RepositoryFailure, Unit] =
    ZIO accessM (_.repository dropUsersTable)

  override def createUser(credential: Credential): RepoTask[User] =
    ZIO accessM (_.repository createUser credential)

  override def findUser(userId: UserId): RepoTask[User] =
    ZIO accessM (_.repository findUser userId)

  override def findUser(credentialId: CredentialId): RepoTask[User] =
    ZIO accessM (_.repository findUser credentialId)

  override def deleteUser(userId: UserId): RepoTask[UserId] =
    ZIO accessM (_.repository deleteUser userId)

  /**
   * For testing purposes only
   */
  override def createCredentialsTable: ZIO[Repository, RepositoryFailure, Unit] =
    ZIO accessM (_.repository createCredentialsTable)

  /**
   * For testing purposes only
   */
  override def dropCredentialsTable: ZIO[Repository, RepositoryFailure, Unit] =
    ZIO accessM (_.repository dropCredentialsTable)

  override def findCredential(userId: UserId): RepoTask[Credential] =
    ZIO accessM (_.repository findCredential userId)

  override def updateCredential(credential: Credential): RepoTask[Credential] =
    ZIO accessM (_.repository updateCredential credential)

}
