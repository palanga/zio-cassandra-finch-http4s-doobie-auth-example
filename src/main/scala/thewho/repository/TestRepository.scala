package thewho.repository

import java.util.concurrent.atomic.AtomicInteger

import scalaz.zio.ZIO
import thewho.auth._
import Repository.Service

trait TestRepository extends Repository {

  import scala.collection.mutable.{ Map => MutableMap }

  private val CREDENTIAL_ID_TO_CREDENTIAL_SECRET = MutableMap[CredentialId, CredentialSecret](
    "420606" -> "skrik",
    "475711" -> "impression"
  )

  private val CREDENTIAL_ID_TO_USER_ID = MutableMap[CredentialId, UserId](
    "420606" -> 0
  )

  private val USER_ID_TO_CREDENTIAL_ID = MutableMap[UserId, CredentialId](
    0 -> "420606"
  )

  private val COUNTER = new AtomicInteger(0)

  override val repository = new Service[Any] {

    override def findCredentialSecret(credentialId: CredentialId) =
      ZIO
        .fromOption(CREDENTIAL_ID_TO_CREDENTIAL_SECRET get credentialId)
        .mapError(_ => new Exception(s"Couldn't find secret for credential id $credentialId"))

    override def findUserId(credentialId: CredentialId) =
      ZIO
        .fromOption(CREDENTIAL_ID_TO_USER_ID get credentialId)
        .mapError(_ => new Exception(s"Couldn't find credential id for credential id $credentialId"))

    override def findUser(credentialId: CredentialId) =
      for {
        secret <- findCredentialSecret(credentialId)
        userId <- findUserId(credentialId)
      } yield User from (userId, credentialId, secret)

    override def createCredential(credential: Credential) =
      for {
        _ <- findUser(credential.id).flip.mapError(_ => new Exception(s"Credential id ${credential.id} already exists"))
      } yield {
        val userId = COUNTER.incrementAndGet()
        CREDENTIAL_ID_TO_CREDENTIAL_SECRET += (credential.id -> credential.secret)
        CREDENTIAL_ID_TO_USER_ID += (credential.id           -> userId)
        User(userId, credential)
      }

  }

}
