package thewho.http

import cats.data.Kleisli
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.{ HttpRoutes, Request, Response }
import scalaz.zio.interop.catz._
import thewho.AppTask
import thewho.auth._

object Routes {

  object dsl extends Http4sDsl[AppTask]
  import dsl._

  val authRoutes: HttpRoutes[AppTask] = HttpRoutes.of[AppTask] {
    case req @ POST -> Root / "signup" =>
      req decode [Credential](signup(_).map(TokenResponse).foldM(ExceptionHandler.all, Created(_)))
    case req @ (GET | POST) -> Root / "login" =>
      req decode [Credential](login(_).map(TokenResponse).foldM(ExceptionHandler.all, Ok(_)))
    case req @ POST -> Root / "change-password" =>
      req decode [CredentialSecretUpdateForm](changePassword(_).map(TokenResponse).foldM(ExceptionHandler.all, Ok(_)))
    case req @ POST -> Root / "signout" =>
      req decode [Credential](signout(_).foldM(ExceptionHandler.all, Ok(_)))
  }

  val all: Kleisli[AppTask, Request[AppTask], Response[AppTask]] = authRoutes.orNotFound

}
