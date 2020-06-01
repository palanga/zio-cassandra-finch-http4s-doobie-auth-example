package thewho.database.cassandra

import com.datastax.oss.driver.api.core.cql.{ SimpleStatement, Statement }
import thewho.error.DatabaseException.DatabaseDefect
import thewho.model.{ CredentialId, UnvalidatedCredential, UserId }
import zio.{ IO, ZIO }

object cql {

  // TODO simple statement
  def createKeyspace(keyspace: String) =
    s"""
       |CREATE KEYSPACE $keyspace
       |  WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'};
       |""".stripMargin

  val dropCredentialsTable =
    SimpleStatement
      .builder("""DROP TABLE IF EXISTS credentials;""")
      .build()
      .toZIO

  val dropCredentialsByUserTable =
    SimpleStatement
      .builder("""DROP TABLE IF EXISTS credentials_by_user;""")
      .build()
      .toZIO

  /**
   *  cred_id | cred_secret | user_id
   * ---------|-------------|---------
   *  the.one | ****        | 1
   *  the.fst | ****        | 1
   *  the.two | ****        | 2
   */
  val createCredentialsTable =
    SimpleStatement
      .builder(
        """
          |CREATE TABLE IF NOT EXISTS credentials (
          |  cred_id text,
          |  cred_secret text,
          |  user_id int,
          |  PRIMARY KEY (cred_id)
          |);
          |""".stripMargin
      )
      .build()
      .toZIO

  /**
   *  user_id | cred_id
   * ---------|---------
   *  1       | the.one
   *  1       | the.fst
   *  2       | the.two
   */
  val createCredentialsByUserTable =
    SimpleStatement
      .builder(
        """
          |CREATE TABLE IF NOT EXISTS credentials_by_user (
          |  user_id int,
          |  cred_id text,
          |  PRIMARY KEY (user_id)
          |);
          |""".stripMargin
      )
      .build()
      .toZIO

  def insertCredentialIfNotExists(credential: UnvalidatedCredential, userId: UserId) =
    SimpleStatement
      .builder("INSERT INTO credentials (cred_id , cred_secret , user_id) VALUES (?,?,?) IF NOT EXISTS;")
      .addPositionalValues(credential.id, credential.secret, userId)
      .build()
      .toZIO

  def insertCredentialByUserIfNotExists(userId: UserId, credentialId: CredentialId) =
    SimpleStatement
      .builder("INSERT INTO credentials_by_user (user_id , cred_id ) VALUES (?,?) IF NOT EXISTS;")
      .addPositionalValues(userId, credentialId)
      .build()
      .toZIO

  def selectFromCredentialsWhere(credentialId: CredentialId) =
    SimpleStatement
      .builder("SELECT * FROM credentials WHERE cred_id=?")
      .addPositionalValue(credentialId)
      .build
      .toZIO

  implicit class StatementOps[T <: Statement[T]](val self: Statement[T]) extends AnyVal {
    def toZIO: IO[DatabaseDefect, Statement[T]] = ZIO effect self mapError DatabaseDefect
  }

}
