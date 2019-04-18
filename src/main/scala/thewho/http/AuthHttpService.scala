package thewho.http

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s.HttpService
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.io._
import scalaz.syntax.id._
import thewho.auth._
import thewho.zioToCatsIO

object AuthHttpService {

  case class TokenWrapper(token: Token)

  case class AuthIdWrapper(authId: AuthId)

  val authHttpService = HttpService[IO] {
    case req @ (GET | POST) -> Root / "login" =>
      req decode[PhoneAuth] (login(_).map(TokenWrapper) |> zioToCatsIO |> (Ok(_)))
    case req @ POST -> Root / "signup" =>
      req decode[PhoneAuth] (signup(_).map(TokenWrapper) |> zioToCatsIO |> (Ok(_)))
    case req @ (GET | POST) -> Root / "me" =>
      req decode[TokenWrapper] ((tw: TokenWrapper) => me(tw.token).map(AuthIdWrapper) |> zioToCatsIO |> (Ok(_)))
  }

}
