package thewho

import scalaz.zio.clock.Clock
import scalaz.zio.{ Task, ZIO }
import thewho.repository.Repository

package object auth extends Auth.Service[Auth with Repository with Clock] {

  override def login(authInfo: AuthInfo) = ZIO accessM (_.auth login authInfo)

  override def signup(authInfo: AuthInfo) = ZIO accessM (_.auth signup authInfo)

  override def me(token: Token) = ZIO accessM (_.auth me token)

  override def decode(token: Token) = ZIO accessM (_.auth decode token)

  type Token        = String
  type AuthId       = String
  type AuthSecret   = String
  type CredentialId = Int
  type PublicKey    = String
  type PrivateKey   = String
  type Timestamp    = Long

  trait AuthInfo {

    def id: AuthId

    def secret: AuthSecret

  }

  case class Credential(id: CredentialId, authInfo: AuthInfo) {

    def validate(thatAuthInfo: AuthInfo) =
      if (authInfo == thatAuthInfo) Task succeed id
      else Task fail new Exception("Couldn't validate credential")

  }

  object Credential {

    def from(credentialId: CredentialId, authId: AuthId, secret: AuthSecret) =
      Credential(credentialId, PhoneAuth(authId, secret))

  }

}
