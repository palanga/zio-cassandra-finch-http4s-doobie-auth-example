package thewho.database.cassandra

import com.datastax.oss.driver.api.core.cql.SimpleStatement
import thewho.database.cassandra.adapter.ZStatement
import thewho.model.{ CredentialId, UnvalidatedCredential, UserId }

object cql {

  /**
   *  cred_id | cred_secret | user_id
   * ---------|-------------|---------
   *  the.one | ****        | 1
   *  the.fst | ****        | 1
   *  the.two | ****        | 2
   */
  object credentials {

    val credentials = "credentials"
    val cred_id     = "cred_id"
    val cred_secret = "cred_secret"
    val user_id     = "user_id"

    val dropTable =
      SimpleStatement
        .builder(s"""DROP TABLE IF EXISTS $credentials;""")
        .build()

    val createTable =
      SimpleStatement
        .builder(
          s"""
             |CREATE TABLE IF NOT EXISTS $credentials (
             |  $cred_id text,
             |  $cred_secret text,
             |  $user_id int,
             |  PRIMARY KEY ($cred_id)
             |);
             |""".stripMargin
        )
        .build()

    private val insertStatement =
      ZStatement(s"INSERT INTO $credentials ($cred_id, $cred_secret, $user_id) VALUES (?,?,?) IF NOT EXISTS;")

    def insert(credential: UnvalidatedCredential, userId: UserId) =
      insertStatement.bind(credential.id, credential.secret, userId)

    private val selectStatement =
      ZStatement(s"SELECT * FROM $credentials WHERE $cred_id=?;")

    def select(credentialId: CredentialId) =
      selectStatement
        .decode(row => (row.getString(0), row.getString(1), row.getInt(2)))
        .bind(credentialId)

    private val deleteStatement =
      ZStatement(s"DELETE FROM $credentials WHERE $cred_id=?;")

    def delete(credentialId: CredentialId) =
      deleteStatement.bind(credentialId)

  }

  /**
   *  user_id | cred_id
   * ---------|---------
   *  1       | the.one
   *  1       | the.fst
   *  2       | the.two
   */
  object credentials_by_user {

    val credentials_by_user = "credentials_by_user"
    val user_id             = "user_id"
    val cred_id             = "cred_id"

    val dropTable =
      SimpleStatement
        .builder(s"""DROP TABLE IF EXISTS $credentials_by_user;""")
        .build()

    val createTable =
      SimpleStatement
        .builder(
          s"""
             |CREATE TABLE IF NOT EXISTS $credentials_by_user (
             |  $user_id int,
             |  $cred_id text,
             |  PRIMARY KEY ($user_id)
             |);
             |""".stripMargin
        )
        .build()

    private val insertStatement =
      ZStatement(s"INSERT INTO $credentials_by_user ($user_id, $cred_id) VALUES (?,?) IF NOT EXISTS;")

    def insert(userId: UserId, credentialId: CredentialId) =
      insertStatement.bind(userId, credentialId)

    private val selectStatement =
      ZStatement(s"SELECT * FROM $credentials_by_user WHERE $user_id=?")

    def select(userId: UserId) =
      selectStatement
        .decode(row => (row.getInt(0), row.getString(1)))
        .bind(userId)

    private val deleteStatement =
      ZStatement(s"DELETE FROM $credentials_by_user WHERE $user_id=?;")

    def delete(userId: UserId) =
      deleteStatement.bind(userId)

  }

}
