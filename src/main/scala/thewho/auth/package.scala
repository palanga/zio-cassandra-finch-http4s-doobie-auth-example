package thewho

import scalaz.zio.ZIO
import scalaz.zio.clock.Clock
import thewho.error.{ AppException, AuthFailure }
import thewho.repository.Repository

package object auth extends Auth.Service[Auth with Repository with Clock] {

  type AuthR = Auth with Repository with Clock

  override def signup(credential: Credential): ZIO[AuthR, AppException, Token] =
    ZIO accessM (_.auth signup credential)

  override def login(credential: Credential): ZIO[AuthR, AppException, Token] =
    ZIO accessM (_.auth login credential)

  override def changePassword(form: CredentialSecretUpdateForm): ZIO[AuthR, AppException, Token] =
    ZIO accessM (_.auth changePassword form)

  override def signout(credential: Credential): ZIO[AuthR, AppException, Unit] =
    ZIO accessM (_.auth signout credential)

  override def decode(token: Token): ZIO[AuthR, AuthFailure, TokenContent] =
    ZIO accessM (_.auth decode token)

  type Token            = String
  type CredentialId     = String
  type CredentialSecret = String
  type UserId           = Int
  type PublicKey        = String
  type PrivateKey       = String
  type Timestamp        = Long

}
