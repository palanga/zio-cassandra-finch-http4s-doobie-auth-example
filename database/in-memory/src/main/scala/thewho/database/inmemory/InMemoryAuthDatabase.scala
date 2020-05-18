package thewho.database.inmemory

import thewho.database.module.AuthDatabase
import thewho.error.DatabaseException.DatabaseError.{ CredentialAlreadyExist, CredentialNotFound, UserNotFound }
import thewho.model._
import zio.ULayer
import zio.stm.{ STM, TMap, TRef }

object InMemoryAuthDatabase {
  val make: ULayer[AuthDatabase] = {
    for {
      credentials       <- TMap.empty[CredentialId, (CredentialId, CredentialSecret, UserId)]
      credentialsByUser <- TMap.empty[UserId, (UserId, CredentialId)]
      userIdCounter     <- TRef make 0
    } yield new InMemoryAuthDatabase(credentials, credentialsByUser, userIdCounter)
  }.commit.toLayer
}

/**
 *
 *  cred_id | cred_secret | user_id
 * ---------|-------------|---------
 *  the.one | ****        | 1
 *  the.fst | ****        | 1
 *  the.two | ****        | 2
 *
 *  CREATE TABLE IF NOT EXISTS credentials (
 *     cred_id text,
 *     cred_secret text,
 *     user_id uuid,
 *     PRIMARY KEY (cred_id)
 * );
 *
 *  user_id | cred_id
 * ---------|---------
 *  1       | the.one
 *  1       | the.fst
 *  2       | the.two
 *
 *  CREATE TABLE IF NOT EXISTS credentials_by_user (
 *     user_id uuid,
 *     cred_id text,
 *     PRIMARY KEY (user_id)
 * );
 *
 */
private final class InMemoryAuthDatabase(
  credentials: TMap[CredentialId, (CredentialId, CredentialSecret, UserId)],
  credentialsByUser: TMap[UserId, (UserId, CredentialId)],
  userIdCounter: TRef[UserId],
) extends AuthDatabase.Service {

  override def createUser(credential: UnvalidatedCredential) =
    STM
      .fail(CredentialAlreadyExist)
      .whenM(credentials contains credential.id)
      .zipRight(userIdCounter updateAndGet (_ + 1))
      .tap(userId => credentialsByUser put (userId, (userId, credential.id)))
      .tap(userId => credentials put (credential.id, (credential.id, credential.secret, userId)))
      .commit
      .map(User(_, Credential(credential.id, credential.secret)))

  override def findUser(credentialId: CredentialId) =
    credentials
      .get(credentialId)
      .someOrFail(CredentialNotFound)
      .map { case (_, credSecret, userId) => User(userId, Credential(credentialId, credSecret)) }
      .commit

  override def deleteUser(userId: UserId) =
    credentialsByUser
      .get(userId)
      .someOrFail(UserNotFound)
      .flatMap(credentialsByUser delete userId as _._2)
      .flatMap(credentials.delete)
      .commit
      .as(userId)

  override def findCredentialId(userId: UserId) =
    credentialsByUser
      .get(userId)
      .someOrFail(UserNotFound)
      .flatMap { case (_, credId) => credentials get credId }
      .someOrFail(CredentialNotFound)
      .commit
      .map(_._1)

  override def updateCredential(credential: Credential) =
    credentials
      .get(credential.id)
      .someOrFail(CredentialNotFound)
      .map { case (_, _, userId) => (credential.id, credential.secret, userId) }
      .flatMap(newRow => credentials.merge(credential.id, newRow)((_, _) => newRow))
      .commit
      .as(credential)

}
