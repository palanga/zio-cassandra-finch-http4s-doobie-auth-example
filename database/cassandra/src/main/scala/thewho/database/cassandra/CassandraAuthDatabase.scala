package thewho.database.cassandra

import java.net.InetSocketAddress

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.uuid.Uuids
import thewho.database.module.AuthDatabase
import thewho.error.DatabaseException
import thewho.error.DatabaseException.DatabaseDefect
import thewho.error.DatabaseException.DatabaseError.{ CredentialAlreadyExist, CredentialNotFound, UserAlreadyExist }
import thewho.model._
import zio.console.{ putStrLn, Console }
import zio.{ IO, RLayer, ZIO }

object CassandraAuthDatabase {

  private val host       = "127.0.0.1"
  private val port       = 9042
  private val address    = new InetSocketAddress(host, port)
  private val keyspace   = "thewho_dummy"
  private val datacenter = "datacenter1"

  val dummy: RLayer[Console, AuthDatabase] =
    ZIO
      .effect(
        CqlSession
          .builder()
          .addContactPoint(address)
          .withKeyspace(keyspace)
          .withLocalDatacenter(datacenter)
          .build()
      )
      .map(CassandraAuthDatabase.fromCqlSession)
      .tap(_.initialize)
      .toManaged(_.close.catchAll(t => putStrLn("Failed trying to close cassandra session:\n" + t.getMessage)))
      .toLayer

  private def fromCqlSession(cqlSession: CqlSession): CassandraAuthDatabase =
    new CassandraAuthDatabase(new ZioCqlSession(cqlSession))

}

class CassandraAuthDatabase(session: ZioCqlSession) extends AuthDatabase.Service {

  import thewho.database.cassandra.codec._

  override def createUser(credential: UnvalidatedCredential): IO[DatabaseException, User] = {
    val userId = Uuids.timeBased().hashCode()
    cql
      .insertCredentialIfNotExists(credential, userId)
      .flatMap(session.execute(_) mapError DatabaseDefect)
      .flatMap(ZIO fail CredentialAlreadyExist when !_.wasApplied()) *>
      cql
        .insertCredentialByUserIfNotExists(userId, credential.id)
        .flatMap(session.execute(_) mapError DatabaseDefect)
        .flatMap(ZIO fail UserAlreadyExist when !_.wasApplied())
        .as(User(userId, Credential(credential.id, credential.secret)))
  }

  override def findUser(credentialId: CredentialId): IO[DatabaseException, User] =
    cql
      .selectFromCredentialsWhere(credentialId)
      .flatMap(session.executeHead(_) mapError DatabaseDefect)
      .someOrFail(CredentialNotFound)
      .flatMap(decode[CredentialsRow])
      .map { case (credId, credSecret, userId) => User(userId, Credential(credId, credSecret)) }

  override def deleteUser(userId: UserId): IO[DatabaseException, UserId]         = ???

  override def findCredentialId(userId: UserId): IO[DatabaseException, CredentialId] = ???

  override def updateCredential(credential: Credential): IO[DatabaseException, Credential] = ???

  private val close =
    putStrLn("closing cassandra session...") *> session.close <* putStrLn("closed cassandra session")

  private def initialize = dropTables *> createTables

  private def dropTables =
    cql.dropCredentialsTable.flatMap(session.execute) &> cql.dropCredentialsByUserTable.flatMap(session.execute)

  private def createTables =
    cql.createCredentialsTable.flatMap(session.execute) &> cql.createCredentialsByUserTable.flatMap(session.execute)

}
