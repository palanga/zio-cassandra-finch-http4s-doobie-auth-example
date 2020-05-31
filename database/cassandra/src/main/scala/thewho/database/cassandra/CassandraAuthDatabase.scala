package thewho.database.cassandra

import thewho.database.module.AuthDatabase
import thewho.error.DatabaseException
import thewho.model._
import zio.blocking.Blocking
import zio.{ IO, ZLayer }

object CassandraAuthDatabase {

  val host = "127.0.0.1"
  val port = 9042

  val make: ZLayer[Blocking, Throwable, AuthDatabase] = ???

}

class CassandraAuthDatabase extends AuthDatabase.Service {

  override def createUser(credential: UnvalidatedCredential): IO[DatabaseException, User] = ???

  override def findUser(credentialId: CredentialId): IO[DatabaseException, User] = ???

  override def deleteUser(userId: UserId): IO[DatabaseException, UserId] = ???

  override def findCredentialId(userId: UserId): IO[DatabaseException, CredentialId] = ???

  override def updateCredential(credential: Credential): IO[DatabaseException, Credential] = ???

}
