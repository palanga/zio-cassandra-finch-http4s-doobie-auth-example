package thewho.auth

import pdi.jwt.algorithms.JwtAsymmetricAlgorithm
import pdi.jwt.exceptions.JwtExpirationException
import pdi.jwt.{ Jwt, JwtClaim }
import thewho.auth.CirceZioAdapter.decodeString
import thewho.error.AuthException.AuthDefect
import thewho.error.AuthException.AuthDefect.{ JwtClaimDefect, JwtDecodeDefect, JwtEncodeDefect }
import thewho.error.AuthException.AuthError.TokenExpired
import thewho.error.{ AuthException, HasCause }
import thewho.model._
import zio.{ IO, ZIO }

// TODO use java security and eliminate pdi jwt dependency
object JwtZioFacade {

  def createToken(
    privateKey: PrivateKey,
    algorithm: JwtAsymmetricAlgorithm,
    tokenContent: TokenContent,
  ): IO[AuthDefect, Token] = {
    import io.circe.generic.auto._
    import io.circe.syntax._
    for {
      claim <- ZIO effect JwtClaim(content = tokenContent.asJson.noSpaces) mapError JwtClaimDefect
      token <- ZIO effect Jwt.encode(claim, privateKey, algorithm) mapError JwtEncodeDefect
    } yield token
  }

  def decode(
    publicKey: PublicKey,
    algorithm: JwtAsymmetricAlgorithm,
    token: Token,
  ): IO[AuthException with HasCause, TokenContent] = {
    import io.circe.generic.auto._

    def decodeAndMapError =
      ZIO
        .fromTry(Jwt.decode(token, publicKey, algorithm :: Nil))
        .mapError {
          case t: JwtExpirationException => TokenExpired(t)
          case t                         => JwtDecodeDefect(t)
        }

    // It seems like the decoded jwtClaim put the _exp_ property apart from the content
    case class HasId(id: UserId)
    def expirationNotPresent = JwtDecodeDefect(new NoSuchElementException("exp (expiration)"))

    for {
      jwtClaim <- decodeAndMapError
      hasId    <- decodeString[HasId](jwtClaim.content)
      exp      <- ZIO succeed jwtClaim.expiration someOrFail expirationNotPresent
    } yield TokenContent fromExpiration (exp, hasId.id)

  }

}
