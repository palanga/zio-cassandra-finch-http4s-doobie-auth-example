package thewho.database.cassandra

import com.datastax.oss.driver.api.core.cql.PreparedStatement
import thewho.database.cassandra.codec.Decoder
import thewho.error.DatabaseException
import thewho.error.DatabaseException.DatabaseDefect
import thewho.error.DatabaseException.DatabaseError.{ CredentialNotFound, UserAlreadyExist }
import thewho.model.{ CredentialId, UnvalidatedCredential, UserId }
import zio.{ IO, ZIO }

object Credentials {
  type CredentialsRow = (CredentialId, CredentialId, UserId)
  implicit val credDecoder: Decoder[CredentialsRow] = row => (row.getString(0), row.getString(1), row.getInt(2))
}

final class Credentials(
  zCqlSession: ZCqlSession,
  insertCredential: PreparedStatement,
  selectCredential: PreparedStatement,
) {

  import Credentials._

  def insert(credential: UnvalidatedCredential, userId: UserId): IO[DatabaseException, Unit] =
    zCqlSession
      .execute(insertCredential.bind(credential.id, credential.secret, userId))
      .mapError(DatabaseDefect)
      .flatMap(ZIO fail UserAlreadyExist when !_.wasApplied())

  def select(credentialId: CredentialId): IO[DatabaseException, CredentialsRow] =
    zCqlSession
      .executeHead(selectCredential.bind(credentialId))
      .mapError(DatabaseDefect)
      .someOrFail(CredentialNotFound)
      .flatMap(codec.decode[CredentialsRow])

}
