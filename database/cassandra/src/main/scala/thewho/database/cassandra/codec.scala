package thewho.database.cassandra

import com.datastax.oss.driver.api.core.cql.Row
import thewho.error.DatabaseException.DatabaseDefect
import thewho.model.{ CredentialId, UserId }
import zio.ZIO

object codec {

  private type Decoder[T]           = Row => T

  type CredentialsRow       = (CredentialId, CredentialId, UserId)
  type CredentialsByUserRow = (UserId, CredentialId)

  // TODO do not map error
  def decode[T](row: Row)(implicit decode: Decoder[T])          = ZIO effect decode(row) mapError DatabaseDefect
  implicit def credDecoder: Decoder[CredentialsRow]             = row => (row.getString(0), row.getString(1), row.getInt(2))
  implicit def credByUserDecoder: Decoder[CredentialsByUserRow] = row => (row.getInt(0), row.getString(1))
  implicit def booleanDecoder: Decoder[Boolean]                 = row => row.getBoolean(0)

}
