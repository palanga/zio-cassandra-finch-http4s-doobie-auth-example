package thewho.auth

import java.util.concurrent.TimeUnit.SECONDS

import io.circe.generic.auto._
import io.circe.parser.{ decode => circeDecode }
import io.circe.syntax._
import pdi.jwt.{ Jwt, JwtAlgorithm, JwtClaim }
import scalaz.zio.clock.{ currentTime, Clock }
import scalaz.zio.{ TaskR, ZIO }
import thewho.Constants
import thewho.repository.{ createCredential, findCredential, Repository }

trait Auth {
  val auth: Auth.Service[Repository with Clock]
}

object Auth {

  trait Service[R] {

    def login(authInfo: AuthInfo): TaskR[R, Token]

    def signup(authInfo: AuthInfo): TaskR[R, Token]

    def me(token: Token): TaskR[R, AuthId]

    def decode(token: Token): TaskR[R, TokenContent]

  }

  trait Test extends Auth {

    private val PRIVATE_KEY = Constants.PRIVATE_KEY_TEST
    private val PUBLIC_KEY  = Constants.PUBLIC_KEY_TEST
    private val ALGORITHM   = JwtAlgorithm.RS256
    private val TTL         = 60 // 5 seconds

    override val auth = new Service[Repository with Clock] {

      override def login(authInfo: AuthInfo) = findCredential(authInfo.id) >>= (_ validate authInfo) >>= createToken

      override def signup(authInfo: AuthInfo) = createCredential(authInfo).map(_.id) >>= createToken

      override def me(token: Token) = decode(token).map(_.id) >>= findCredential

      def createToken(credentialId: CredentialId): ZIO[Clock, Throwable, String] =
        for {
          currentTime <- currentTime(SECONDS)
          claim       <- ZIO effect JwtClaim(content = TokenContent(credentialId, currentTime + TTL).asJson.noSpaces)
        } yield Jwt encode (claim, PRIVATE_KEY, ALGORITHM)

      override def decode(token: Token) =
        for {
          stringToken  <- ZIO fromTry Jwt.decode(token, PUBLIC_KEY, ALGORITHM :: Nil)
          tokenContent <- ZIO fromEither circeDecode[TokenContent](stringToken)
        } yield tokenContent

    }

  }

  object Test extends Test

}
