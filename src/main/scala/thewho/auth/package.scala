package thewho

import scalaz.zio.ZIO
import scalaz.zio.clock.Clock
import thewho.repository.Repository

package object auth extends Auth.Service[Auth with Repository with Clock] {

  override def signup(credential: Credential) = ZIO accessM (_.auth signup credential)

  override def login(credential: Credential) = ZIO accessM (_.auth login credential)

  override def changePassword(credentialSecretUpdate: CredentialSecretUpdateForm) =
    ZIO accessM (_.auth changePassword credentialSecretUpdate)

  override def signout(credential: Credential) = ZIO accessM (_.auth signout credential)

  override def decode(token: Token) = ZIO accessM (_.auth decode token)

  type Token            = String
  type CredentialId     = String
  type CredentialSecret = String
  type UserId           = Int
  type PublicKey        = String
  type PrivateKey       = String
  type Timestamp        = Long

}
