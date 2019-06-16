package thewho.auth

import java.util.concurrent.TimeUnit.SECONDS

import io.circe.generic.auto._
import io.circe.parser.{ decode => circeDecode }
import io.circe.syntax._
import pdi.jwt.{ Jwt, JwtAlgorithm, JwtClaim }
import scalaz.zio.clock.{ currentTime, Clock }
import scalaz.zio.{ IO, ZIO }
import thewho.Constants
import thewho.auth.Auth.Service
import thewho.error._
import thewho.repository._

trait TestAuth extends Auth {

  private val PRIVATE_KEY = Constants.PRIVATE_KEY_TEST
  private val PUBLIC_KEY  = Constants.PUBLIC_KEY_TEST
  private val ALGORITHM   = JwtAlgorithm.RS256
  private val TTL         = 60 // 60 seconds

  override val auth = new Service[Repository with Clock] {

    override def signup(credential: Credential): ZIO[Repository with Clock, AppException, Token] =
      createUser(credential).map(_.id) >>= createToken

    private def createToken(userId: UserId): ZIO[Clock, AuthFailure, Token] =
      for {
        currentTime <- currentTime(SECONDS)
        claim       <- ZIO effect JwtClaim(content = TokenContent(userId, currentTime + TTL).asJson.noSpaces) mapError JWTClaimFailure
        token       <- ZIO effect (Jwt encode (claim, PRIVATE_KEY, ALGORITHM)) mapError JWTEncodeFailure
      } yield token

    override def login(credential: Credential): ZIO[Repository with Clock, AppException, Token] =
      findUser(credential.id) >>= (_ validate credential) >>= createToken

    override def changePassword(form: CredentialSecretUpdateForm): ZIO[Repository with Clock, AppException, Token] =
      form match {
        case CredentialSecretUpdateForm(credential, newSecret) =>
          for {
            user  <- findUser(credential.id)
            _     <- user validate credential
            _     <- updateCredential(credential copy (secret = newSecret))
            token <- createToken(user.id)
          } yield token
      }

    override def signout(credential: Credential): ZIO[Repository, AppException, Unit] =
      for {
        user <- findUser(credential.id)
        _    <- user validate credential
        _    <- deleteUser(user.id)
      } yield ()

    override def decode(token: Token): IO[AuthFailure, TokenContent] =
      for {
        stringToken  <- ZIO fromTry Jwt.decode(token, PUBLIC_KEY, ALGORITHM :: Nil) mapError JWTDecodeFailure
        tokenContent <- ZIO fromEither circeDecode[TokenContent](stringToken) mapError CirceDecodeFailure
      } yield tokenContent

  }

}
