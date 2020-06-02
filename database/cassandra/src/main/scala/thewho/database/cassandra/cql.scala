package thewho.database.cassandra

import com.datastax.oss.driver.api.core.cql.SimpleStatement

object cql {

  val credentials         = "credentials"
  val credentials_by_user = "credentials_by_user"
  val cred_id             = "cred_id"
  val cred_secret         = "cred_secret"
  val user_id             = "user_id"

  // TODO simple statement
  def createKeyspace(keyspace: String) =
    s"""
       |CREATE KEYSPACE $keyspace
       |  WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'};
       |""".stripMargin

  val dropCredentialsTable =
    SimpleStatement
      .builder(s"""DROP TABLE IF EXISTS $credentials;""")
      .build()

  val dropCredentialsByUserTable =
    SimpleStatement
      .builder(s"""DROP TABLE IF EXISTS $credentials_by_user;""")
      .build()

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
        s"""
          |CREATE TABLE IF NOT EXISTS $credentials_by_user (
          |  $user_id int,
          |  $cred_id text,
          |  PRIMARY KEY ($user_id)
          |);
          |""".stripMargin
      )
      .build()

  val insertIntoCredentials =
    SimpleStatement
      .builder(s"INSERT INTO $credentials ($cred_id, $cred_secret, $user_id) VALUES (?,?,?) IF NOT EXISTS;")
      .build()

  val selectFromCredentials =
    SimpleStatement
      .builder(s"SELECT * FROM $credentials WHERE $cred_id=:$cred_id")
      .build()

  val insertIntoCredentialsByUser =
    SimpleStatement
      .builder(s"INSERT INTO $credentials_by_user ($user_id, $cred_id) VALUES (?,?) IF NOT EXISTS;")
      .build()

}
