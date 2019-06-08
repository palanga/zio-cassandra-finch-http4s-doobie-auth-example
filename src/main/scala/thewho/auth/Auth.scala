package thewho.auth

import scalaz.zio.TaskR
import scalaz.zio.clock.Clock
import thewho.repository.Repository

trait Auth {
  val auth: Auth.Service[Repository with Clock]
}

object Auth {

  // TODO #6 create live implementation
  trait Service[R] {

    def signup(credential: Credential): TaskR[R, Token]

    def login(credential: Credential): TaskR[R, Token]

    def changePassword(credentialSecretUpdate: CredentialSecretUpdateForm): TaskR[R, Token]

    def signout(credential: Credential): TaskR[R, Unit]

    def decode(token: Token): TaskR[R, TokenContent]

  }

}
