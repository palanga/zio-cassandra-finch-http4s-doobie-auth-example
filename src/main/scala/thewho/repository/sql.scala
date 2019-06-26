package thewho.repository

import doobie.implicits._
import thewho.auth.{ Credential, CredentialId, UserId }

object sql {

  final val heartBeat = sql"""SELECT true"""

  object user {

    /**
     * This should only be used for testing purposes
     */
    def createTable =
      sql"""
           |CREATE TABLE users
           |(
           |    id SERIAL PRIMARY KEY
           |)
         """.stripMargin

    /**
      * This should only be used for testing purposes
      */
    def dropTable = sql"""DROP TABLE IF EXISTS users"""

    def create(credential: Credential) =
      sql"""
           |with aux as (
           |  INSERT INTO users DEFAULT VALUES RETURNING id
           |)
           |INSERT INTO credentials (id, secret, user_id)
           |SELECT ${credential.id}, ${credential.secret}, id FROM aux RETURNING user_id
           """.stripMargin

    def find(credentialId: CredentialId) = sql"""SELECT (user_id) FROM credentials WHERE id = $credentialId"""

    def find(userId: UserId) = sql"""SELECT id FROM users WHERE id = $userId"""

    def delete(userId: UserId) = sql"""DELETE FROM users WHERE id = $userId"""

  }

  object credential {

    /**
     * This should only be used for testing purposes
     */
    def createTable =
      sql"""
           |CREATE TABLE credentials
           |(
           |    id      VARCHAR PRIMARY KEY,
           |    secret  VARCHAR,
           |    user_id INT references users (id)
           |)
         """.stripMargin

    /**
      * This should only be used for testing purposes
      */
    def dropTable = sql"""DROP TABLE IF EXISTS credentials"""

    def create(credential: Credential)(userId: UserId) =
      sql"""
           |INSERT INTO credentials (id, secret, user_id)
           |VALUES (${credential.id}, ${credential.secret}, $userId)
           |""".stripMargin

    def find(credentialId: CredentialId) =
      sql"""SELECT * FROM credentials WHERE credentials.id = $credentialId"""

    def find(userId: UserId) = sql"""SELECT * FROM credentials WHERE credentials.user_id = $userId"""

    def update(credential: Credential) =
      sql"""UPDATE credentials SET secret = ${credential.secret} WHERE id = ${credential.id}"""

    def delete(credentialId: CredentialId) = sql"""DELETE FROM credentials WHERE id = $credentialId"""

  }

}
