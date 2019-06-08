package thewho.http

import cats.data.Kleisli
import io.circe.generic.auto._
import org.http4s.{ HttpRoutes, Request, Response }
import org.http4s.implicits._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import scalaz.zio.interop.catz._
import thewho.AppTask
import thewho.auth._

object Routes {

  object dsl extends Http4sDsl[AppTask]
  import dsl._

  // TODO #9 improve error handling
  val authRoutes: HttpRoutes[AppTask] = HttpRoutes.of[AppTask] {
    case req @ POST -> Root / "signup" =>
      req decode [Credential] (signup(_).map(TokenResponse) >>= (Ok(_)))
    case req @ (GET | POST) -> Root / "login" =>
      req decode [Credential] (login(_).map(TokenResponse) >>= (Ok(_)))
    case req @ POST -> Root / "change-password" =>
      req decode [CredentialSecretUpdateForm] (changePassword(_).map(TokenResponse) >>= (Ok(_)))
    case req @ POST -> Root / "signout" =>
      req decode [Credential] (signout(_) >>= (Ok(_)))
  }

  val all: Kleisli[AppTask, Request[AppTask], Response[AppTask]] = authRoutes.orNotFound

}
