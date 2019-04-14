package thewho.auth

import io.circe.generic.auto._
import io.circe.parser.{decode => circeDecode}
import io.circe.syntax._
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import scalaz.zio.{TaskR, ZIO}
import thewho.Constants
import thewho.repository.{Repository, findCredential}

trait Auth {
  val auth: Auth.Service[Repository]
}

object Auth {

  trait Service[R] {

    def login(authInfo: AuthInfo, currentTime: Timestamp): TaskR[R, Token]

    def decode(token: Token): TaskR[R, TokenContent]

  }

  trait Test extends Auth {

    private val PRIVATE_KEY = Constants.PRIVATE_KEY_TEST
    private val PUBLIC_KEY = Constants.PUBLIC_KEY_TEST
    private val ALGORITHM = JwtAlgorithm.RS256
    private val TTL = 5 // 5 seconds

    override val auth = new Service[Repository] {

      override def login(authInfo: AuthInfo, currentTime: Timestamp) = (
        findCredential(authInfo.id)
          >>= (_ validate authInfo)
          >>= createToken(currentTime + TTL)
        )

      private def createToken(expirationTime: Timestamp)(credentialId: CredentialId) = for {
        claim <- ZIO effect JwtClaim(content = TokenContent(credentialId, expirationTime).asJson.noSpaces)
      } yield Jwt encode(claim, PRIVATE_KEY, ALGORITHM)

      override def decode(token: Token) = for {
        stringToken  <- ZIO fromTry Jwt.decode(token, PUBLIC_KEY, ALGORITHM :: Nil)
        tokenContent <- ZIO fromEither circeDecode[TokenContent](stringToken)
      } yield tokenContent

    }

  }

  object Test extends Test

}
