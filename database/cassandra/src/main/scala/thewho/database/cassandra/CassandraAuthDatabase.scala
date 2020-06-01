package thewho.database.cassandra

import java.net.InetSocketAddress

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.{ Row, Statement }
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
      .map(new CassandraAuthDatabase(_))
      .tap(_.initialize)
      .toManaged(_.shutDown.catchAll(t => putStrLn("Error trying to close cassandra session: " + t.getMessage)))
      .toLayer

  private type Decoder[T]           = Row => T
  private type CredentialsRow       = (CredentialId, CredentialId, UserId)
  private type CredentialsByUserRow = (UserId, CredentialId)

  private def decode[T](row: Row)(implicit decode: Decoder[T])          = ZIO effect decode(row) mapError DatabaseDefect
  private implicit def credDecoder: Decoder[CredentialsRow]             = row => (row.getString(0), row.getString(1), row.getInt(2))
  private implicit def credByUserDecoder: Decoder[CredentialsByUserRow] = row => (row.getInt(0), row.getString(1))
  private implicit def booleanDecoder: Decoder[Boolean]                 = row => row.getBoolean(0)

}

class CassandraAuthDatabase(session: CqlSession) extends AuthDatabase.Service {

  import CassandraAuthDatabase._

  private def execute[T <: Statement[T]](s: Statement[T]) = ZIO effect (session execute s) mapError DatabaseDefect

  override def createUser(credential: UnvalidatedCredential): IO[DatabaseException, User] = {
    val userId = Uuids.timeBased().hashCode()
    cql
      .insertCredentialIfNotExists(credential, userId)
      .flatMap(execute)
      .map(results => Option(results.one()))
      .someOrFail(DatabaseDefect(new NoSuchElementException("Inserting returned empty")))
      .flatMap(decode[Boolean])
      .flatMap(ZIO fail CredentialAlreadyExist when !_)
      .zipRight(cql insertCredentialByUserIfNotExists (userId, credential.id))
      .flatMap(execute)
      .map(results => Option(results.one()))
      .someOrFail(DatabaseDefect(new NoSuchElementException("Inserting returned empty")))
      .flatMap(decode[Boolean])
      .flatMap(ZIO fail UserAlreadyExist when !_)
      .as(User(userId, Credential(credential.id, credential.secret)))
  }

  override def findUser(credentialId: CredentialId): IO[DatabaseException, User] =
    cql
      .selectFromCredentialsWhere(credentialId)
      .flatMap(execute)
      .map(_.one())
      .map(Option.apply)
      .someOrFail(CredentialNotFound)
      .flatMap(decode[CredentialsRow])
      .map { case (credId, credSecret, userId) => User(userId, Credential(credId, credSecret)) }

  override def deleteUser(userId: UserId): IO[DatabaseException, UserId]         = ???

  override def findCredentialId(userId: UserId): IO[DatabaseException, CredentialId] = ???

  override def updateCredential(credential: Credential): IO[DatabaseException, Credential] = ???

  private val shutDown =
    putStrLn("closing cassandra session...") *> (ZIO effect session.close()) <* putStrLn("closed cassandra session")

  private def initialize = dropTables *> createTables

  private def dropTables =
    cql.dropCredentialsTable.flatMap(execute) &> cql.dropCredentialsByUserTable.flatMap(execute)

  private def createTables =
    cql.createCredentialsTable.flatMap(execute) &> cql.createCredentialsByUserTable.flatMap(execute)

}
