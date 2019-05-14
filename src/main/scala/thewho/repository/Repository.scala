package thewho.repository

import scalaz.zio.TaskR
import thewho.auth._

trait Repository {
  val repository: Repository.Service[Any]
}

object Repository {

  // TODO #4 create a live implementation
  trait Service[R] {

    def findCredentialSecret(credentialId: CredentialId): TaskR[R, CredentialSecret]

    def findUserId(credentialId: CredentialId): TaskR[R, UserId]

    def findUser(credentialId: CredentialId): TaskR[R, User]

    def createCredential(credential: Credential): TaskR[R, User]

  }

  trait Test  extends TestRepository
  object Test extends Test

}
