package thewho.database.cassandra

import com.datastax.oss.driver.api.core.cql.PreparedStatement
import thewho.error.DatabaseException.DatabaseDefect
import zio.ZIO

object Queries {
  def apply(zCqlSession: ZCqlSession)(statements: List[PreparedStatement]) =
    statements match {
      case insertCredential :: selectCredential :: insertCredentialByUser :: Nil =>
        ZIO succeed new Queries(
          new Credentials(zCqlSession, insertCredential, selectCredential),
          new CredentialsByUser(zCqlSession, insertCredentialByUser),
        )
      case _                                                                     =>
        ZIO fail DatabaseDefect(new IllegalArgumentException("Invalid number of statements"))
    }
}

final class Queries(val credentials: Credentials, val credentialsByUser: CredentialsByUser)
