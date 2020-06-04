package thewho.database.cassandra

import thewho.database.cassandra.adapter.ZStatement
import thewho.database.cassandra.cql._
import thewho.model.{ CredentialId, UnvalidatedCredential, UserId }

object queries {

  def insertIntoCredentials(credential: UnvalidatedCredential, userId: UserId) =
    ZStatement(s"INSERT INTO $credentials ($cred_id, $cred_secret, $user_id) VALUES (?,?,?) IF NOT EXISTS;")
      .bind(credential.id, credential.secret, userId)

  def insertIntoCredentialsByUser(userId: UserId, credentialId: CredentialId) =
    ZStatement(s"INSERT INTO $credentials_by_user ($user_id, $cred_id) VALUES (?,?) IF NOT EXISTS;")
      .bind(userId, credentialId)

  def selectAllFromCredentials(credentialId: CredentialId) =
    ZStatement(s"SELECT * FROM $credentials WHERE $cred_id=?;")
      .decode(row => (row.getString(0), row.getString(1), row.getInt(2)))
      .bind(credentialId)

  def selectAllFromCredentialsByUser(userId: UserId) =
    ZStatement(s"SELECT * FROM $credentials_by_user WHERE $user_id=?")
      .decode(row => (row.getInt(0), row.getString(1)))
      .bind(userId)

  def deleteFromCredentials(credentialId: CredentialId) =
    ZStatement(s"DELETE FROM $credentials WHERE $cred_id=?;")
      .bind(credentialId)

  def deleteFromCredentialsByUser(userId: UserId) =
    ZStatement(s"DELETE FROM $credentials_by_user WHERE $user_id=?;")
      .bind(userId)

}
