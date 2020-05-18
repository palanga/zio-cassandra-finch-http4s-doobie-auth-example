package thewho

object error {

  sealed trait AppException extends Exception with Product with Serializable

  /**
   * An expected app error: a kind of error that we would like to map to a status code more specific than a 500.
   */
  sealed trait AppError extends AppException

  /**
   * An app defect: something unexpected happened. These are bugs.
   */
  sealed trait AppDefect extends AppException with HasCause

  trait HasCause extends Exception {
    def cause: Throwable
    override def getMessage = s"${cause.getClass.getName}: ${cause.getMessage}"
  }

  sealed trait DatabaseException extends AppException
  object DatabaseException {

    sealed trait DatabaseError extends AppError with DatabaseException
    object DatabaseError {
      case object UserNotFound           extends DatabaseError // TODO maybe this is a defect
      case object CredentialNotFound     extends DatabaseError
      case object CredentialAlreadyExist extends DatabaseError
    }

    case class DatabaseDefect(cause: Throwable) extends AppDefect with DatabaseException

  }

  sealed trait AuthException extends AppException
  object AuthException {

    sealed trait AuthError extends AppError with AuthException
    object AuthError {
      case object Forbidden                     extends AuthError
      case class TokenExpired(cause: Throwable) extends AuthError with HasCause
    }

    sealed trait AuthDefect extends AppDefect with AuthException
    object AuthDefect {
      case class JwtClaimDefect(cause: Throwable)    extends AuthDefect
      case class JwtEncodeDefect(cause: Throwable)   extends AuthDefect
      case class JwtDecodeDefect(cause: Throwable)   extends AuthDefect
      case class CirceDecodeDefect(cause: Throwable) extends AuthDefect
    }

  }

}
