package thewho.http

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s.HttpService
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.io._
import scalaz.syntax.id._
import thewho.auth.{PhoneAuth, Token, login}
import thewho.zioToCatsIO

object AuthHttpService {

  case class TokenResponse(token: Token)

  val authHttpService = HttpService[IO] {
    case GET -> Root / "login" / id / secret =>
      login(PhoneAuth(id, secret), 1234l).map(TokenResponse) |> zioToCatsIO |> (Ok(_))
    case req@POST -> Root / "login" =>
      req decode[PhoneAuth] (login(_, 1234l).map(TokenResponse) |> zioToCatsIO |> (Ok(_)))
    case GET -> Root / "signup" / id / secret =>
      Map("response" -> "not implemented") |> (Ok(_))
  }

}
