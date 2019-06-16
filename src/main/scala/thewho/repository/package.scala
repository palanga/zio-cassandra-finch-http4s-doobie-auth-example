package thewho

import scalaz.zio.ZIO
import thewho.auth.{ Credential, CredentialId, User, UserId }
import thewho.error.{ RepositoryException, RepositoryFailure }

package object repository extends Repository.Service[Repository] {

  type RepoTask[A] = ZIO[Repository, RepositoryException, A]

  override def heartBeat: ZIO[Repository, RepositoryFailure, Unit] = ZIO accessM (_.repository heartBeat)

  override def createUser(credential: Credential): RepoTask[User] =
    ZIO accessM (_.repository createUser credential)

  override def findUser(userId: UserId): RepoTask[User] =
    ZIO accessM (_.repository findUser userId)

  override def findUser(credentialId: CredentialId): RepoTask[User] =
    ZIO accessM (_.repository findUser credentialId)

  override def deleteUser(userId: UserId): RepoTask[UserId] =
    ZIO accessM (_.repository deleteUser userId)

  override def createCredential(credential: Credential, userId: UserId): RepoTask[Credential] =
    ZIO accessM (_.repository createCredential (credential, userId))

  override def findCredential(credentialId: CredentialId): RepoTask[Credential] =
    ZIO accessM (_.repository findCredential credentialId)

  override def findCredential(userId: UserId): RepoTask[Credential] =
    ZIO accessM (_.repository findCredential userId)

  override def updateCredential(credential: Credential): RepoTask[Credential] =
    ZIO accessM (_.repository updateCredential credential)

  override def deleteCredential(credentialId: CredentialId): RepoTask[CredentialId] =
    ZIO accessM (_.repository deleteCredential credentialId)

}
