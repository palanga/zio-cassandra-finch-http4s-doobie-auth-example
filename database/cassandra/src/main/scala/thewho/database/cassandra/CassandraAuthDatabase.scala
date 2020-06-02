package thewho.database.cassandra

import java.net.InetSocketAddress

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.uuid.Uuids
import thewho.database.module.AuthDatabase
import thewho.error.DatabaseException
import thewho.error.DatabaseException.DatabaseDefect
import thewho.model._
import zio.console.{ putStrLn, Console }
import zio.{ IO, RLayer }

object CassandraAuthDatabase {

  private val host       = "127.0.0.1"
  private val port       = 9042
  private val address    = new InetSocketAddress(host, port)
  private val keyspace   = "thewho_dummy"
  private val datacenter = "datacenter1"

  val dummy: RLayer[Console, AuthDatabase] =
    putStrLn("Opening cassandra connection...")
      .zipRight(
        ZCqlSession(
          CqlSession
            .builder()
            .addContactPoint(address)
            .withKeyspace(keyspace)
            .withLocalDatacenter(datacenter)
            .build()
        )
      )
      .toManaged(closeSession(_).fork)
      .tap(_ => putStrLn("Initializing cassandra...").toManaged_)
      .tap(initialize(_).toManaged_)
      .flatMap(prepareQueries(_).toManaged_)
      .map(new CassandraAuthDatabase(_))
      .tap(_ => putStrLn("Cassandra connected and initialized").toManaged_)
      .tapError(t => putStrLn("Failed trying to build cassandra layer: " + t.getMessage).toManaged_)
      .toLayer

  private def closeSession(session: ZCqlSession) =
    (putStrLn("Closing cassandra session...") *> session.close <* putStrLn("Closed cassandra session"))
      .catchAll(t => putStrLn("Failed trying to close cassandra session:\n" + t.getMessage))

  private def initialize(session: ZCqlSession) =
    session.executePar(cql.dropCredentialsTable, cql.dropCredentialsByUserTable) *>
      session.executePar(cql.createCredentialsTable, cql.createCredentialsByUserTable)

  private def prepareQueries(zCqlSession: ZCqlSession) =
    zCqlSession
      .preparePar(cql.insertIntoCredentials, cql.selectFromCredentials, cql.insertIntoCredentialsByUser)
      .mapError(DatabaseDefect)
      .flatMap(Queries(zCqlSession))

}

private final class CassandraAuthDatabase(queries: Queries) extends AuthDatabase.Service {

  override def createUser(credential: UnvalidatedCredential): IO[DatabaseException, User] = {
    val userId = Uuids.timeBased().hashCode()
    queries.credentials
      .insert(credential, userId) *>
      queries.credentialsByUser
        .insert(userId, credential.id)
        .as(User(userId, Credential(credential.id, credential.secret)))
  }

  override def findUser(credentialId: CredentialId): IO[DatabaseException, User] =
    queries.credentials
      .select(credentialId)
      .map { case (credId, credSecret, userId) => User(userId, Credential(credId, credSecret)) }

  override def deleteUser(userId: UserId): IO[DatabaseException, UserId]         = ???

  override def findCredentialId(userId: UserId): IO[DatabaseException, CredentialId] = ???

  override def updateCredential(credential: Credential): IO[DatabaseException, Credential] = ???

}
