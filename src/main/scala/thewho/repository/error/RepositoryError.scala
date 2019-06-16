package thewho.repository.error

/**
 * An ADT equals to: RepositoryError | RepositoryFailure
 *
 * In scala 3 this will not be necessary any more.
 *
 * {{
 *   def findUser(id: UserId): IO[RepositoryFailure | UserNotFound, User] =
 *     sql"""SELECT * FROM users WHERE users.id = $id""".query[User].option.run
 *       .mapError(CommonRepositoryFailure)
 *       .flatMap{ case Some(user) => IO succeed user; case None => IO fail UserNotFound }
 * }}
 *
 */
sealed trait RepositoryException extends Exception

/**
 * A "recoverable" error: a kind of error that we would like to map to a status code more specific than a 500.
 */
sealed trait RepositoryError extends RepositoryException

sealed trait UserError       extends RepositoryError
case object UserNotFound     extends UserError
case object UserAlreadyExist extends UserError

sealed trait CredentialError       extends RepositoryError
case object CredentialNotFound     extends CredentialError
case object CredentialAlreadyExist extends CredentialError
case object NoCredentialsUpdated   extends CredentialError
case object NoCredentialsCreated   extends CredentialError

/**
 * A "unrecoverable" failure: a kind of failure that we wouldn't want to handle, and we would let the http layer to
 * map to a 500.
 */
sealed trait RepositoryFailure extends RepositoryException {
  def cause: Throwable
}

case class CommonRepositoryFailure(cause: Throwable) extends RepositoryFailure
case class HeartBeatError(cause: Throwable)          extends RepositoryFailure
