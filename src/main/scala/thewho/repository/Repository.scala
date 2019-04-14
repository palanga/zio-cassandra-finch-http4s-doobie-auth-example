package thewho.repository

import scalaz.zio.{TaskR, ZIO}
import thewho.auth.{AuthId, AuthSecret, Credential, CredentialId}

trait Repository {
  val repository: Repository.Service[Any]
}

object Repository {

  trait Service[R] {

    def findAuthSecret(authId: AuthId): TaskR[R, AuthSecret]

    def findCredentialId(authId: AuthId): TaskR[R, CredentialId]

    def findCredential(authId: AuthId): TaskR[R, Credential]

  }

  trait Test extends Repository {

    private val AUTH_ID_TO_AUTH_SECRET = Map[AuthId, AuthSecret](
      "420606" -> "skrik",
      "475711" -> "impression"
    )

    private val AUTH_ID_TO_CREDENTIAL_ID = Map[AuthId, CredentialId](
      "420606" -> 7
    )

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

    }

  }

  object Test extends Test

}
