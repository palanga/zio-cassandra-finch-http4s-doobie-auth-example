package thewho.auth

import scalaz.zio.ZIO
import scalaz.zio.clock.Clock
import thewho.error.{ AppException, AuthFailure }
import thewho.repository.Repository

trait Auth {
  val auth: Auth.Service[Repository with Clock]
}

object Auth {

  // TODO #6 create live implementation
  trait Service[R] {

    def signup(credential: Credential): ZIO[R, AppException, Token]

    def login(credential: Credential): ZIO[R, AppException, Token]

    def changePassword(credentialSecretUpdate: CredentialSecretUpdateForm): ZIO[R, AppException, Token]

    def signout(credential: Credential): ZIO[R, AppException, Unit]

    def decode(token: Token): ZIO[R, AuthFailure, TokenContent]

  }

}
