package thewho.repository

import scalaz.zio.TaskR
import thewho.auth._

trait Repository {
  val repository: Repository.Service[Any]
}

object Repository {

  trait Service[R] {

    def heartBeat: TaskR[R, Unit]

    def createUser(credential: Credential): TaskR[R, User]

    def findUser(userId: UserId): TaskR[R, User]

    def findUser(credentialId: CredentialId): TaskR[R, User]

    def deleteUser(userId: UserId): TaskR[R, UserId]

    def createCredential(userId: UserId, credential: Credential): TaskR[R, Credential]

    def findCredential(credentialId: CredentialId): TaskR[R, Credential]

    def findCredential(userId: UserId): TaskR[R, Credential]

    def updateCredential(credential: Credential): TaskR[R, Credential]

    def deleteCredential(credentialId: CredentialId): TaskR[R, CredentialId]

  }

}
