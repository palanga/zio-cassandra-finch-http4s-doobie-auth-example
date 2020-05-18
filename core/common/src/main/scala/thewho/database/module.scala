package thewho.database

import thewho.error.DatabaseException
import thewho.model._
import zio.{ Has, IO, ZIO }

object module {

  type AuthDatabase = Has[AuthDatabase.Service]

  object AuthDatabase {
    trait Service {
      def createUser(credential: UnvalidatedCredential): IO[DatabaseException, User]
      def findUser(credentialId: CredentialId): IO[DatabaseException, User]
      def deleteUser(userId: UserId): IO[DatabaseException, UserId]
      def findCredentialId(userId: UserId): IO[DatabaseException, CredentialId]
      def updateCredential(credential: Credential): IO[DatabaseException, Credential]
    }
  }

  def createUser(credential: UnvalidatedCredential): ZIO[AuthDatabase, DatabaseException, User] =
    ZIO accessM (_.get createUser credential)

  def findUser(credentialId: CredentialId): ZIO[AuthDatabase, DatabaseException, User] =
    ZIO accessM (_.get findUser credentialId)

  def deleteUser(userId: UserId): ZIO[AuthDatabase, DatabaseException, UserId] =
    ZIO accessM (_.get deleteUser userId)

  def findCredentialId(userId: UserId): ZIO[AuthDatabase, DatabaseException, CredentialId] =
    ZIO accessM (_.get findCredentialId userId)

  def updateCredential(credential: Credential): ZIO[AuthDatabase, DatabaseException, Credential] =
    ZIO accessM (_.get updateCredential credential)

}
