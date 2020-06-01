package thewho.database.cassandra

import java.net.InetSocketAddress

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.{ Row, Statement }
import thewho.database.module.AuthDatabase
import thewho.error.DatabaseException
import thewho.error.DatabaseException.DatabaseDefect
import thewho.error.DatabaseException.DatabaseError.CredentialNotFound
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
      .toManaged(_.shutDown.catchAll(t => putStrLn("Error trying to close cassandra session: " + t.getMessage)))
      .toLayer

  private type Decoder[T]           = Row => T
  private type CredentialsRow       = (CredentialId, CredentialId, UserId)
  private type CredentialsByUserRow = (UserId, CredentialId)

  private def decode[T](row: Row)(implicit decode: Decoder[T])          = ZIO effect decode(row) mapError DatabaseDefect
  private implicit def credDecoder: Decoder[CredentialsRow]             = row => (row.getString(0), row.getString(1), row.getInt(2))
  private implicit def credByUserDecoder: Decoder[CredentialsByUserRow] = row => (row.getInt(0), row.getString(1))

}

class CassandraAuthDatabase(session: CqlSession) extends AuthDatabase.Service {

  import CassandraAuthDatabase._

  private val shutDown =
    putStrLn("closing cassandra session...") *> (ZIO effect session.close()) <* putStrLn("closed cassandra session")

  private def execute[T <: Statement[T]](s: Statement[T]) = ZIO effect (session execute s) mapError DatabaseDefect

  override def createUser(credential: UnvalidatedCredential): IO[DatabaseException, User] = ???

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

}
