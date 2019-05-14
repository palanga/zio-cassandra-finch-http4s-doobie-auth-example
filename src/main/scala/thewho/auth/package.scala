package thewho

import scalaz.zio.clock.Clock
import scalaz.zio.ZIO
import thewho.repository.Repository

package object auth extends Auth.Service[Auth with Repository with Clock] {

  override def login(credential: Credential) = ZIO accessM (_.auth login credential)

  override def signup(credential: Credential) = ZIO accessM (_.auth signup credential)

  override def decode(token: Token) = ZIO accessM (_.auth decode token)

  type Token            = String
  type CredentialId     = String
  type CredentialSecret = String
  type UserId           = Int
  type PublicKey        = String
  type PrivateKey       = String
  type Timestamp        = Long

}
