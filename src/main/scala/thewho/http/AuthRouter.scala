package thewho.http

import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.CirceEntityEncoder._
import scalaz.zio.Task
import scalaz.zio.interop.catz._
import thewho.TestEnv
import thewho.auth._
import thewho.http.syntax.zio._

// TODO #11 rename to controller ?
object AuthRouter {

  // TODO #9 improve error handling
  // TODO #10 make it TaskR so we can provide the R at a higher level
  val authRouter: HttpRoutes[Task] = HttpRoutes.of[Task] {
    case req @ (GET | POST) -> Root / "login" =>
      req decode [Credential] (login(_).provide(TestEnv) >>= (Ok(_)))
    case req @ POST -> Root / "signup" =>
      req decode [Credential] (signup(_).provide(TestEnv) >>= (Ok(_)))
  }

}
