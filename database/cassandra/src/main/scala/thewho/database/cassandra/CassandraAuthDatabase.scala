package thewho.database.cassandra

import java.net.InetSocketAddress

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.uuid.Uuids
import thewho.database.cassandra.adapter.{ CassandraException, ZCqlSession }
import thewho.database.module.AuthDatabase
import thewho.error.DatabaseException
import thewho.error.DatabaseException.DatabaseDefect
import thewho.error.DatabaseException.DatabaseError.{
  CredentialAlreadyExist,
  CredentialNotFound,
  UserAlreadyExist,
  UserNotFound,
}
import thewho.model._
import zio.console.{ putStrLn, Console }
import zio.{ IO, ZIO, ZLayer }

object CassandraAuthDatabase {

  private val host       = "127.0.0.1"
  private val port       = 9042
  private val address    = new InetSocketAddress(host, port)
  private val keyspace   = "thewho_dummy"
  private val datacenter = "datacenter1"

  val dummy: ZLayer[Console, CassandraException, AuthDatabase] =
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
      .map(new CassandraAuthDatabase(_))
      .tap(_ => putStrLn("Cassandra connected and initialized").toManaged_)
      .tapError(t => putStrLn("Failed trying to build cassandra layer: " + t.getMessage).toManaged_)
      .toLayer

  private def closeSession(session: ZCqlSession) =
    (putStrLn("Closing cassandra session...") *> session.close <* putStrLn("Closed cassandra session"))
      .catchAll(t => putStrLn("Failed trying to close cassandra session:\n" + t.getMessage))

  private def initialize(session: ZCqlSession) =
    session.executeSimplePar(cql.dropCredentialsTable, cql.dropCredentialsByUserTable) *>
      session.executeSimplePar(cql.createCredentialsTable, cql.createCredentialsByUserTable)

}

private final class CassandraAuthDatabase(session: ZCqlSession) extends AuthDatabase.Service {

  override def createUser(credential: UnvalidatedCredential): IO[DatabaseException, User] = {
    val userId = Uuids.timeBased().hashCode()
    session
      .execute(queries.insertIntoCredentials(credential, userId))
      .mapError(DatabaseDefect)
      .flatMap(ZIO fail CredentialAlreadyExist when !_.wasApplied()) *>
      session
        .execute(queries.insertIntoCredentialsByUser(userId, credential.id))
        .mapError(DatabaseDefect)
        .flatMap(ZIO fail UserAlreadyExist when !_.wasApplied())
        .as(User(userId, Credential(credential.id, credential.secret)))
  }

  override def findCredentialId(userId: UserId): IO[DatabaseException, CredentialId] =
    session
      .executeHeadOption(queries.selectAllFromCredentialsByUser(userId))
      .mapError(DatabaseDefect)
      .someOrFail(UserNotFound)
      .map(_._2)

  override def findUser(credentialId: CredentialId): IO[DatabaseException, User]           =
    session
      .executeHeadOption(queries.selectAllFromCredentials(credentialId))
      .mapError(DatabaseDefect)
      .someOrFail(CredentialNotFound)
      .map { case (credentialId, credentialSecret, userId) => User(userId, Credential(credentialId, credentialSecret)) }

  override def updateCredential(credential: Credential): IO[DatabaseException, Credential] = ???

  override def deleteUser(userId: UserId): IO[DatabaseException, UserId] =
    findCredentialId(userId)
      .flatMap(credId => session.execute(queries.deleteFromCredentials(credId)).mapError(DatabaseDefect))
      .flatMap(ZIO fail CredentialNotFound when !_.wasApplied())
      .zipRight(session.execute(queries.deleteFromCredentialsByUser(userId)).mapError(DatabaseDefect))
      .flatMap(ZIO fail UserNotFound when !_.wasApplied())
      .as(userId)

}
