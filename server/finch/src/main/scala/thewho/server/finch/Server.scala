package thewho.server.finch

import com.twitter.finagle.Http
import com.twitter.util.Await
import io.circe.generic.auto._
import io.finch.circe._
import io.finch.{ Endpoint, _ }
import thewho.auth.Authenticator
import thewho.model.{ TokenResponse, UnvalidatedCredential }
import thewho.types.AppEnv
import zio.interop.catz._
import zio.{ Runtime, Task }

/**
 *
 * Usage:
 *
 * object Main extends Server {
 *   val dependencies: ZLayer[Any, Throwable, AuthDatabase] = InMemoryAuthDatabase.make
 *   override implicit val runtime: Runtime[AppEnv] = Runtime.unsafeFromLayer(dependencies ++ ZEnv.live)
 *   start()
 * }
 *
 */
trait Server extends Endpoint.Module[Task] with App {

  implicit val runtime: Runtime[AppEnv]

  private val signup = post("signup" :: jsonBody[UnvalidatedCredential]) { cred: UnvalidatedCredential =>
    Ok(runtime.unsafeRun(Authenticator.make.signup(cred).map(TokenResponse)))
  } handle {
    case e: Exception => BadRequest(e)
    case _            => BadRequest(new Exception())
  }

  def start() = Await.ready(Http.server.serve(":8080", signup.toService))

}
