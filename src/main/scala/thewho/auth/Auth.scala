package thewho.auth

import java.util.concurrent.TimeUnit.SECONDS

import io.circe.generic.auto._
import io.circe.parser.{ decode => circeDecode }
import io.circe.syntax._
import pdi.jwt.{ Jwt, JwtAlgorithm, JwtClaim }
import scalaz.zio.clock.{ currentTime, Clock }
import scalaz.zio.{ TaskR, ZIO }
import thewho.Constants
import thewho.repository.{ createCredential, findUser, Repository }

trait Auth {
  val auth: Auth.Service[Repository with Clock]
}

object Auth {

  // TODO #6 create live implementation
  trait Service[R] {

    def login(credential: Credential): TaskR[R, Token]

    def signup(credential: Credential): TaskR[R, Token]

    def decode(token: Token): TaskR[R, TokenContent]

  }

  trait Test extends Auth {

    private val PRIVATE_KEY = Constants.PRIVATE_KEY_TEST
    private val PUBLIC_KEY  = Constants.PUBLIC_KEY_TEST
    private val ALGORITHM   = JwtAlgorithm.RS256
    private val TTL         = 60 // 60 seconds

    override val auth = new Service[Repository with Clock] {

      override def login(credential: Credential) = findUser(credential.id) >>= (_ validate credential) >>= createToken

      override def signup(credential: Credential) = createCredential(credential).map(_.id) >>= createToken

      def createToken(userId: UserId): ZIO[Clock, Throwable, String] =
        for {
          currentTime <- currentTime(SECONDS)
          claim       <- ZIO effect JwtClaim(content = TokenContent(userId, currentTime + TTL).asJson.noSpaces)
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
