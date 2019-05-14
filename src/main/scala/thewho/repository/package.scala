package thewho

import scalaz.zio.ZIO
import thewho.auth.{ Credential, CredentialId, UserId }

package object repository extends Repository.Service[Repository] {

  override def findCredentialSecret(credentialId: CredentialId) =
    ZIO accessM (_.repository findCredentialSecret credentialId)

  override def findUserId(credentialId: CredentialId) =
    ZIO accessM (_.repository findUserId credentialId)

  override def findUser(credentialId: CredentialId) =
    ZIO accessM (_.repository findUser credentialId)

  override def findCredentialIds(userId: UserId) =
    ZIO accessM (_.repository findCredentialIds userId)

  override def createCredential(credential: Credential) =
    ZIO accessM (_.repository createCredential credential)

}
