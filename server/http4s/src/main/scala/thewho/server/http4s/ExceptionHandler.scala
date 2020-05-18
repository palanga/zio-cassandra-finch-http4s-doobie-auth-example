package thewho.server.http4s

import org.http4s.Challenge
import org.http4s.headers.`WWW-Authenticate`
import thewho.error.AuthException.AuthError
import thewho.error.DatabaseException.DatabaseError
import thewho.error.DatabaseException.DatabaseError.{ CredentialAlreadyExist, CredentialNotFound, UserNotFound }
import thewho.error._
import zio.Task
import zio.interop.catz._

object ExceptionHandler {

  import dsl._

  def all(t: Throwable) = absolveAppErrors(t) >>= appErrorResponseMapper

  private def absolveAppErrors(t: Throwable) =
    t match {
      case e: AppError => Task succeed e
      case _           => Task fail t
    }

  private def appErrorResponseMapper(e: AppError) =
    e match {
      case e: DatabaseError => databaseErrorResponseMapper(e)
      case e: AuthError     => authErrorResponseMapper(e)
    }

  private def databaseErrorResponseMapper(e: DatabaseError) =
    e match {
      case UserNotFound | CredentialNotFound => NotFound()
      case CredentialAlreadyExist            => Conflict()
    }

  private def authErrorResponseMapper(e: AuthError) =
    e match {
      case AuthError.Forbidden           => Forbidden()
      case AuthError.TokenExpired(cause) =>
        Unauthorized(`WWW-Authenticate`(Challenge("Bearer", "the-who", Map("error_description" -> cause.getMessage))))
    }

}
