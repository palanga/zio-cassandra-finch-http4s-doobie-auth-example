package thewho

import scalaz.zio.{ TaskR, ZIO }
import thewho.auth.{ Credential, CredentialId, User, UserId }

package object repository extends Repository.Service[Repository] {

  override def heartBeat: TaskR[Repository, Unit] = ZIO accessM (_.repository heartBeat)

  override def createUser(credential: Credential): TaskR[Repository, User] =
    ZIO accessM (_.repository createUser credential)

  override def findUser(userId: UserId): TaskR[Repository, User] =
    ZIO accessM (_.repository findUser userId)

  override def findUser(credentialId: CredentialId): ZIO[Repository, Throwable, User] =
    ZIO accessM (_.repository findUser credentialId)

  override def deleteUser(userId: UserId): ZIO[Repository, Throwable, UserId] =
    ZIO accessM (_.repository deleteUser userId)

  override def createCredential(userId: UserId, credential: Credential): ZIO[Repository, Throwable, Credential] =
    ZIO accessM (_.repository createCredential (userId, credential))

  override def findCredential(credentialId: CredentialId): ZIO[Repository, Throwable, Credential] =
    ZIO accessM (_.repository findCredential credentialId)

  override def findCredential(userId: UserId): ZIO[Repository, Throwable, Credential] =
    ZIO accessM (_.repository findCredential userId)

  override def updateCredential(credential: Credential): TaskR[Repository, Credential] =
    ZIO accessM (_.repository updateCredential credential)

  override def deleteCredential(credentialId: CredentialId): TaskR[Repository, CredentialId] =
    ZIO accessM (_.repository deleteCredential credentialId)

}
