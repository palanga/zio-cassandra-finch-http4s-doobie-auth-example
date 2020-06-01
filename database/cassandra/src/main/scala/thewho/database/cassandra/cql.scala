package thewho.database.cassandra

import com.datastax.oss.driver.api.core.cql.SimpleStatement
import thewho.error.DatabaseException.DatabaseDefect
import thewho.model.{ CredentialId, UserId }
import zio.ZIO

object cql {

  // TODO simple statement
  def createKeyspace(keyspace: String) =
    s"""
       |CREATE KEYSPACE $keyspace
       |  WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'};
       |""".stripMargin

  /**
   *  cred_id | cred_secret | user_id
   * ---------|-------------|---------
   *  the.one | ****        | 1
   *  the.fst | ****        | 1
   *  the.two | ****        | 2
   */
  val createCredentialsTableIfNotExists =
    """
      |CREATE TABLE IF NOT EXISTS credentials (
      |  cred_id text,
      |  cred_secret text,
      |  user_id int,
      |  PRIMARY KEY (cred_id)
      |);
      |""".stripMargin

  /**
   *  user_id | cred_id
   * ---------|---------
   *  1       | the.one
   *  1       | the.fst
   *  2       | the.two
   */
  val createCredentialsByUserTableIfNotExists =
    """
      |CREATE TABLE IF NOT EXISTS credentials_by_user (
      |  user_id int,
      |  cred_id text,
      |  PRIMARY KEY (user_id)
      |);
      |""".stripMargin

  def selectFromCredentialsWhere(credentialId: CredentialId) =
    ZIO
      .effect(
        SimpleStatement
          .builder("SELECT * FROM credentials WHERE cred_id=?")
          .addPositionalValue(credentialId)
          .build
      )
      .mapError(DatabaseDefect)

}
