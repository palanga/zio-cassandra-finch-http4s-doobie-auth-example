package thewho.http

import org.http4s.Response
import scalaz.zio.Task
import scalaz.zio.interop.catz._
import thewho.AppTask
import thewho.error._

object ExceptionHandler {

  import thewho.http.Routes.dsl._

  def all(t: Throwable): AppTask[Response[AppTask]] = absolveAppErrors(t) >>= appErrorToStatusCode

  private def absolveAppErrors(t: Throwable): Task[AppError] = t match {
    case e: AppError => Task succeed e
    case _           => Task fail t
  }

  private def appErrorToStatusCode(error: AppError): AppTask[Response[AppTask]] = error match {
    case e: RepositoryError => repositoryErrorResponseMapper(e)
    case e: AuthError       => authErrorResponseMapper(e)
  }

  private def repositoryErrorResponseMapper(error: RepositoryError): AppTask[Response[AppTask]] = error match {
    case UserNotFound | CredentialNotFound         => NotFound()
    case UserAlreadyExist | CredentialAlreadyExist => Conflict()
  }

  private def authErrorResponseMapper(e: AuthError): AppTask[Response[AppTask]] = e match {
    case thewho.error.Forbidden => Forbidden()
  }

}
