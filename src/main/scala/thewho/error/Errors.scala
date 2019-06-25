package thewho.error

sealed trait AppException extends Exception

/**
 * A "recoverable" error: a kind of error that we would like to map to a status code more specific than a 500.
 */
sealed trait AppError extends AppException

/**
 * A "unrecoverable" failure: a kind of failure that we wouldn't want to handle, and we would let the http layer to
 * map to a 500.
 */
sealed trait AppFailure extends AppException

trait HasCause {
  def cause: Throwable
}

//
//  REPOSITORY ERRORS
//

sealed trait RepositoryException extends AppException

sealed trait RepositoryError       extends AppError with RepositoryException
case object UserNotFound           extends RepositoryError
case object CredentialNotFound     extends RepositoryError
case object CredentialAlreadyExist extends RepositoryError

sealed trait RepositoryFailure                       extends AppFailure with RepositoryException
case object NoCredentialsCreated                     extends RepositoryFailure
case class CommonRepositoryFailure(cause: Throwable) extends RepositoryFailure with HasCause

//
//  AUTH ERRORS
//

sealed trait AuthException extends AppException

sealed trait AuthError extends AppError with AuthException
case object Forbidden  extends AuthError

sealed trait AuthFailure                        extends AppFailure with AuthException
case class JWTClaimFailure(cause: Throwable)    extends AuthFailure with HasCause
case class JWTEncodeFailure(cause: Throwable)   extends AuthFailure with HasCause
case class JWTDecodeFailure(cause: Throwable)   extends AuthFailure with HasCause
case class CirceDecodeFailure(cause: Throwable) extends AuthFailure with HasCause
