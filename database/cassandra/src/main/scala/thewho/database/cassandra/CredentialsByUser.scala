package thewho.database.cassandra

import com.datastax.oss.driver.api.core.cql.PreparedStatement
import thewho.database.cassandra.codec.Decoder
import thewho.error.DatabaseException
import thewho.error.DatabaseException.DatabaseDefect
import thewho.error.DatabaseException.DatabaseError.CredentialAlreadyExist
import thewho.model.{ CredentialId, UserId }
import zio.{ IO, ZIO }

object CredentialsByUser {
  type CredentialsByUserRow = (UserId, CredentialId)
  implicit def credByUserDecoder: Decoder[CredentialsByUserRow] = row => (row.getInt(0), row.getString(1))
}

final class CredentialsByUser(
  zCqlSession: ZCqlSession,
  insertCredentialByUser: PreparedStatement,
) {

  def insert(userId: UserId, credentialId: CredentialId): IO[DatabaseException, Boolean] =
    zCqlSession
      .execute(insertCredentialByUser.bind(userId, credentialId))
      .map(_.wasApplied())
      .mapError(DatabaseDefect)
      .tap(ZIO fail CredentialAlreadyExist when !_)

}
