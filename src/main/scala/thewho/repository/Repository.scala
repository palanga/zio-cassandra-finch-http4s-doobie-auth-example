package thewho.repository

import scalaz.zio.ZIO
import thewho.auth._
import thewho.repository.error.{ RepositoryException, RepositoryFailure }

trait Repository {
  val repository: Repository.Service[Any]
}

object Repository {

  trait Service[R] {

    type RepoServiceTask[A] = ZIO[R, RepositoryException, A]

    def heartBeat: ZIO[R, RepositoryFailure, Unit]

    def createUser(credential: Credential): RepoServiceTask[User]

    def findUser(userId: UserId): RepoServiceTask[User]

    def findUser(credentialId: CredentialId): RepoServiceTask[User]

    def deleteUser(userId: UserId): RepoServiceTask[UserId]

    def createCredential(credential: Credential, userId: UserId): RepoServiceTask[Credential]

    def findCredential(credentialId: CredentialId): RepoServiceTask[Credential]

    def findCredential(userId: UserId): RepoServiceTask[Credential]

    def updateCredential(credential: Credential): RepoServiceTask[Credential]

    def deleteCredential(credentialId: CredentialId): RepoServiceTask[CredentialId]

  }

}
