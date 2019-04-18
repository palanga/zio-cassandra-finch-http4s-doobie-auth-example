package thewho.repository

import java.util.concurrent.atomic.AtomicInteger

import scalaz.zio.{TaskR, ZIO}
import thewho.auth.{AuthId, AuthInfo, AuthSecret, Credential, CredentialId}

trait Repository {
  val repository: Repository.Service[Any]
}

object Repository {

  trait Service[R] {

    def findAuthSecret(authId: AuthId): TaskR[R, AuthSecret]

    def findCredentialId(authId: AuthId): TaskR[R, CredentialId]

    def findCredential(authId: AuthId): TaskR[R, Credential]

    def findCredential(credentialId: CredentialId): TaskR[R, AuthId]

    def createCredential(authInfo: AuthInfo): TaskR[R, Credential]

  }

  trait Test extends Repository {

    import scala.collection.mutable.{Map => MutableMap}

    private val AUTH_ID_TO_AUTH_SECRET = MutableMap[AuthId, AuthSecret](
      "420606" -> "skrik",
      "475711" -> "impression"
    )

    private val AUTH_ID_TO_CREDENTIAL_ID = MutableMap[AuthId, CredentialId](
      "420606" -> 0
    )

    private val CREDENTIAL_ID_TO_AUTH_ID = MutableMap[CredentialId, AuthId](
       0 -> "420606"
    )

    private val COUNTER = new AtomicInteger(0)

    override val repository = new Service[Any] {

      override def findAuthSecret(authId: AuthId) = ZIO
        .fromOption(AUTH_ID_TO_AUTH_SECRET get authId)
        .mapError(_ => new Exception(s"Couldn't find secret for auth id $authId"))

      override def findCredentialId(authId: AuthId) = ZIO
        .fromOption(AUTH_ID_TO_CREDENTIAL_ID get authId)
        .mapError(_ => new Exception(s"Couldn't find credential id for auth id $authId"))

      override def findCredential(authId: AuthId) = for {
        secret       <- findAuthSecret(authId)
        credentialId <- findCredentialId(authId)
      } yield Credential from(credentialId, authId, secret)

      override def findCredential(credentialId: CredentialId) = ZIO
        .fromOption(CREDENTIAL_ID_TO_AUTH_ID get credentialId)
        .mapError(_ => new Exception(s"Couldn't find credential $credentialId"))

      override def createCredential(authInfo: AuthInfo) = for {
        _ <- findCredential(authInfo.id).flip.mapError(_ => new Exception(s"Auth id ${authInfo.id} already exists"))
      } yield {
        val credId = COUNTER.incrementAndGet()
        AUTH_ID_TO_AUTH_SECRET += (authInfo.id -> authInfo.secret)
        AUTH_ID_TO_CREDENTIAL_ID += (authInfo.id -> credId)
        Credential(credId, authInfo)
      }

    }

  }

  object Test extends Test

}
