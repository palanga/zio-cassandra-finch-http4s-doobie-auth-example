package thewho.auth

import java.util.concurrent.TimeUnit.SECONDS

import pdi.jwt.JwtAlgorithm
import pdi.jwt.algorithms.JwtAsymmetricAlgorithm
import thewho.constants.{ PRIVATE_KEY_TEST, PUBLIC_KEY_TEST }
import thewho.database.module.AuthDatabase
import thewho.database.{ module => db }
import thewho.error.AuthException.AuthError.Forbidden
import thewho.error.{ AppException, AuthException, HasCause }
import thewho.model._
import zio.clock.Clock
import zio.{ clock, IO, ZIO }

object Authenticator {

  private val testConfig = new Config(PRIVATE_KEY_TEST, PUBLIC_KEY_TEST, JwtAlgorithm.RS256, 60L) // 60 seconds

  val make = new Authenticator(testConfig)

}

private final class Config(
  val privateKey: PrivateKey,
  val publicKey: PublicKey,
  val algorithm: JwtAsymmetricAlgorithm,
  val ttl: Seconds, // TODO time unit
)

final class Authenticator private (config: Config) {

  def signup(credential: UnvalidatedCredential): ZIO[Clock with AuthDatabase, AppException, Token] =
    for {
      // TODO
      user        <- db createUser credential
      now         <- clock currentTime SECONDS
      tokenContent = TokenContent fromIssued (config.ttl, now, user.id)
      token       <- JwtZioFacade createToken (config.privateKey, config.algorithm, tokenContent)
    } yield token

  def login(credential: UnvalidatedCredential): ZIO[Clock with AuthDatabase, AppException, Token] =
    for {
      user        <- db findUser credential.id
      _           <- validate(credential, user)
      now         <- clock currentTime SECONDS
      tokenContent = TokenContent fromIssued (config.ttl, now, user.id)
      token       <- JwtZioFacade createToken (config.privateKey, config.algorithm, tokenContent)
    } yield token

  def changePassword(
    oldCredential: UnvalidatedCredential,
    newSecret: CredentialSecret,
  ): ZIO[Clock with AuthDatabase, AppException, Token] =
    for {
      user         <- db findUser oldCredential.id
      _            <- validate(oldCredential, user)
      newCredential = Credential(oldCredential.id, newSecret)
      _            <- db updateCredential newCredential
      now          <- clock currentTime SECONDS
      tokenContent  = TokenContent fromIssued (config.ttl, now, user.id)
      token        <- JwtZioFacade createToken (config.privateKey, config.algorithm, tokenContent)
    } yield token

  def signout(credential: UnvalidatedCredential): ZIO[AuthDatabase, AppException, Unit] =
    for {
      user <- db findUser credential.id
      _    <- validate(credential, user)
      _    <- db deleteUser user.id
    } yield ()

  def findCredentialId(token: Token): ZIO[AuthDatabase, AppException, CredentialId] =
    decode(token) flatMap (db findCredentialId _.id)

  private[auth] def decode(token: Token): IO[AuthException with HasCause, TokenContent] =
    JwtZioFacade decode (config.publicKey, config.algorithm, token)

  private def validate(credential: UnvalidatedCredential, user: User): IO[Forbidden.type, UserId] = {
    val isValid = credential isSame user.credential
    ZIO fail Forbidden when !isValid as user.id
  }

}
