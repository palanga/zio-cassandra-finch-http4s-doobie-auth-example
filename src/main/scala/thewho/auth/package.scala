package thewho

import scalaz.zio.{Task, ZIO}
import thewho.repository.Repository

package object auth extends Auth.Service[Auth with Repository] {

  override def login(authInfo: AuthInfo, currentTime: Timestamp) =
    ZIO accessM (_.auth login(authInfo, currentTime))

  override def decode(token: Token) = ZIO accessM (_.auth decode token)

  type Token = String
  type AuthId = String
  type AuthSecret = String
  type CredentialId = Int
  type PublicKey = String
  type PrivateKey = String
  type Timestamp = Long

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
